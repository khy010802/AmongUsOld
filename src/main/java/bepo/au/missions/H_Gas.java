package bepo.au.missions;

import java.util.Arrays;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

import bepo.au.base.Mission;
import bepo.au.base.TimerBase;
import bepo.au.utils.Util;

public class H_Gas extends Mission {

	int Count;
	Boolean opened = false;

	public H_Gas(MissionType mt2, String name, String korean, int clear, Location loc) {
		super(true, mt2, name, korean, clear, loc);
	}

	public void onAssigned(Player p) {
		assign(p);
		for (int i = 0; i < 4; i++) {
			uploadInventory(p, 54, "Gas " + i);
		}
		locs = Arrays.asList(locs.get(0), locs.get(1), locs.get(0), locs.get(2));
	}

	public void onStart(Player p, int i) {
		if (i == 0 || (i == 2 && cleared.contains(1)))
			gas1(p, i);
		else if (i == 3 && cleared.contains(2) && cleared.contains(1))
			gas2(p, 3);
		else if (i == 1 && cleared.contains(0))
			gas2(p, 1);

	}

	public void onStop(Player p, int i) {
		if (Timer != null && Timer.GetTimerRunning())
			Timer.StopTimer();
		Timer = null;
	}

	public void onClear(Player p, int i) {
		generalClear(p, i);
	}

	GasTimer Timer;

	public void gas1(Player p, int code) {
		for (int i = 1; i < 5; i++) {
			Util.Stack(gui.get(code), 1 + i * 9, Material.RED_STAINED_GLASS_PANE, 1, " ");
			Util.Stack(gui.get(code), 5 + i * 9, Material.RED_STAINED_GLASS_PANE, 1, " ");
		}
		for (int i = 2; i < 5; i++) {
			if (i != 3)
				Util.Stack(gui.get(code), i, Material.RED_STAINED_GLASS_PANE, 1, " ");
			Util.Stack(gui.get(code), i + 45, Material.RED_STAINED_GLASS_PANE, 1, " ");
		}
		Util.Stack(gui.get(code), 43, Material.GRAY_CONCRETE, 1, " ");

		if (!opened) {
			opened = true;
			Count = 4;
		}
		p.openInventory(gui.get(code));
	}

	public void gas2(Player p, int code) {

		for (int i = 1; i < 6; i++) {
			Util.Stack(gui.get(code), 3 + i * 9, Material.WHITE_STAINED_GLASS_PANE, 1, " ");
			Util.Stack(gui.get(code), 5 + i * 9, Material.WHITE_STAINED_GLASS_PANE, 1, " ");
		}
		// 이거 필요없어보이는 구문 하나 지웠는데 확인점
		Util.Stack(gui.get(code), 49, Material.WHITE_STAINED_GLASS_PANE, 1, " ");
		Util.Stack(gui.get(code), 43, Material.GRAY_CONCRETE, 1, " ");
		Util.StackPotion(gui.get(code), getCoordinate(5, 0), Color.YELLOW, 1, " ");

		if (!opened) {
			opened = true;
			Count = 4;
		}
		p.openInventory(gui.get(code));
	}

	@EventHandler
	public void Click(InventoryClickEvent e) {

		if (!checkPlayer(e))
			return;

		int code = getCode(e.getView().getTitle());
		if (code < 0)
			return;

	
		if (code % 2 == 0) {
			if (e.getCurrentItem() != null && e.getCurrentItem().getType() == Material.GRAY_CONCRETE) {
				Util.Stack(gui.get(code), 43, Material.GREEN_CONCRETE, 1, " ");
				e.setCancelled(true);
				startTimer(code);
			} else {
				e.setCancelled(true);
			}
		} else if (code % 2 == 1) {
			if (e.getCurrentItem() != null && e.getCurrentItem().getType() == Material.GRAY_CONCRETE) {
				Util.Stack(gui.get(code), 43, Material.GREEN_CONCRETE, 1, " ");
				e.setCancelled(true);
				startTimer(code);
			} else {
				e.setCancelled(true);
			}
		}
		
	}

	@EventHandler
	public void Close(InventoryCloseEvent e) {

		if (!checkPlayer(e))
			return;

		if (Timer != null) {
			Util.Stack(gui.get(getCode(e.getView().getTitle())), 43, Material.GRAY_CONCRETE, 1, " ");
			Timer.StopTimer();
			Timer = null;
		}
	}

	public void startTimer(int c) {
		Timer = new GasTimer();
		Timer.setCase(c);
		Timer.StartTimer(Count, true);
	} 
	
	public int getCoordinate(int x, int y) {
		return x + 9 * y;
	}

	public final class GasTimer extends TimerBase {

		private int Case = -1;

		public void setCase(int i) {
			this.Case = i;
		}

		@Override
		public void EventStartTimer() {
			// TODO Auto-generated method stub

		}

		@Override
		public void EventRunningTimer(int count) {
/*
			if (getPlayer() == null || !getPlayer().getOpenInventory().getTitle().split(" ")[0].equals("Gas")) {
				StopTimer();
				return;
			}*/
			if (Case % 2 == 0) {
				if (!(count == 0)) {
					for (int i = 2; i < 5; i++) {
						Util.Stack(gui.get(Case), i + 9 * count, Material.YELLOW_STAINED_GLASS_PANE, 1, " ");
					}
				}
			}
			if (Case % 2 == 1) {
				if (!(count == 0)) {
					Util.Stack(gui.get(Case), 4 + 9 * count, Material.YELLOW_STAINED_GLASS_PANE, 1, " ");
				}

				if (count == 1) {
					Util.Stack(gui.get(Case), 5, Material.GLASS_BOTTLE, 1, " ");
				}
			}
			Count = count;
		}

		@Override
		public void EventEndTimer() {
			// TODO Auto-generated method stub

			if (getPlayer() != null) {
				getPlayer().closeInventory();
				onClear(getPlayer(), Case);
			}
			opened = false;

		}

	}
}
