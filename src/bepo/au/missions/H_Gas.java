package bepo.au.missions;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;

import Mission.CustomRandom;
import Mission.TimerBase;
import Mission.Util;


public class H_Gas implements Listener {

	GasTimer Timer = new GasTimer();
	Inventory Inv = null;
	Player P = null;
	CustomRandom Random = new CustomRandom();
	int Case = -1;

	public void gas1(Player p) {
		P = p;
		Inventory inv = Bukkit.createInventory(p, 54, "Gas1");
		Inv = inv;
		Case = 1;
		for (int i = 1; i < 6; i++) {
			Util.Stack(inv, getCoordinate(1, i), Material.RED_STAINED_GLASS_PANE, 1, " ");
			Util.Stack(inv, getCoordinate(5, i), Material.RED_STAINED_GLASS_PANE, 1, " ");
		}
		Util.Stack(inv, getCoordinate(2, 5), Material.RED_STAINED_GLASS_PANE, 1, " ");
		Util.Stack(inv, getCoordinate(3, 5), Material.RED_STAINED_GLASS_PANE, 1, " ");
		Util.Stack(inv, getCoordinate(4, 5), Material.RED_STAINED_GLASS_PANE, 1, " ");
		Util.Stack(inv, getCoordinate(2, 0), Material.RED_STAINED_GLASS_PANE, 1, " ");
		Util.Stack(inv, getCoordinate(4, 0), Material.RED_STAINED_GLASS_PANE, 1, " ");
		Util.Stack(inv, getCoordinate(7, 4), Material.GRAY_CONCRETE, 1, " ");
		p.openInventory(inv);
	}

	public void gas2(Player p) {
		P = p;
		Inventory inv = Bukkit.createInventory(p, 54, "Gas2");
		Inv = inv;
		Case = 2;
		for (int i = 1; i < 6; i++) {
			Util.Stack(inv, getCoordinate(3, i), Material.WHITE_STAINED_GLASS_PANE, 1, " ");
			Util.Stack(inv, getCoordinate(5, i), Material.WHITE_STAINED_GLASS_PANE, 1, " ");
		}
		Util.Stack(inv, getCoordinate(4, 5), Material.WHITE_STAINED_GLASS_PANE, 1, " ");
		Util.Stack(inv, getCoordinate(7, 4), Material.GRAY_CONCRETE, 1, " ");
		ItemStack potion = new ItemStack(Material.POTION);
		PotionMeta mateP = (PotionMeta) potion.getItemMeta();
		mateP.setColor(Color.YELLOW);
		potion.setItemMeta(mateP);
		inv.setItem(getCoordinate(5, 0), potion);
		p.openInventory(inv);
	}

	@EventHandler
	public void Click(InventoryClickEvent e) {
		P = (Player) e.getWhoClicked();
		Inv = e.getClickedInventory();
		if (e.getView().getTitle() == "Gas1") {
			if (e.getCurrentItem().getType() == Material.GRAY_CONCRETE) {
				e.setCancelled(true);
				Timer.StartTimer(4, true);
			} else {
				e.setCancelled(true);
			}
		}
		if (e.getView().getTitle() == "Gas2") {
			if (e.getCurrentItem().getType() == Material.GRAY_CONCRETE) {
				e.setCancelled(true);
				Timer.StartTimer(4, true);
			} else {
				e.setCancelled(true);
			}
		}

	}

	public int getCoordinate(int x, int y) {
		return x + 9 * y;
	}

	public final class GasTimer extends TimerBase {

		@Override
		public void EventStartTimer() {
			// TODO Auto-generated method stub

		}

		@Override
		public void EventRunningTimer(int count) {
			if (Case == 1) {
				if (!(count == 0)) {
					for (int i = 2; i < 5; i++) {
						Util.Stack(Inv, getCoordinate(i, count), Material.YELLOW_STAINED_GLASS_PANE, 1, " ");
					}
				}
			}
			if (Case == 2) {
				if (!(count == 0)) {
					Util.Stack(Inv, getCoordinate(4, count), Material.YELLOW_STAINED_GLASS_PANE, 1, " ");
				}
				if (count == 1) {
					Util.Stack(Inv, getCoordinate(5, 0), Material.GLASS_BOTTLE, 1, " ");
				}
			}
		}

		@Override
		public void EventEndTimer() {
			// TODO Auto-generated method stub
			P.closeInventory();
			P.sendMessage("Clear");
		}

	}
}
