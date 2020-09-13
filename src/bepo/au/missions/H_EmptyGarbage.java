package bepo.au.missions;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import bepo.au.base.Mission;
import bepo.au.base.TimerBase;
import bepo.au.utils.Util;

public class H_EmptyGarbage extends Mission {

	public H_EmptyGarbage(MissionType mt2, String name, String korean, int clear, Location loc) {
		super(true, mt2, name, korean, clear, loc);
	}

	public void onAssigned(Player p) {
		assign(p);
		uploadInventory(p, 45, "EmptyGarbage 0");
		uploadInventory(p, 45, "EmptyGarbage 1");
	}

	public void onStart(Player p, int i) {
		Timer = new EmptyGarbageTimer();
		if (i == 0)
			emptyGarbage1(p);
		else if (cleared.contains((Integer) i))
			emptyGarbage2(p);
	}

	public void onStop(Player p, int i) {
		if(Timer != null && Timer.GetTimerRunning()) Timer.StopTimer();
	}

	public void onClear(Player p, int i) {
		generalClear(p, i);
	}

	EmptyGarbageTimer Timer;
	ItemStack stack[];

	public void emptyGarbage1(Player p) {
		for (int i = 0; i < 6; i++) {
			int a = 0;
			if (!(i == 0 || i == 5)) {
				a = Util.random(1, 2);
			}
			for (int j = a; j < 5; j++) {
				int b = Util.random(0, 1);
				if (b == 1) {
					Util.Stack(gui.get(0), getCoordinate(i, j), Material.LILY_PAD, 1, " ");
				}
				if (b == 0) {
					Util.Stack(gui.get(0), getCoordinate(i, j), Material.KELP, 1, " ");
				}
				Util.Stack(gui.get(0), getCoordinate(0, j), Material.WHITE_STAINED_GLASS_PANE, 1, " ");
				Util.Stack(gui.get(0), getCoordinate(5, j), Material.WHITE_STAINED_GLASS_PANE, 1, " ");
			}
		}
		Util.Stack(gui.get(0), 25, Material.GRAY_CONCRETE, 1, " ");
		p.openInventory(gui.get(0));
	}

	public void emptyGarbage2(Player p) {
		for (int i = 0; i < 6; i++) {
			int a = 0;
			if (!(i == 0 || i == 5)) {
				a = Util.random(1, 2);
			}
			for (int j = a; j < 5; j++) {
				int b = Util.random(0, 1);
				if (b == 1) {
					Util.Stack(gui.get(1), getCoordinate(i, j), Material.LILY_PAD, 1, " ");
				}
				if (b == 0) {
					Util.Stack(gui.get(1), getCoordinate(i, j), Material.DRIED_KELP_BLOCK, 1, " ");
				}
				Util.Stack(gui.get(1), getCoordinate(0, j), Material.WHITE_STAINED_GLASS_PANE, 1, " ");
				Util.Stack(gui.get(1), getCoordinate(5, j), Material.WHITE_STAINED_GLASS_PANE, 1, " ");
			}
		}
		int x = Util.random(1, 4);
		int y = Util.random(1, 4);
		Util.Stack(gui.get(1), getCoordinate(x, y), Material.DIAMOND, 1, " ");
		x = Util.random(1, 4);
		y = Util.random(1, 4);
		Util.Stack(gui.get(1), getCoordinate(x, y), Material.TOTEM_OF_UNDYING, 1, " ");
		x = Util.random(1, 4);
		y = Util.random(1, 4);
		Util.Stack(gui.get(1), getCoordinate(x, y), Material.TNT, 1, " ");
		Util.Stack(gui.get(1), 25, Material.GRAY_CONCRETE, 1, " ");
		p.openInventory(gui.get(1));
	}

	@EventHandler
	public void Click(InventoryClickEvent e) {

		if (!checkPlayer(e))
			return;

		if (e.getCurrentItem().getType() == Material.GRAY_CONCRETE) {
			e.setCancelled(true);
			Timer.StartTimer(4);
		} else {
			e.setCancelled(true);
		}
	}

	public int getCoordinate(int x, int y) {
		return x + 9 * y;
	}

	public int setCoordinateX(int idx) {
		return idx % 9;
	}

	public int setCoordinateY(int idx) {
		return idx / 9;
	}

	public final class EmptyGarbageTimer extends TimerBase {

		@Override
		public void EventStartTimer() {
			// TODO Auto-generated method stub

		}

		@Override
		public void EventRunningTimer(int count) {
			
			if(getPlayer() == null) {
				StopTimer();
				return;
			}
			
			Player P = getPlayer();
			
			Inventory Inv = P.getOpenInventory().getTopInventory();
			if(Inv == null) {
				StopTimer();
				return;
			}
			
			stack = Inv.getContents();
			for (int i = 4; i > 0; i--) {
				for (int j = 1; j < 5; j++) {
					P.playSound(P.getLocation(), Sound.ENTITY_MINECART_RIDING, 1, 1);
					Inv.setItem(getCoordinate(i, j), stack[getCoordinate(i, j) - 9]);
				}
			}
		}

		@Override
		public void EventEndTimer() {
			// TODO Auto-generated method stub
			getPlayer().closeInventory();
		}

	}
}
