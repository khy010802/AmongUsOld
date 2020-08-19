package bepo.au.missions;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

public class H_ActivatingReactor implements Listener {

	Inventory Inv = null;
	int[] ReactorPassword = new int[5];
	int Count = 0;
	int Tmp = 0;
	int Case = 0;
	int MaxCount = 1;
	Player P = null;
	ActivatingReactorTimer Timer = new ActivatingReactorTimer();
	CustomRandom Random = new CustomRandom();

	public void activatingReactor(Player p) {
		Inventory inv = Bukkit.createInventory(p, 36, "Activating_Reactor");
		Inv = inv;
		this.P = p;
		for (int i = 0; i < 5; i++) {
			ReactorPassword[i] = Random.random(1, 9);
		}
		for (int i = 2; i < 7; i++) {
    		Util.Stack(inv, i, Material.WHITE_STAINED_GLASS_PANE, 1, " ");
		}
		for (int i = 1; i < 4; i++) {
			for (int j = 1; j < 4; j++) {
	    		Util.Stack(inv, j + i * 9, Material.WHITE_CONCRETE, 1, " ");
			}
			for (int j = 5; j < 8; j++) {
	    		Util.Stack(inv, j + i * 9, Material.WHITE_WOOL, 1, " ");
			}
		}
		p.openInventory(inv);
		Count = 0;
		Lighting(1);
	}

	@EventHandler
	public void Click(InventoryClickEvent e) {
		Player p = (Player) e.getWhoClicked();
		Inventory inv = e.getClickedInventory();
		if (e.getView().getTitle().equals("Activating_Reactor")) {
			if (e.getCurrentItem().getType() == Material.WHITE_WOOL) {
				e.setCancelled(true);
				int x = e.getSlot();
				if ((x % 9 - 4) + ((x / 9 - 1) * 3) == ReactorPassword[Count]) {
					P.playSound(P.getLocation(), Sound.BLOCK_NOTE_BLOCK_SNARE, 10, 1);
		    		Util.Stack(inv, Count + 2, Material.GREEN_STAINED_GLASS_PANE, 1, " ");
					Count++;
					if (Count == MaxCount) {
						if (Count == 5) {
							P.sendMessage("Clear");
							P.closeInventory();
						} else {
							for (int i = 2; i < 7; i++) {
								Util.Stack(inv, i, Material.GREEN_STAINED_GLASS_PANE, 1, " ");
							}
							Case = 2;
							Timer.StartTimer(1, false, 10);
						}
					}
				} else {
					Count = 0;
					p.closeInventory();
				}
			} else {
				e.setCancelled(true);
			}
		}
	}

	public void Lighting(int count) {
		Case = 1;
		MaxCount = count;
		Timer.StartTimer(count, false, 10);
	}

	public final class ActivatingReactorTimer extends TimerBase {

		@Override
		public void EventStartTimer() {
			for (int i = 1; i < 4; i++) {
				for (int j = 5; j < 8; j++) {
					Util.Stack(Inv, j + i * 9, Material.GRAY_WOOL, 1, " ");
				}
			}

		}

		@Override
		public void EventRunningTimer(int count) {
			if (Case == 1) {
				if (count > 0) {
					if (Tmp <= 3) {
						Util.Stack(Inv, Tmp + 9, Material.WHITE_CONCRETE, 1, " ");
					} else if (Tmp > 3 && Tmp <= 6) {
						Util.Stack(Inv, Tmp + 15, Material.WHITE_CONCRETE, 1, " ");
					} else if (Tmp > 6 && Tmp <= 9) {
						Util.Stack(Inv, Tmp + 21, Material.WHITE_CONCRETE, 1, " ");
					}
				}
				if (count < MaxCount) {
					Tmp = ReactorPassword[count];
					Util.Stack(Inv, count + 2, Material.GREEN_STAINED_GLASS_PANE, 1, " ");
					if (Tmp <= 3) {
						P.playSound(P.getLocation(), Sound.BLOCK_NOTE_BLOCK_SNARE, 10, 1);
						Util.Stack(Inv, Tmp + 9, Material.BLUE_CONCRETE, 1, " ");
					} else if (Tmp > 3 && Tmp <= 6) {
						P.playSound(P.getLocation(), Sound.BLOCK_NOTE_BLOCK_SNARE, 10, 1);
						Util.Stack(Inv, Tmp + 15, Material.BLUE_CONCRETE, 1, " ");
					} else if (Tmp > 6 && Tmp <= 9) {
						P.playSound(P.getLocation(), Sound.BLOCK_NOTE_BLOCK_SNARE, 10, 1);
						Util.Stack(Inv, Tmp + 21, Material.BLUE_CONCRETE, 1, " ");
					}
				}
			}
		}

		@Override
		public void EventEndTimer() {
			if (Case == 1) {
				for (int i = 1; i < 4; i++) {
					for (int j = 5; j < 8; j++) {
						Util.Stack(Inv, j + i * 9, Material.WHITE_WOOL, 1, " ");
					}
				}
				for (int i = 2; i < 7; i++) {
					Util.Stack(Inv, i, Material.WHITE_STAINED_GLASS_PANE, 1, " ");
				}
			}
			if (Case == 2) {
				Lighting(Count + 1);
				Count = 0;
			}
		}

	}
}
