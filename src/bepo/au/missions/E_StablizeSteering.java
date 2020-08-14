package bepo.au.missions;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;

public class E_StablizeSteering implements Listener {
	Main main;
	Player p;
	Random random = new Random();
	private final int maxslot = 45;
	private Inventory gui;
	private final String guiName = "StablizeSteering";
	private Material[] Clickable = {Material.ELYTRA};
	/*
	 * 명령어 쳤을 때 실행됨, GUI 열기 시도.
	 */
	public void stablizeSteeringOpen(Player pl, Main m) {

		main = m;
		p = pl;
		initialize_stablizeSteering();
		p.openInventory(gui);

	}

	/*
	 * 초기화 ; GUI를 세팅.
	 */
	private void initialize_stablizeSteering() {
		int x, y;
		while (true) {
			x = random.nextInt(9);
			y = random.nextInt(5);
			if (!(2 < x && x < 5 && 0 < y && y < 4))
				break; // 가운데 3x3칸은 ㄴㄴ
		}
		int elytraSlot = x + y * 9;
		gui = Bukkit.createInventory(p, maxslot, guiName);
		gui.setMaxStackSize(1);
		setGUI(elytraSlot); // GUI 만들기

	}
	/*
	 * GUI를 만든다.
	 */

	private void setGUI(int elytraSlot) {
		List<String> lore = Arrays.asList("§7조정간을 정 가운데의 빈 공간으로 옮기세요.");
		for (int slot = 0; slot < maxslot; slot++) {
			// int x = slot % 9, y = slot / 9;
			if (slot == 22)
				; // 가운데는 빈칸
			else if (slot == 21 || slot == 23 || slot == 13 || slot == 31) { // 주위 4칸은 하얀색
				Util.Stack(gui, slot, Material.WHITE_STAINED_GLASS_PANE, 1, " ");
			} else if (slot == elytraSlot) { // 겉날개 랜덤위치
				Util.Stack(gui, slot, Material.ELYTRA, 1, "§f조정간", lore);
			} else {
				Util.Stack(gui, slot, Material.GRAY_STAINED_GLASS_PANE, 1, " ");
			}

		}
	}

	/*
	 * 클리어 확인
	 */
	private void check() {
		new BukkitRunnable() {
			public void run() {
				if(gui.getItem(22).getType()==Clickable[0]) {
					Util.debugMessage("클리어!");
				}
			}
		}.runTaskLater(main, 0L);
	}

	

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	@EventHandler
	private void onClick(InventoryClickEvent e) {
		if (e.getView().getTitle().equals(guiName)) {
			Util.debugMessage("클릭 인식됨");
			int slot = e.getRawSlot();
			ItemStack itemstack = e.getCurrentItem();

			// Inventory gui = e.getClickedInventory();
			// Player p = (Player) e.getWhoClicked();

			if (e.getClick().equals(ClickType.DOUBLE_CLICK) || e.isShiftClick() == true) { // 더블클릭,쉬프트클릭 금지
				Util.debugMessage("더블 클릭 불가");
				e.setCancelled(true);
			}
			if (itemstack != null) {
				if (Arrays.asList(Clickable).contains(itemstack.getType())) {
					;
				} else {
					Util.debugMessage("클릭 불가");
					e.setCancelled(true);
				}
			}else {check();}
		}
	}

	@EventHandler
	private void onDrag(InventoryDragEvent e) {
		if (e.getView().getTitle().equals(guiName) && !e.getRawSlots().isEmpty()) {
			for(int slot : e.getRawSlots()) {
				if (slot==22) {
					check();
					break;
				}
			}
			}

	}

}
