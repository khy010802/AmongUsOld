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
				p.sendMessage(Main.PREFIX + "��cġ���� �纸Ÿ�� �ߵ� �߿� �ߵ��� �� �����ϴ�.");
			} else if(tick < 0) {
				p.sendMessage(Main.PREFIX + "��c���� �ݾ��� ���� ġ���� �纸Ÿ���� �ߵ��� �� �����ϴ�.");
			} else {
				p.sendMessage(Main.PREFIX + "��f" + (tick / 20 + 1) + "��c�� �� �ߵ��� �� �ֽ��ϴ�.");
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
				p.sendMessage(Main.PREFIX + "��c��ǥ �ð��� �ƴմϴ�.");
				event.setCancelled(true);
				return;
			}
			
			
			boolean blockClick = event.getAction() == Action.RIGHT_CLICK_BLOCK;

			// 1. Ŭ���� �� ����
			if (blockClick) {
				Location loc = event.getClickedBlock().getLocation();
				
				if(loc.getBlock().getType() == Material.IRON_TRAPDOOR) {
					if(GameTimer.IMPOSTER.contains(p.getName())) {
						if(loc.distance(p.getLocation().getBlock().getLocation()) > 3.0D) {
							p.sendMessage(Main.PREFIX + "��c��Ʈ�� ������ �پ��ּ���.");
						} else {
							Util.toggleDoor(loc);
							p.playSound(p.getLocation(), Sound.BLOCK_IRON_TRAPDOOR_OPEN, 1.0F, 1.0F);
						}
					}
				} else if (LocManager.getLoc("EmergencyButton").contains(loc)) {
					if(pd.getRemainEmerg() <= 0) {
						p.sendMessage("��c����� ��� ���� ���� Ƚ���� �����߽��ϴ�.");
					} else if(GameTimer.EMERG_REMAIN_TICK > 0){
						p.sendMessage("��c���� ����� �Ұ����մϴ�. ���� �ð� : ��f" + ((GameTimer.EMERG_REMAIN_TICK / 20)+1) + "��");
					} else if(Sabotage.isActivating(0)) {
						p.sendMessage("��c���� �纸Ÿ�� �ߵ� �߿��� ��� ������ �� �� �����ϴ�.");
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
								p.sendMessage(Main.PREFIX + "��c�̹� �Ϸ��� �̼��Դϴ�.");
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
					 // stop �� ��� �ڵ带 �޾ƿ� ���ΰ�?
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
			Bukkit.broadcastMessage(Main.PREFIX + pd.getColor().getChatColor() + p.getName() + "��f�Բ��� �ߵ� Ż�ַ� Ż��ó���Ǿ����ϴ�.");
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