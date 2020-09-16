package bepo.au.manager;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.EquipmentSlot;

import bepo.au.GameTimer.Status;
import bepo.au.GameTimer;
import bepo.au.Main;
import bepo.au.base.Mission;
import bepo.au.base.PlayerData;
import bepo.au.base.Sabotage;
import bepo.au.base.Sabotage.SaboType;
import bepo.au.function.AdminMap;
import bepo.au.function.ItemList;
import bepo.au.function.Vent;
import bepo.au.function.VoteSystem;
import bepo.au.utils.PlayerUtil;
import bepo.au.utils.Util;

public class EventManager implements Listener {
	
	@EventHandler
	public void onChange(PlayerSwapHandItemsEvent event) {
		Player p = event.getPlayer();
		PlayerData pd = PlayerData.getPlayerData(p.getName());
		
		if(pd == null) return;
		
		if(GameTimer.IMPOSTER.contains(p.getName()) && Main.gt.getStatus() == Status.WORKING) {
			int id = 0;
			if(pd.getSelectedSabo() == SaboType.DOOR) id = pd.getSelectedSaboDoor();
			int tick = Sabotage.saboActivate(pd.getSelectedSabo(), id);
			boolean crit = Sabotage.isActivating(0);
			if(tick == 0 && !crit) {
				
				p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_TRADE, 1.0F, 1.0F);
			} else if(Sabotage.isActivating(0)){
				p.sendMessage(Main.PREFIX + "§c치명적 사보타지 발동 중엔 발동할 수 없습니다.");
			} else if(tick < 0) {
				p.sendMessage(Main.PREFIX + "§c문을 닫았을 때는 치명적 사보타지를 발동할 수 없습니다.");
			} else {
				p.sendMessage(Main.PREFIX + "§f" + (tick / 20 + 1) + "§c초 뒤 발동할 수 있습니다.");
			}
		}
	}
	
	@EventHandler
	public void onHeld(PlayerItemHeldEvent event) {
		Player p = event.getPlayer();
		PlayerData pd = PlayerData.getPlayerData(p.getName());
		
		if(pd == null) return;
		
		if(GameTimer.IMPOSTER.contains(p.getName()) && Main.gt.getStatus() == Status.WORKING) {
			boolean crit = false;
			switch(event.getNewSlot()) {
			case 2: crit = true;
			case 3:
				pd.nextSabo(p, crit);
				event.setCancelled(true);
				break;
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
			

			return;
		} else if (Main.gt.getStatus() == Status.WORKING) {

			if(event.getItem() != null && event.getItem().equals(ItemList.VOTE_PAPER)) {
				p.sendMessage(Main.PREFIX + "§c투표 시간이 아닙니다.");
				event.setCancelled(true);
				return;
			}
			
			
			boolean blockClick = event.getAction() == Action.RIGHT_CLICK_BLOCK;

			// 1. 클릭한 곳 점검
			if (blockClick) {
				Location loc = event.getClickedBlock().getLocation();
				
				if(loc.getBlock().getType() == Material.IRON_TRAPDOOR) {
					if(GameTimer.IMPOSTER.contains(p.getName())) {
						if(loc.distance(p.getLocation().getBlock().getLocation()) > 3.0D) {
							p.sendMessage(Main.PREFIX + "§c벤트와 가까이 붙어주세요.");
						} else {
							Util.toggleDoor(loc);
							p.playSound(p.getLocation(), Sound.BLOCK_IRON_TRAPDOOR_OPEN, 1.0F, 1.0F);
						}
					}
				} else if (LocManager.getLoc("EmergencyButton").contains(loc)) {
					if(pd.getRemainEmerg() <= 0) {
						p.sendMessage("§c당신은 모든 소집 가능 횟수를 소진했습니다.");
					} else if(GameTimer.EMERG_REMAIN_TICK > 0){
						p.sendMessage("§c아직 사용이 불가능합니다. 남은 시간 : §f" + ((GameTimer.EMERG_REMAIN_TICK / 20)+1) + "초");
					} else if(Sabotage.isActivating(0)) {
						p.sendMessage("§c위급 사보타지 발동 중에는 긴급 소집을 할 수 없습니다.");
					} else {
						VoteSystem.start(p.getName(), false);
					}
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
								if(!m.isCleared(list.get(i))) {
									first = list.get(i);
									break;
								}
							}
							
							if(first == -1) {
								p.sendMessage(Main.PREFIX + "§c이미 완료한 미션입니다.");
							} else {
								m.onStart(p, first);
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

			for (Mission m : Mission.MISSIONS) {
				if (m.getTitles().contains(event.getView().getTitle())) {
					m.onStop(p, 0);
					 // stop 시 어떻게 코드를 받아올 것인가?
					break;
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

	//@EventHandler
	public void onBreak(BlockBreakEvent event) {
		Player p = event.getPlayer();
		if (GameTimer.PLAYERS.contains(p.getName()))
			event.setCancelled(true);
	}

	//@EventHandler
	public void onPlace(BlockPlaceEvent event) {
		Player p = event.getPlayer();
		if (GameTimer.PLAYERS.contains(p.getName()))
			event.setCancelled(true);
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		Player p = event.getPlayer();
		if(GameTimer.PLAYERS.contains(p.getName())) {
			PlayerData pd = PlayerData.getPlayerData(p.getName());
			Bukkit.broadcastMessage(Main.PREFIX + pd.getColor().getChatColor() + p.getName() + "§f님께서 중도 탈주로 탈락처리되었습니다.");
			PlayerData.getPlayerData(p.getName()).kill();
		}
	}
	
	@EventHandler
	public void onMove(PlayerMoveEvent event) {
		AdminMap.onMove(event);
		String ventname = Vent.check(event.getTo());
		if(ventname != null) {
			Vent v = Vent.getVent(ventname);
			Player p = event.getPlayer();
			
			Location loc = event.getTo().clone();
			loc.setY(Vent.VENT_Y_VALUE);
			Util.setDoor(loc, false);
			
			PlayerUtil.setInvisible(p, true);
			p.teleport()
		}
	}
	
	@EventHandler
	public void onClick(InventoryClickEvent event) {
		AdminMap.onClick(event);
	}

}