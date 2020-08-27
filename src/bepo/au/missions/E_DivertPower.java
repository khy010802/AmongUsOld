package bepo.au.missions;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import org.bukkit.event.EventHandler;

import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;

import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import bepo.au.Util;
import bepo.au.base.Mission;
import bepo.au.base.Mission.MissionType;

public class E_DivertPower extends Mission {

	public E_DivertPower(MissionType mt2, String name, String korean, int clear, Location loc) {
		super(mt2, name, korean, clear, loc);
	}

	int roomNum;

	final Material[] material = { Material.RED_STAINED_GLASS, // 레버0
			Material.REDSTONE_BLOCK, // 활성화된 레버1
			Material.YELLOW_STAINED_GLASS_PANE, // 전기2
			Material.GRAY_STAINED_GLASS_PANE, // 회색배경3
			Material.WHITE_STAINED_GLASS_PANE, // 하양배경4
			Material.LIGHT_GRAY_STAINED_GLASS_PANE// 연회색5
	};
	final String[] rooms = { "§f하부 엔진", // 레버
			"상부 엔진", //
			"무기고", //
			"보호막 제어실", //
			"정의되지 않은 방", //
			"항해실", //
			"통신실", //
			"산소 공급실", //
			"보안실" };// 배경

	public void onAssign(Player p) {

	}

	public void divertPowerPt1(Player pl, int roomnumber) {
		roomNum = roomnumber;
		gui = Bukkit.createInventory(p, 54, "DivertPower");
		reset1();
	}

	public void divertPowerPt2(Player pl, int roomnumber) {
		roomNum = roomnumber;
		gui = Bukkit.createInventory(p, 27, "DivertPower " + rooms[roomNum]);
		reset2();
	}

	public void reset1() {
		for (int slot = 0; slot < 54; slot++) {// gui인벤토리
			int y = slot / 9, x = slot % 9;
			Util.debugMessage(slot + "에 아이템을 채웁니다" + x + "," + y);
			if (y == 3 || y == 5)
				Util.Stack(gui, slot, x == roomNum ? Material.AIR : material[3], 1, " "); // 배경 가로줄 채우기
			else if (x == 4) {
				Util.Stack(gui, slot, material[3], 1, " ");// 배경 세로줄 채우기
			} else if (y == 4) { // y가 7일때
				if (x == roomNum) {
					Util.Stack(gui, slot, material[1], 1, "§e" + rooms[x] + " §f레버", "§7위로 올려 전력을 공급하세요."); // 클릭가능 레버
				} else {
					Util.Stack(gui, slot, material[0], 1, "§e" + rooms[x] + " §f레버", "§4클릭불가"); // 클릭불가 레버
				}
			} else if (x == 4) {
				Util.Stack(gui, slot, material[3], 1, " ");
			} else if (y == 1 || y == 2)
				Util.Stack(gui, slot, material[2], 1, " ");
			else if (y == 0)
				Util.Stack(gui, slot, material[5], 1, " ");
		}
		p.openInventory(gui);
	}

	public void reset2() {
		for (int slot = 0; slot < 27; slot++) {// gui인벤토리
			int y = slot / 9, x = slot % 9;
			if (y == 0 || y == 2)
				Util.Stack(gui, slot, material[4], 1, " ");
			else if (x < 4)
				Util.Stack(gui, slot, material[2], 1, " ");
			else if (x == 4)
				Util.Stack(gui, slot, Material.OAK_FENCE_GATE, 1, "§f레버", "§a클릭해 전력을 연결하세요");
			else
				Util.Stack(gui, slot, material[3], 1, "§ ");
		}
		p.openInventory(gui);
	}

	public void checkLever(int num) {
		Util.debugMessage(num + "에 checklever 실행");
		if (num / 9 == 3) {
			Util.debugMessage("0번");
			Util.Stack(gui, num - 27, material[2], 1, " ");
			Util.Stack(gui, num - 18, material[2], 1, " ");
			for (int slot = 0; slot < 18; slot++) {
				if (slot % 9 != num % 9)
					Util.Stack(gui, slot, material[5], 1, " ");
			} // 나머지 전기 1단계
			clear(1); // 클리어
		}
		if (num / 9 == 4) {
			Util.debugMessage("1번");
			for (int slot = 0; slot < 27; slot++) {
				if (slot / 9 == 0)
					Util.Stack(gui, slot, material[5], 1, " ");// 전기 2단계
				else
					Util.Stack(gui, slot, material[2], 1, " ");
			}
		} else if (num / 9 == 5) {
			Util.debugMessage("2번");
			Util.Stack(gui, num - 36, material[5], 1, " ");
			Util.Stack(gui, num - 45, material[5], 1, " ");
			for (int slot = 0; slot < 18; slot++)
				if (slot % 9 != num % 9)
					Util.Stack(gui, slot, material[2], 1, " ");// 나머지 전기 3단계
		}
		for (int slot = 4; slot < 24; slot += 9)
			Util.Stack(gui, slot, material[3], 1, " ");
		new BukkitRunnable() {

			@Override
			public void run() {
				p.updateInventory();

			}
		}.runTaskLater(main, 0);

	}

	public void clear(int part) {
		switch (part) {
		case 1:
			/*
			 * 파트1 클리어;
			 * 
			 */
			break;
		case 2:
			for (int slot = 13; slot < 18; slot++)
				Util.Stack(gui, slot, material[2], 1, " ");
			/*
			 * 파트2 클리어;
			 * 
			 */
			break;
		}
	}

	@EventHandler
	public void onClick(InventoryClickEvent e) {
		if (e.getView().getTitle().split(" ")[0].equals("DivertPower")) {
			Util.debugMessage((e.getCurrentItem() == null) + "," + (e.getCursor().getType() == material[1]) + "클릭 인식됨");
			int slot = e.getRawSlot();
			ItemStack itemstack = e.getCurrentItem();
			if (e.getCurrentItem() != null) {
				Material item = itemstack.getType();
				if (item == material[1])
					;
				else if (item == Material.OAK_FENCE_GATE) {
					clear(2);
					e.setCancelled(true);
				} else
					e.setCancelled(true);
			} else {
				Util.debugMessage("공기 클릭함");
				if (e.getCursor().getType() == material[1]) {
					Util.debugMessage("레드스톤 블럭 들고있음");
					checkLever(slot);
				}

			}
		}

	}

	@EventHandler
	public void onDrag(InventoryDragEvent e) {

		if (e.getView().getTitle().equals("DivertPower") && !e.getRawSlots().isEmpty()) {
			Util.debugMessage("드래그 인식됨");
			for (int slot : e.getRawSlots()) {
				if (gui.getItem(slot) == null) {
					checkLever(slot);
					break;
				}
			}
		}

	}

}
