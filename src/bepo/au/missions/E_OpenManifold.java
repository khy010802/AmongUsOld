package bepo.au.missions;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

public class E_OpenManifold implements Listener {
	int[] PassWord  = new int[10];
	Player P = null;
	int Count = -1;
	Inventory Inv = null;
	OpenManifoldTimer Timer = new OpenManifoldTimer();
	CustomRandom Random = new CustomRandom();
	
	public void openManifold(Player p) {
		Inventory inv = Bukkit.createInventory(p, 18, "Manifold"); //gui »ý¼º
		P = p;
		PassWord = Random.difrandom(1, 10, 10);
		for(int i = 0; i <= 9; i++) {
			if(i/5 == 0) {
				Util.Stack(inv, i+2, Material.WHITE_STAINED_GLASS_PANE, PassWord[i], " ");
			}
			else {
				Util.Stack(inv, i+6, Material.WHITE_STAINED_GLASS_PANE, PassWord[i], " ");
			}
		}
		Count = 1;
		p.openInventory(inv);
	}
	
	@EventHandler
	public void Click(InventoryClickEvent e) {
		Inventory inv = e.getClickedInventory();
		Player p = (Player) e.getWhoClicked();
		if(e.getView().getTitle().equals("Manifold")) {
			if(e.getCurrentItem().getType() == Material.WHITE_STAINED_GLASS_PANE) {
				e.setCancelled(true);
				int x = e.getSlot();
				int y = -1;
				if(x/9 == 0) {
					y = x -2;
				}
				else {
					y = x - 6;
				}
				if(PassWord[y] == Count) {
					float a = (float) Math.pow(2, (-6+Count)/12d);
					P.playSound(P.getLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME, 1, a);
					Util.Stack(inv, x, Material.GREEN_STAINED_GLASS_PANE, 1, " ");
					Count++;
					if(Count == 11) {
						p.closeInventory();
						p.sendMessage("Clear");
					}
				}
				else {
					Inv = inv;
					Timer.StartTimer(2);
				}
			}
			else {
				e.setCancelled(true);
			}
		}
	}
	
	public final class OpenManifoldTimer extends TimerBase {
		@Override
		public void EventEndTimer() {
			// TODO Auto-generated method stub
			for(int i = 0; i <= 9; i++) {
				if(i/5 == 0) {
					Util.Stack(Inv, i+2, Material.WHITE_STAINED_GLASS_PANE, PassWord[i], " ");
				}
				else {
					Util.Stack(Inv, i+6, Material.WHITE_STAINED_GLASS_PANE, PassWord[i], " ");
				}
				Count = 1;
			}
			
		}
		@Override
		public void EventRunningTimer(int count) {
			// TODO Auto-generated method stub
			
		}
		@Override
		public void EventStartTimer() {
			// TODO Auto-generated method stub
			P.playSound(P.getLocation(), Sound.BLOCK_NOTE_BLOCK_SNARE, 1, 1);
			for(int i = 0; i <= 9; i++) {
				if(i/5 == 0) {
					Util.Stack(Inv, i+2, Material.RED_STAINED_GLASS_PANE, PassWord[i], " ");
				}
				else {
					Util.Stack(Inv, i+6, Material.RED_STAINED_GLASS_PANE, PassWord[i], " ");
				}
			}
			
		}
	
	
	}
}
