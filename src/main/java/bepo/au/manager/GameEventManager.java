package bepo.au.manager;

import bepo.au.GameTimer;
import bepo.au.GameTimer.GameType;
import bepo.au.GameTimer.Status;
import bepo.au.Main;
import bepo.au.Main.SETTING;
import bepo.au.base.Mission;
import bepo.au.base.PlayerData;
import bepo.au.base.Sabotage;
import bepo.au.base.Sabotage.SaboType;
import bepo.au.function.*;
import bepo.au.manager.BossBarManager.BossBarList;
import bepo.au.utils.PlayerUtil;
import bepo.au.utils.Util;
import com.destroystokyo.paper.event.player.PlayerStopSpectatingEntityEvent;
import org.bukkit.*;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.spigotmc.event.entity.EntityDismountEvent;

import java.util.List;

public class GameEventManager implements Listener {
	
	@EventHandler
	public void onChat(AsyncPlayerChatEvent event) {
		Player p = event.getPlayer();
		
		PlayerData pd = PlayerData.getPlayerData(p.getName());
		boolean isPlayer = pd != null;
		boolean isAlive = isPlayer && pd.isAlive();
		
		boolean chatAlive = false;
		boolean chatSpec = false;
		
		event.setCancelled(true);

		switch(Main.gt.getStatus()) {
		
		case WORKING:
			if(isAlive) {
				p.sendMessage(Main.PREFIX + "§c일과 중 채팅은 불가합니다.");
				return;
			} else {
				chatSpec = true;
			}
			break;
		case VOTING:
			chatSpec = true;
			if(isAlive) {
				chatAlive = true;
			}
			break;
		default:
			chatAlive = true;
			chatSpec = true;
			break;
		}
		
		String msg = event.getMessage();
		if(isPlayer) {
			msg = pd.getColor().getChatColor() + p.getName() + " §f: " + msg;
			if(!isAlive) msg = "§7[유령] " + msg;
		} else {
			msg = "§7[관전자] §n" + p.getName() + " : " + msg;
		}
		
		for(Player ap : Bukkit.getOnlinePlayers()) {
			boolean alive = PlayerData.getPlayerData(ap.getName()) != null && PlayerData.getPlayerData(ap.getName()).isAlive();
			if(alive && chatAlive) {
				ap.sendMessage(msg);
			} else if(!alive && chatSpec) {
				ap.sendMessage(msg);
			}
		}
		
		Bukkit.getConsoleSender().sendMessage(Main.PREFIX + "§f[ChatLog] " + msg);
	}
	
	
	
	
	
	@EventHandler
	public void onLeave(EntityDismountEvent event) {
		
		if(event.getDismounted() instanceof ArmorStand && event.getEntity() instanceof Player) {
			Player p = (Player) event.getEntity();
			if(PlayerUtil.isSitting(p)) event.getDismounted().remove();
		}
	}
	
