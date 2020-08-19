package bepo.au.missions;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import Mission.TimerBase;


public class OpenManifold implements Listener {
	int[] PassWord  = new int[10];
	Player P = null;
	int Count = -1;
	Inventory Inv = null;
	OpenManifoldTimer Timer = new OpenManifoldTimer();
	
	public void openManifold(Player p) {
		Inventory inv = Bukkit.createInventory(p, 18, "Manifold"); //gui »ý¼º
		P = p;
		PassWord = difrandom(1, 10, 10);
		for(int i = 0; i <= 9; i++) {
			if(i/5 == 0) {
				inv.setItem(i+2, new ItemStack(Material.WHITE_STAINED_GLASS_PANE, PassWord[i]));
			}
			else {
				inv.setItem(i+6, new ItemStack(Material.WHITE_STAINED_GLASS_PANE, PassWord[i]));
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
					P.playSound(P.getLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME, 10, 1);
					inv.setItem(x, new ItemStack(Material.GREEN_STAINED_GLASS_PANE, Count));
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
	
	public int[] difrandom(int min, int max, int length) {	
		if (length>max-min+1) return null;
		ArrayList<Integer> numbers = new ArrayList<Integer>();
		for (int num = min; num<=max ; num++) numbers.add(num);
		Collections.shuffle(numbers);
		return Arrays.stream(numbers.toArray(new Integer[numbers.size()])).mapToInt(Integer::intValue).toArray();
		
	}	
	
	
	public final class OpenManifoldTimer extends TimerBase {
		@Override
		public void EventEndTimer() {
			// TODO Auto-generated method stub
			for(int i = 0; i <= 9; i++) {
				if(i/5 == 0) {
					Inv.setItem(i+2, new ItemStack(Material.WHITE_STAINED_GLASS_PANE, PassWord[i]));
				}
				else {
					Inv.setItem(i+6, new ItemStack(Material.WHITE_STAINED_GLASS_PANE, PassWord[i]));
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
			P.playSound(P.getLocation(), Sound.BLOCK_NOTE_BLOCK_SNARE, 10, 1);
			for(int i = 0; i <= 9; i++) {
				if(i/5 == 0) {
					Inv.setItem(i+2, new ItemStack(Material.RED_STAINED_GLASS_PANE, PassWord[i]));
				}
				else {
					Inv.setItem(i+6, new ItemStack(Material.RED_STAINED_GLASS_PANE, PassWord[i]));
				}
			}
			
		}
	
	
	}
}
