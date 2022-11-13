package bepo.au.missions;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;

import bepo.au.base.Mission;
import bepo.au.base.TimerBase;
import bepo.au.utils.Util;


public class E_OpenManifold extends Mission {
	
	int[] PassWord  = new int[10];
	int Count = -1;
	OpenManifoldTimer Timer;
	
	public E_OpenManifold(MissionType mt2, String name, String korean, int clear, Location loc) {
		super(mt2, name, korean, clear, loc);
	}

	public void onAssigned(Player p) {
		assign(p);
		uploadInventory(p, 18, "Manifold");
	}

	public void onStart(Player p, int i) {
		openManifold(p);
	}

	public void onStop(Player p, int i) {
		if(Timer != null && Timer.GetTimerRunning()) Timer.StopTimer();
		Timer = null;
	}

	public void onClear(Player p, int i) {
		generalClear(p, i);
	}
	
	public void openManifold(Player p) {
		Timer = new OpenManifoldTimer();
		PassWord = difrandom(1, 10, 10);
		for(int i = 0; i <= 9; i++) {
			if(i/5 == 0) {
				Util.Stack(gui.get(0), i+2, Material.WHITE_STAINED_GLASS_PANE, PassWord[i], " ");
			}
			else {
				Util.Stack(gui.get(0), i+6, Material.WHITE_STAINED_GLASS_PANE, PassWord[i], " ");
			}
		}
		Count = 1;
		p.openInventory(gui.get(0));
	}
	
	@EventHandler
	public void Click(InventoryClickEvent e) {
		
		if(!checkPlayer(e)) return;
		
		Player P = (Player) e.getWhoClicked();
		if(e.getView().getTitle().equals("Manifold")) {
			if(e.getCurrentItem() != null && e.getCurrentItem().getType() == Material.WHITE_STAINED_GLASS_PANE) {
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
					Util.Stack(gui.get(0), x, Material.GREEN_STAINED_GLASS_PANE, Count, " ");
					Count++;
					if(Count == 11) {
						P.closeInventory();
						onClear(P, 0);
					}
				}
				else {
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
					Util.Stack(gui.get(0), i+2, Material.WHITE_STAINED_GLASS_PANE, PassWord[i], " ");
				}
				else {
					Util.Stack(gui.get(0), i+6, Material.WHITE_STAINED_GLASS_PANE, PassWord[i], " ");
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
			
			if(getPlayer() != null) {
				Player P = getPlayer();
				P.playSound(P.getLocation(), Sound.BLOCK_NOTE_BLOCK_SNARE, 10, 1);
				for(int i = 0; i <= 9; i++) {
					if(i/5 == 0) {
					Util.Stack(gui.get(0), i+2, Material.RED_STAINED_GLASS_PANE, PassWord[i], " ");
				}
				else {
					Util.Stack(gui.get(0), i+6, Material.RED_STAINED_GLASS_PANE, PassWord[i], " ");
					}
				}
			}
			
			
			
		}
	
	
	}
}