	@EventHandler
	public void onDamage(EntityDamageEvent event) {
		if(event.getEntity() instanceof Player) {
			Player p = (Player) event.getEntity();
			if(GameTimer.ALIVE_PLAYERS.contains(p)) event.setCancelled(true);
		}
	}

	
	public void checkKill(EntityDamageByEntityEvent event, Player p, Player e) {
		
		if(Main.gt.getStatus() != Status.WORKING) return;
		
		ItemStack is = p.getInventory().getItemInMainHand();
		if(is != null && is.getType() == ItemList.I_SWORD.getType()) {
			if(GameTimer.ALIVE_PLAYERS.contains(p) && GameTimer.ALIVE_PLAYERS.contains(e)) {
				Util.debugMessage("alive check");
				if(GameTimer.ALIVE_IMPOSTERS.contains(p.getName()) && !GameTimer.ALIVE_IMPOSTERS.contains(e.getName())) {
					Util.debugMessage("kill check");
					PlayerData pd = PlayerData.getPlayerData(p.getName());
					PlayerData ed = PlayerData.getPlayerData(e.getName());
					
					if(pd.getKillCool() > 0) {
						p.sendMessage(Main.PREFIX + "§c킬 쿨타임이 §f" + (pd.getKillCool()/20 + 1) + "§c초 남았습니다.");
					} else {
						pd.resetKillCool(false);
						p.playSound(e.getLocation(), Sound.ENTITY_PLAYER_HURT, 1.0F, 1.0F);
						
						e.playSound(e.getLocation(), Sound.ENTITY_PLAYER_HURT	, 1.0F, 1.0F);
						if(ed.isWatchingCCTV()) ed.exitCCTV(e);
						e.sendTitle("§c§l사망하셨습니다", "§cBy " + pd.getColor().getChatColor() + p.getName(), 0, 100, 20);
						ed.kill(false);
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onDamageByEntity(EntityDamageByEntityEvent event) {
		
		Entity entity = event.getEntity();
		
		if(entity instanceof Zombie) {
			event.setCancelled(true);
			return;
		}
		
		if(entity instanceof ArmorStand) {
			ArmorStand as = (ArmorStand) event.getEntity();
			if(as.getCustomName() != null) {
				String name = as.getCustomName();
				if(Bukkit.getPlayer(name) != null) {
					entity = Bukkit.getPlayer(name);
					event.setCancelled(true);
				}
			}
		}
		
		if(entity instanceof Player && event.getDamager() instanceof Player) {
			Player p = (Player) event.getDamager();
			Player e = (Player) entity;
			
			checkKill(event, p, e);
			
			event.setCancelled(true);
			
		}
	}
	
	@EventHandler
	public void onStopSpectating(PlayerStopSpectatingEntityEvent event) {
		Player p = event.getPlayer();
		PlayerData pd = PlayerData.getPlayerData(p.getName());
		
		if(pd == null) return;
		
		if(Main.gt.getStatus() == Status.WORKING) {
			if(pd.isWatchingCCTV()){
				pd.moveCCTV(p, true);
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onChange(PlayerSwapHandItemsEvent event) {
		
		if(GameTimer.gamemode != GameType.NORMAL) return;
		
		Player p = event.getPlayer();
		PlayerData pd = PlayerData.getPlayerData(p.getName());
		
		if(pd == null) return;
		
		if(Main.gt.getStatus() == Status.WORKING) {
			if(GameTimer.IMPOSTER.contains(p.getName()) && pd.getVent() == null) {
				Sabotage.saboActivate(p);
			}
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onHeld(PlayerItemHeldEvent event) {
		Player p = event.getPlayer();
		PlayerData pd = PlayerData.getPlayerData(p.getName());
		
		if(pd == null) return;
		
		ItemStack is = p.getInventory().getItem(event.getNewSlot());
		
		if(GameTimer.IMPOSTER.contains(p.getName())) {
				if(pd.getVent() != null) {
					if(event.getNewSlot() != 4) {
						pd.nextVent(p, event.getNewSlot() < 4 ? false : true);
						event.setCancelled(true);
					}
				} else if(is != null){
					boolean act = true;
					boolean door = false;
					if(is.getType() == ItemList.I_SABOTAGE_CRIT.getType()) door = false;
					else if(is.getType() == ItemList.I_SABOTAGE_DOOR.getType()) door = true;
					else act = false;
					
					if(act) {
						if(Main.gt.getStatus() == Status.WORKING) pd.nextSabo(p, door);
						event.setCancelled(true);
					}
				}
			
			
			
			
		}
		
		
	}

	
	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		Player p = event.getPlayer();
		PlayerData pd = PlayerData.getPlayerData(p.getName());

		if(event.getHand() != EquipmentSlot.HAND) return;
		
		if (pd == null)
			return;

		if (Main.gt.getStatus() == Status.VOTING) {
			ItemStack is = event.getItem();
			if(is != null) {
				if(is.getType() == ItemList.VOTE_PAPER.getType() && !p.hasPotionEffect(PotionEffectType.BLINDNESS)) {
					VoteSystem.PROGRESSED_VOTE.openGUI(p);
					event.setCancelled(true);
					return;
				}
			}
			
			return;
		} else if (Main.gt.getStatus() == Status.WORKING) {
			ItemStack is = event.getItem();
			if(event.getAction() == Action.LEFT_CLICK_AIR) {
				if(pd.isWatchingCCTV()){
					pd.moveCCTV(p, true);
					event.setCancelled(true);
				}
			} else if(is != null) {
				
				if(is.getType() == ItemList.VOTE_PAPER.getType()) {
					p.sendMessage(Main.PREFIX + "§c투표 시간이 아닙니다.");
					event.setCancelled(true);
					return;
				} else if(is.equals(ItemList.I_VENT_CONFIRM)) {
					if(pd.getVent() != null) {
						pd.confirmVent(p, false);
					}
					event.setCancelled(true);
					return;
				}
			}
			
			if(pd.getVent() != null) {
				event.setCancelled(true);
				return;
			}
			
			boolean blockClick = event.getAction() == Action.RIGHT_CLICK_BLOCK;

			// 1. 클릭한 곳 점검
			if (blockClick) {
				Location loc = event.getClickedBlock().getLocation();
				 // 이러면 다락문도 못열음
				if(Util.getCorpse(loc) != null) {
					if(!SETTING.ENABLE_CORPSE_REPORT.getAsBoolean()) {
						event.setCancelled(true);
						return;
					}
					
					if(PlayerData.getPlayerData(p.getName()) != null && p.getGameMode() != GameMode.SPECTATOR) {
						VoteSystem.start(p.getWorld(), p.getName(), true, Util.getCorpse(loc));
					} else {
						p.sendMessage(Main.PREFIX + "§c시체 리포트는 생존자만 가능합니다.");
					}
					event.setCancelled(true);
				} else if(loc.getBlock().getType() == Material.IRON_TRAPDOOR) {
					if(GameTimer.gamemode != GameType.NORMAL) return;
					if(GameTimer.IMPOSTER.contains(p.getName())) {
						if(loc.distance(p.getLocation().getBlock().getLocation()) > 3.5D) {
							p.sendMessage(Main.PREFIX + "§c벤트와 가까이 붙어주세요.");
						} else {
							Util.toggleDoor(loc);
							p.playSound(p.getLocation(), Sound.BLOCK_IRON_TRAPDOOR_OPEN, 1.0F, 1.0F);
						}
					}
				} else if(LocManager.getLoc("CCTVButton").contains(loc)){
					if(p.getGameMode() != GameMode.SPECTATOR){
						if(Sabotage.isActivating(0) && Sabotage.Sabos.getType() == SaboType.COMM) {
							p.sendMessage(Main.PREFIX + "§c통신 사보타지 발동 중엔 확인하실 수 없습니다.");
						} else {
							pd.moveCCTV(p, true);
						}
					}
				} else if (LocManager.getLoc("EmergencyButton").contains(loc)) {
					if(!pd.isAlive()) {
						p.sendMessage("§c생존자만 긴급 소집이 가능합니다.");
					} else if(pd.getRemainEmerg() <= 0) {
						p.sendMessage("§c당신은 모든 소집 가능 횟수를 소진했습니다.");
					} else if(GameTimer.EMERG_REMAIN_TICK > 0){
						p.sendMessage("§c아직 사용이 불가능합니다. 남은 시간 : §f" + ((GameTimer.EMERG_REMAIN_TICK / 20)+1) + "초");
					} else if(Sabotage.isActivating(0)) {
						p.sendMessage("§c위급 사보타지 발동 중에는 긴급 소집을 할 수 없습니다.");
					} else {
						VoteSystem.start(p.getWorld(), p.getName(), false, null);
						pd.subtractRemainEmerg();
					}
					event.setCancelled(true);
					return;
				} else if(LocManager.getLoc("AdminMap").contains(loc)) {
					AdminMap.openGUI(p);
					event.setCancelled(true);
				} else {
					for (Mission m : pd.getMissions()) {
						List<Integer> list = m.getCode(loc);
						int first = -1;
						if(list.size() > 0) {
							
							for(int i=0;i<list.size();i++) {
								if(!m.isCleared() && !m.isCleared(list.get(i))) {
									first = list.get(i);
									break;
								}
							}
							
							if(first == -1) {
								p.sendMessage(Main.PREFIX + "§c이미 완료한 미션입니다.");
							} else {
								Material material = event.getClickedBlock().getType();

								if(!(material==Material.COMMAND_BLOCK||
								material==Material.CHAIN_COMMAND_BLOCK||
								material==Material.REPEATING_COMMAND_BLOCK)){
									 event.setCancelled(true);
								}
								
								if(!GameTimer.IMPOSTER.contains(p.getName()) || (m instanceof Sabotage)) {
									
									if(m instanceof Sabotage && !pd.isAlive()) {
										return;
									}
									
									if(p.getGameMode() == GameMode.SPECTATOR) {
										final int f = first;
										new BukkitRunnable() {
											public void run() {
												m.onStart(p, f);
											}
										}.runTaskLater(Main.getInstance(), 0L);
									} else {
										m.onStart(p, first);
									}
									
									
								}
							}
						}
					}
				}
				
			}
			
			

			return;
		}

	}

	@EventHandler
	public void onClose(InventoryCloseEvent event) {
		if (event.getPlayer() instanceof Player) {
			Player p = (Player) event.getPlayer();
			PlayerData pd = PlayerData.getPlayerData(p.getName());
			if(pd == null) return;
			for (Mission m : pd.getMissions()) {
				for(String s : m.getTitles()) {
					if(event.getView().getTitle().contains(s)) {
						m.onStop(p, 0);
						return;
					}
				}
				if (m.getTitles().contains(event.getView().getTitle())) {
					m.onStop(p, 0);
					return;
				}
			}
		}
	}

	@EventHandler
	public void onDrop(PlayerDropItemEvent event) {
		Player p = event.getPlayer();

		if (PlayerData.getPlayerData(p.getName()) != null) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onBreak(BlockBreakEvent event) {
		Player p = event.getPlayer();
		if (GameTimer.PLAYERS.contains(p.getName()) && p.getGameMode() != GameMode.CREATIVE)
			event.setCancelled(true);
	}

	@EventHandler
	public void onPlace(BlockPlaceEvent event) {
		Player p = event.getPlayer();
		if (GameTimer.PLAYERS.contains(p.getName()) && p.getGameMode() != GameMode.CREATIVE)
			event.setCancelled(true);
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		Player p = event.getPlayer();
		
		BossBarManager.sendBossBar(BossBarList.TASKS, p);

		if(GameTimer.PLAYERS.contains(p.getName())) {
			PlayerData pd = PlayerData.getPlayerData(p.getName());
			Bukkit.broadcastMessage(Main.PREFIX + pd.getColor().getChatColor() + p.getName() + "§f님께서 재접속하셨습니다.");
			//필요 임무 수 다시 늘리기
			GameTimer.REQUIRED_MISSION += Main.SETTING.COMMON_MISSION_AMOUNT.getAsInteger() + Main.SETTING.EASY_MISSION_AMOUNT.getAsInteger() + Main.SETTING.HARD_MISSION_AMOUNT.getAsInteger();
			//완료 임무 수 다시 늘리기
			GameTimer.CLEARED_MISSION +=pd.cleared_missions;
			BossBarManager.updateBossBar(BossBarList.TASKS, Sabotage.Sabos != null && Sabotage.Sabos.getType() == SaboType.COMM);
		}
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		Player p = event.getPlayer();
		if(GameTimer.PLAYERS.contains(p.getName())) {
			PlayerData pd = PlayerData.getPlayerData(p.getName());

			//필요 임무 수 줄이기(재접속시 늘려야함)
			GameTimer.REQUIRED_MISSION -= Main.SETTING.COMMON_MISSION_AMOUNT.getAsInteger() + Main.SETTING.EASY_MISSION_AMOUNT.getAsInteger() + Main.SETTING.HARD_MISSION_AMOUNT.getAsInteger();
			//완료 임무 수 줄이기(재접속시 늘려야함)
			GameTimer.CLEARED_MISSION -=pd.cleared_missions;
			
			
			BossBarManager.updateBossBar(BossBarList.TASKS, Sabotage.Sabos != null && Sabotage.Sabos.getType() == SaboType.COMM);

			if(pd.isAlive()) {
				if(pd.isWatchingCCTV()) 
					pd.exitCCTV(p);
				else if(pd.getVent() != null) 
					pd.confirmVent(p, true);
				
				pd.kill(false);

				

				if (GameTimer.CLEARED_MISSION == GameTimer.REQUIRED_MISSION) {

					GameTimer.WIN_REASON = GameTimer.WinReason.CREW_MISSION;

				}

				if(Main.gt.getStatus()==GameTimer.Status.VOTING&&!VoteSystem.VOTERS.contains(p.getName())){//투표 중 탈주
					VoteSystem.VOTERS.contains(p.getName());
					Bukkit.broadcastMessage(Main.PREFIX + PlayerData.getPlayerData(p.getName()).getColor().getChatColor() + p.getName()
				+ "§f님께서 중도 탈주로 탈락처리되었습니다. §e(남은 인원 : " + --VoteSystem.remainedVoter + "명)");
					if(VoteSystem.remainedVoter==0){
						VoteSystem.PROGRESSED_VOTE.voteover();
					}
				}else{//기타 상황 중 탈주
					Bukkit.broadcastMessage(Main.PREFIX + pd.getColor().getChatColor() + p.getName() + "§f님께서 중도 탈주로 탈락처리되었습니다.");
				}
			}
		}
	}
	
	@EventHandler
	public void onMove(PlayerMoveEvent event) {

		Player p = event.getPlayer();
		PlayerData pd = PlayerData.getPlayerData(p.getName());
		if(pd == null || !pd.isAlive()) return;
		
		Location to = event.getTo();
		Location from = event.getFrom();
		
		if(pd.getVent() != null) {
			if(to.getX() != from.getX() || to.getZ() != from.getZ()) event.setCancelled(true);
		}
		
		AdminMap.onMove(event);
		String ventname = Vent.check(event.getTo());
		if(ventname != null) {

			Vent v = Vent.getVent(ventname);
			
			if(!GameTimer.IMPOSTER.contains(p.getName())) {
				p.teleport(p.getLocation().add(0, 3, 0));
				return;
			}
			
			Location loc = event.getTo().clone();
			loc.setY(Vent.VENT_Y_VALUE);
			Util.setDoor(loc, false);
			
			PlayerUtil.setInvisible(p, true);
			p.teleport(loc.add(0, 0.5D, 0));
			
			pd.setVent(p, v, loc);
		}
	}

	@EventHandler
	public void onInteractAt(PlayerInteractAtEntityEvent event){
		Player p = event.getPlayer();
		if(event.getRightClicked() instanceof  ArmorStand){
			if(PlayerData.getPlayerData(p.getName()) != null)
				event.setCancelled(true);
		}
	}
	
	@EventHandler(priority=EventPriority.LOW)
	public void onClick(InventoryClickEvent event) {
		
		if(!(event.getWhoClicked() instanceof Player)) return;
		
		if(event.getClickedInventory() == event.getView().getBottomInventory() && event.getWhoClicked().getGameMode() != GameMode.CREATIVE) {
			event.setCancelled(true);
		}
		
		Player p = (Player) event.getWhoClicked();
		
		PlayerData pd = PlayerData.getPlayerData(p.getName());
		
		if(pd != null) {
			if(p.getGameMode() == GameMode.SPECTATOR && !pd.isWatchingCCTV())
				event.setCancelled(false);
			if(pd.isWatchingCCTV() && event.getCurrentItem() != null && event.getCurrentItem().getType() == ItemList.CCTV_EXIT.getType()) {
				pd.exitCCTV(p);
				event.setCancelled(true);
				p.closeInventory();
				return;
			}
		}
		SabotageGUI.onClick(event);
		AdminMap.onClick(event);
	}

}