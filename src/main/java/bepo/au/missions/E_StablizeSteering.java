package bepo.au.missions;

import bepo.au.Main;
import bepo.au.base.Mission;
import bepo.au.utils.Util;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class E_StablizeSteering extends Mission {

	Random random = new Random();
	private final int maxslot = 45;
	private final String guiName = "StablizeSteering";
	private Material[] Clickable = {Material.ELYTRA};
	/*
	 * 명령어 쳤을 때 실행됨, GUI 열기 시도.
	 */

	public E_StablizeSteering(MissionType mt, String name, String korean, int clear, Location loc) {
		super(mt, name, korean, clear, loc);
	}

	@Override
	public void onAssigned(Player p) {
		assign(p);
		uploadInventory(p, maxslot, guiName);
	}

	@Override
	public void onStart(Player p, int code) {
		int x, y;
		while (true) {
			x = random.nextInt(9);
			y = random.nextInt(5);
			if (!(2 < x && x < 6 && 0 < y && y < 4))
				break; // 가운데 3x3칸은 ㄴㄴ
		}
		int elytraSlot = x + y * 9;
		setGUI(elytraSlot); // GUI 만들기
		p.openInventory(gui.get(0));
	}

	@Override
	public void onStop(Player p, int code) {
		new BukkitRunnable() {
			public void run() {
				p.getInventory().remove(Material.ELYTRA);
			}
		}.runTaskLater(Main.getInstance(), 0L);

	}

	@Override
	public void onClear(Player p, int code) {
		generalClear(p, code);
	}

	private void setGUI(int elytraSlot) {
		List<String> lore = Arrays.asList("§7조정간을 정 가운데의 빈 공간으로 옮기세요.");
		for (int slot = 0; slot < maxslot; slot++) {
			// int x = slot % 9, y = slot / 9;
			if (slot == 22)
				; // 가운데는 빈칸
			else if (slot == 21 || slot == 23 || slot == 13 || slot == 31) { // 주위 4칸은 하얀색
				Util.Stack(gui.get(0), slot, Material.WHITE_STAINED_GLASS_PANE, 1, " ");
			} else if (slot == elytraSlot) { // 겉날개 랜덤위치
				Util.Stack(gui.get(0), slot, Material.ELYTRA, 1, "§f조정간", lore);
			} else {
				Util.Stack(gui.get(0), slot, Material.GRAY_STAINED_GLASS_PANE, 1, " ");
			}

		}
	}

	/*
	 * 클리어 확인
	 */
	private void check(Player p) {
		new BukkitRunnable() {
			public void run() {
				if(gui.get(0).getItem(22) != null && gui.get(0).getItem(22).getType()==Clickable[0]) {
					Util.debugMessage("클리어!");
					onClear(p, 0);
				}
			}
		}.runTaskLater(main, 0L);
	}



	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	@EventHandler
	private void onClick(InventoryClickEvent e) {
		if(!checkPlayer(e)) return;

		Util.debugMessage("클릭 인식됨");
		ItemStack itemstack = e.getCurrentItem();

		// Inventory gui = e.getClickedInventory();
		// Player p = (Player) e.getWhoClicked();

		if (e.getClick().equals(ClickType.DOUBLE_CLICK) || e.isShiftClick() == true) { // 더블클릭,쉬프트클릭 금지
			Util.debugMessage("더블 클릭 불가");
			e.setCancelled(true);
		}
		if (itemstack != null) {
			if (Arrays.asList(Clickable).contains(itemstack.getType())) {
				check((Player) e.getWhoClicked());
			} else {
				Util.debugMessage("클릭 불가");
				e.setCancelled(true);
			}
		} else {
			check((Player) e.getWhoClicked());
		}
	}

	@EventHandler
	private void onDrag(InventoryDragEvent e) {
		if(!checkPlayer(e)) return;
		if (!e.getRawSlots().isEmpty()) {
			for(int slot : e.getRawSlots()) {
				if (slot==22) {
					check((Player) e.getWhoClicked());
					break;
				}
			}
		}

	}

}
