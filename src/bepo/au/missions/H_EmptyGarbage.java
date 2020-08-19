package bepo.au.missions;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import Mission.CustomRandom;
import Mission.TimerBase;
import Mission.Util;

public class H_EmptyGarbage implements Listener {

	EmptyGarbageTimer Timer = new EmptyGarbageTimer();
	CustomRandom Random = new CustomRandom();
	Inventory Inv;
	int Case;
	Player P = null;
	ItemStack stack[];

	public void emptyGarbage1(Player p) {
		Case = 1;
		Inventory inv = Bukkit.createInventory(p, 45, "EmptyGarbage");
		Inv = inv;
		P = p;
		for (int i = 0; i < 6; i++) {
			int a = 0;
			if (!(i == 0 || i == 5)) {
				a = Random.random(1, 2);
			}
			for (int j = a; j < 5; j++) {
				int b = Random.random(0, 1);
				if (b == 1) {
					Util.Stack(inv, getCoordinate(i, j), Material.LILY_PAD, 1, " ");
				}
				if (b == 0) {
					Util.Stack(inv, getCoordinate(i, j), Material.KELP, 1, " ");
				}
				Util.Stack(inv, getCoordinate(0, j), Material.WHITE_STAINED_GLASS_PANE, 1, " ");
				Util.Stack(inv, getCoordinate(5, j), Material.WHITE_STAINED_GLASS_PANE, 1, " ");
			}
		}
		Util.Stack(inv, 25, Material.GRAY_CONCRETE, 1, " ");
		p.openInventory(inv);
	}

	public void emptyGarbage2(Player p) {
		Case = 2;
		Inventory inv = Bukkit.createInventory(p, 45, "EmptyGarbage");
		Inv = inv;
		P = p;
		for (int i = 0; i < 6; i++) {
			int a = 0;
			if (!(i == 0 || i == 5)) {
				a = Random.random(1, 2);
			}
			for (int j = a; j < 5; j++) {
				int b = Random.random(0, 1);
				if (b == 1) {
					Util.Stack(inv, getCoordinate(i, j), Material.LILY_PAD, 1, " ");
				}
				if (b == 0) {
					Util.Stack(inv, getCoordinate(i, j), Material.DRIED_KELP_BLOCK, 1, " ");
				}
				Util.Stack(inv, getCoordinate(0, j), Material.WHITE_STAINED_GLASS_PANE, 1, " ");
				Util.Stack(inv, getCoordinate(5, j), Material.WHITE_STAINED_GLASS_PANE, 1, " ");
			}
		}
		int x = Random.random(1, 4);
		int y = Random.random(1, 4);
		Util.Stack(inv, getCoordinate(x, y), Material.DIAMOND, 1, " ");
		x = Random.random(1, 4);
		y = Random.random(1, 4);
		Util.Stack(inv, getCoordinate(x, y), Material.TOTEM_OF_UNDYING, 1, " ");
		x = Random.random(1, 4);
		y = Random.random(1, 4);
		Util.Stack(inv, getCoordinate(x, y), Material.TNT, 1, " ");
		Util.Stack(inv, 25, Material.GRAY_CONCRETE, 1, " ");
		p.openInventory(inv);
	}

	@EventHandler
	public void Click(InventoryClickEvent e) {
		if (e.getView().getTitle() == "EmptyGarbage") {
			if (e.getCurrentItem().getType() == Material.GRAY_CONCRETE) {
				e.setCancelled(true);
				Timer.StartTimer(4);
			} else {
				e.setCancelled(true);
			}
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
			stack = Inv.getContents();
			for (int i = 4; i > 0; i--) {
				for (int j = 1; j < 5; j++) {
					P.playSound(P.getLocation(), Sound.ENTITY_MINECART_RIDING, 0.05f, 0.1f);
					Inv.setItem(getCoordinate(i, j), stack[getCoordinate(i, j) - 9]);
				}
			}
		}

		@Override
		public void EventEndTimer() {
			// TODO Auto-generated method stub
			P.closeInventory();
			P.stopSound(Sound.ENTITY_MINECART_RIDING);
			P.sendMessage("Clear");
		}

	}
}
