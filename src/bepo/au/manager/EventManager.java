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
			// 투표 중

			return;
		} else if (Main.gt.getStatus() == Status.WORKING) {

			boolean blockClick = event.getAction() == Action.RIGHT_CLICK_BLOCK;

			// 1. 클릭한 곳 점검
			if (blockClick) {
				Location loc = event.getClickedBlock().getLocation();
				
				if (LocManager.getLoc("EmergencyButton").contains(loc)) {
					if(pd.getRemainEmerg() <= 0) {
						p.sendMessage("§c당신은 모든 소집 가능 횟수를 소진했습니다.");
					} else if(GameTimer.EMERG_REMAIN_TICK > 0){
						p.sendMessage("§c아직 사용이 불가능합니다. 남은 시간 : §f" + ((GameTimer.EMERG_REMAIN_TICK / 20)+1) + "초");
					} else if(Sabotage.isActivating(0)) {
						p.sendMessage("§c위급 사보타지 발동 중에는 긴급 소집을 할 수 없습니다.");
					} else {
						
					}
					return;
				}
				for (Mission m : pd.getMissions()) {
					int i = m.getCode(loc);
					if (i >= 0) {
						if (m.isCleared(i) || m.isCleared())
							p.sendMessage(Main.PREFIX + "§c이미 완료한 미션입니다.");
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
					m.onStop(p, 0); // stop 시 어떻게 코드를 받아올 것인가?
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
			Bukkit.broadcastMessage(Main.PREFIX + pd.getColor().getChatColor() + p.getName() + "§f님께서 중도 탈주로 탈락처리되었습니다.");
			PlayerData.getPlayerData(p.getName()).kill();
		}
	}

}