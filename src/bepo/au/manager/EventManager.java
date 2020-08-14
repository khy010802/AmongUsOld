package bepo.au.manager;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import bepo.au.Main;
import bepo.au.base.Mission;
import bepo.au.base.PlayerData;

public class EventManager implements Listener{
	
	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		Player p = event.getPlayer();
		PlayerData pd = PlayerData.getPlayerData(p.getName());
		if(event.getAction() == Action.RIGHT_CLICK_BLOCK && pd != null) {
			Location loc = event.getClickedBlock().getLocation();
			for(Mission m : pd.getMissions()) {
				int i = m.getCode(loc);
				if(i >= 0) {
					if(m.isCleared()) p.sendMessage(Main.PREFIX + "§c이미 완료한 미션입니다.");
					else m.onStart(p, i);
				}
			}
		}
	}
	
	@EventHandler
	public void onClose(InventoryCloseEvent event) {
		if(event.getPlayer() instanceof Player) {
			Player p = (Player) event.getPlayer();
			
			for(Mission m : Mission.MISSIONS) {
				if(event.getView().getTitle().equalsIgnoreCase(m.getTitle())) {
					m.onStop(p, 0); // stop 시 어떻게 코드를 받아올 것인가?
					break;
				}
			}
		}
	}
	
	@EventHandler
	public void onDrop(PlayerDropItemEvent event) {
		Player p = event.getPlayer();
		
		if(PlayerData.getPlayerData(p.getName()) != null) {
			event.setCancelled(true);
		}
	}

}
