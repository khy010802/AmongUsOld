package bepo.au.missions;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import bepo.au.base.Mission;
import bepo.au.utils.Util;

public class H_AlignEngine extends Mission {

	private int maxslot = 27;
	private Material m = Material.ACACIA_BOAT;
	private String guiName = "AlignEngine";
	private String Namelore[] = { "Upper", "Lower" };

	public H_AlignEngine(MissionType mt, String name, String korean, int required_clear, Location loc) {
		super(mt, name, korean, required_clear, loc);
	}

	@Override
	public void onAssigned(Player p) {
		assign(p);
		for (int i = 0; i < 2; i++) {
			uploadInventory(p, maxslot, guiName + " " + Namelore[i]);
			for (int slot = 0; slot < 9; slot++)
				Util.Stack(gui.get(i), slot,
						(slot == 4 ? Material.GREEN_STAINED_GLASS_PANE : Material.WHITE_STAINED_GLASS_PANE), 1, " ");
			Util.Stack(gui.get(i), 9, m, 1, "§f이것을 가운데에 두세요.");
			for (int slot = 18; slot < 27; slot++)
				Util.Stack(gui.get(i), slot,
						(slot == 4 ? Material.GREEN_STAINED_GLASS_PANE : Material.WHITE_STAINED_GLASS_PANE), 1, " ");
		}
	}

	@Override
	public void onStart(Player p, int i) {
		p.openInventory(gui.get(i));
	}

	@Override
	public void onClear(Player p, int i) {
		generalClear(p, i);
	}

	@Override
	public void onStop(Player p, int i) {
		p.getInventory().remove(m);

	}



	public void onClick(InventoryClickEvent e) {

		if (!checkPlayer(e))
			return;

		Util.debugMessage("클릭 인식됨");
		ItemStack itemstack = e.getCurrentItem();
		if (e.getClick().equals(ClickType.DOUBLE_CLICK) || e.isShiftClick() == true) { // 더블클릭,쉬프트클릭 금지
			Util.debugMessage("클릭 불가");
			e.setCancelled(true);
		}
		if (itemstack != null) {
			if (e.getRawSlot() == 13 && e.getCursor().getType() == m) {
				for (int i = 0; i < 2; i++) {
					if (e.getView().getTitle().split(" ")[1] == Namelore[i]) {
						onClear((Player) e.getWhoClicked(), i);
						;
						break;
					}
				}

			} else {
				Util.debugMessage("클릭 불가");
				e.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onDrag(InventoryDragEvent e) {
		
		if(!checkPlayer(e)) return;
		
		if (!e.getRawSlots().isEmpty() && e.getCursor().getType() == m) {
			for (int slot : e.getRawSlots())
				if (slot == 13) {
					for (int i = 0; i < 2; i++) {
						final int c = i;
						if (e.getView().getTitle().split(" ")[1] == Namelore[i]) {
							new BukkitRunnable() {
								public void run() {
									if(gui.get(0).getItem(22).getType()==m) {
										Util.debugMessage("클리어!");
										onClear((Player) e.getWhoClicked(), c);
									}
								}
							}.runTaskLater(main, 0L);

						}
					}

				}

		}
	}
}