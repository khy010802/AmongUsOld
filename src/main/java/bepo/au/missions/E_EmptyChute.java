package bepo.au.missions;

import java.util.HashSet;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;

import bepo.au.base.Mission;
import bepo.au.utils.Util;

public class E_EmptyChute extends Mission {

	int maxLeaves = 5; // 해야하는 나뭇잎 개수 HARD EASY 조절 가능. 최대 36개.
	int remainingLeaves = maxLeaves;
	int maxslot = 45;
	HashSet<Integer> leafSlots;
	public E_EmptyChute(MissionType mt, String name, String korean, int clear, Location loc) {
		super(mt, name, korean, clear, loc);
	}
	
	@Override
	public void onAssigned(Player p) {
		assign(p);
		uploadInventory(p, 45, "EmptyChute");
		
		gui.get(0).setMaxStackSize(1);
		leafSlots = new HashSet<Integer>();
		while (true) {
			int leafslot = Util.random(3, 8) + 9 * Util.random(0, 4);
			Util.debugMessage(leafslot + "에 나뭇잎");
			leafSlots.add(leafslot);
			if (leafSlots.size() == maxLeaves)
				break;
		}
		
		
		
	}
	
	@Override
	public void onStart(Player p, int code) {
		a_reset();
		p.openInventory(gui.get(0));
	}
	
	@Override
	public void onStop(Player p, int code) {
		p.getInventory().remove(Material.KELP);
	}
	
	@Override
	public void onClear(Player p, int code) {
		generalClear(p, code);
	}

	public void a_reset() {
		gui.get(0).clear();
		remainingLeaves = maxLeaves;
		for (int slot = 0; slot < maxslot; slot++) {
			int x = slot % 9, y = slot / 9;
			if (x < 3) {
				if (slot == 20)
					Util.Stack(gui.get(0), slot, Material.HOPPER, 1, "§f이곳에 나뭇잎을 들고 클릭해주세요");
				else if (slot == 18)
					Util.Stack(gui.get(0), slot, Material.BOOK, 1, "§f산소 필터 청소하기", "§7나뭇잎을 들고 호퍼에 클릭하세요.");
				else if (x > 0 && y > 0 && y <= 3)
					Util.Stack(gui.get(0), slot, Material.WHITE_STAINED_GLASS_PANE, 1, " ");
				else
					Util.Stack(gui.get(0), slot, Material.GRAY_STAINED_GLASS_PANE, 1, " ");
			} else if (leafSlots.contains(slot)) {

				Util.Stack(gui.get(0), slot, Material.KELP, 1, "§f나뭇잎", "§7이것을 든 채로 호퍼를 클릭하세요.");
			}

		}
		
	}

	public void removeLeaf(Player p) {
		if (--remainingLeaves == 0) {
			onClear(p, 0);
			return;
		}
		Util.debugMessage(remainingLeaves + "개 남음");

	}

	@EventHandler
	public void onClick(InventoryClickEvent e) {
		if(!checkPlayer(e)) return;
		
		Player p = (Player) e.getWhoClicked();
		
		if (e.getView().getTitle().equals("EmptyChute")) {
			Util.debugMessage("클릭 인식됨");
			// int slot = e.getRawSlot();
			ItemStack itemstack = e.getCurrentItem();

			// Inventory gui = e.getClickedInventory();
			// Player p = (Player) e.getWhoClicked();

			if (e.getClick().equals(ClickType.DOUBLE_CLICK) || e.isShiftClick() == true) { // 더블클릭,쉬프트클릭 금지
				Util.debugMessage("클릭 불가");
				e.setCancelled(true);
			}
			if (itemstack != null) {
				if (e.getCursor().getType() == Material.AIR && itemstack.getType() == Material.KELP)
					Util.debugMessage("켈프 클릭 인식됨");
				else if (e.getCursor().getType() == Material.KELP && itemstack.getType() == Material.HOPPER) {
					
					e.getWhoClicked().setItemOnCursor(null);
					e.setCancelled(true);
					removeLeaf(p);
				} else {
					Util.debugMessage("클릭 불가");
					e.setCancelled(true);
				}
			}
		}
	}

	@EventHandler
	public void onDrag(InventoryDragEvent e) {
		if (e.getView().getTitle().equals("EmptyChute") && !e.getRawSlots().isEmpty()) {
			;
		}

	}

}
