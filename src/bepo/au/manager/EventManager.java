package bepo.au.manager;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import bepo.au.GameTimer.Status;
import bepo.au.GameTimer;
import bepo.au.Main;
import bepo.au.base.Mission;
import bepo.au.base.PlayerData;
import bepo.au.base.Sabotage;

public class EventManager implements Listener {

	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		Player p = event.getPlayer();
		PlayerData pd = PlayerData.getPlayerData(p.getName());

		if (pd == null)
			return;

		if (Main.gt.getStatus() == Status.VOTING) {
			// ��ǥ ��

			return;
		} else if (Main.gt.getStatus() == Status.WORKING) {

			boolean blockClick = event.getAction() == Action.RIGHT_CLICK_BLOCK;

			// 1. Ŭ���� �� ����
			if (blockClick) {
				Location loc = event.getClickedBlock().getLocation();
				
				if (LocManager.getLoc("EmergencyButton").contains(loc)) {
					if(pd.getRemainEmerg() <= 0) {
						p.sendMessage("��c����� ��� ���� ���� Ƚ���� �����߽��ϴ�.");
					} else if(GameTimer.EMERG_REMAIN_TICK > 0){
						p.sendMessage("��c���� ����� �Ұ����մϴ�. ���� �ð� : ��f" + ((GameTimer.EMERG_REMAIN_TICK / 20)+1) + "��");
					} else if(Sabotage.isActivating(0)) {
						p.sendMessage("��c���� �纸Ÿ�� �ߵ� �߿��� ��� ������ �� �� �����ϴ�.");
					} else {
						
					}
					return;
				}
				for (Mission m : pd.getMissions()) {
					int i = m.getCode(loc);
					if (i >= 0) {
						if (m.isCleared(i) || m.isCleared())
							p.sendMessage(Main.PREFIX + "��c�̹� �Ϸ��� �̼��Դϴ�.");
						else
							m.onStart(p, i);
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
					m.onStop(p, 0); // stop �� ��� �ڵ带 �޾ƿ� ���ΰ�?
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

	@EventHandler
	public void onBreak(BlockBreakEvent event) {
		Player p = event.getPlayer();
		if (GameTimer.PLAYERS.contains(p.getName()))
			event.setCancelled(true);
	}

	@EventHandler
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

}