package bepo.au.missions;

import java.util.Arrays;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import org.bukkit.event.EventHandler;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import bepo.au.base.Mission;
import bepo.au.Util;

public class E_FixWriting extends Mission {

	public E_FixWriting(MissionType mt2, String name, String korean, int clear, Location... locs) {
		super(mt2, name, korean, clear, locs);
	}

	static int[][] wirecolorArray;
	static boolean[][] connected = { { false, false, false, false }, { false, false, false, false },
			{ false, false, false, false } };
	static Material[][] WIRECOLORARRAY = {
			{ Material.RED_STAINED_GLASS_PANE, Material.BLUE_STAINED_GLASS_PANE, Material.GREEN_STAINED_GLASS_PANE,
					Material.PURPLE_STAINED_GLASS_PANE },

			{ Material.RED_STAINED_GLASS_PANE, Material.BLUE_STAINED_GLASS_PANE, Material.GREEN_STAINED_GLASS_PANE,
					Material.PURPLE_STAINED_GLASS_PANE },

			{ Material.RED_STAINED_GLASS_PANE, Material.BLUE_STAINED_GLASS_PANE, Material.GREEN_STAINED_GLASS_PANE,
					Material.PURPLE_STAINED_GLASS_PANE } };

	@Override
	public void onAssigned(Player p) {
		wirecolorArray = new int[][] { Util.difrandom(0, 3, 4), Util.difrandom(0, 3, 4), Util.difrandom(0, 3, 4) };
		for (int i = 0; i < 3; i++) {
			uploadInventory(p, 54, "FixWriting" + i);

			for (int slot = 0; slot < 54; slot++) {// gui�κ��丮
				int y = slot / 9, x = slot % 9;
				if (y == 0 || y == 2 || y == 3 || y == 5) {
					Util.debugMessage(" wirecolorArray Ȯ�� " + wirecolorArray[i][yToidx(y)]);
					if (x == 8)
						Util.Stack(gui.get(i), slot, Material.BLACK_STAINED_GLASS_PANE, 1, " ");// ������ǥ��
					else if (x == 0)
						Util.Stack(gui.get(i), slot, Material.YELLOW_STAINED_GLASS_PANE, 1, " ");// ��� ǥ��
					else if (x == 1 || x == 2)
						fillWire(gui.get(i), slot, wirecolorArray[i][yToidx(y)], x, i);// ���� ���̾� ä���(����)
					else if (x == 7)
						fillWire(gui.get(i), slot, yToidx(y), i); // ������ ���̾� ä���(����)
					else if (x == 6)
						; // ������ ���̾� �����
					else
						Util.Stack(gui.get(i), slot, Material.WHITE_STAINED_GLASS_PANE, 1, " "); // ���
				} else
					Util.Stack(gui.get(i), slot, Material.WHITE_STAINED_GLASS_PANE, 1, " "); // ���
			}
		}

	}

	@Override
	public void onStart(Player p, int i) {
		p.openInventory(gui.get(i));
	}

	@Override
	public void onStop(Player p, int i) {
		for (Material mat : WIRECOLORARRAY[0]) {
			p.getInventory().remove(mat);
		}
	}

	@Override
	public void onClear(Player p, int i) {
		generalClear(p, i);
	}

	public static int yToidx(int y) {
		int idx = -1;
		switch (y) {
		case 0:
			idx = 0;
			break;
		case 2:
			idx = 1;
			break;
		case 3:
			idx = 2;
			break;
		case 5:
			idx = 3;
			break;
		}
		return idx;
	}

	public static void fillWire(Inventory gui, int slot, int color, int num, int code) {
		List<String> lore = (num == 1 ? Arrays.asList("��4Ŭ���Ұ�") : Arrays.asList("��7��Ŭ���� �����մϴ�."));
		switch (color) {
		case 0:
			Util.Stack(gui, slot, WIRECOLORARRAY[code][0], num, "��cRed ��fWire", lore);
			break;
		case 1:
			Util.Stack(gui, slot, WIRECOLORARRAY[code][1], num, "��9Blue ��fWire", lore);
			break;
		case 2:
			Util.Stack(gui, slot, WIRECOLORARRAY[code][2], num, "��aGreen ��fWire", lore);
			;
			break;
		case 3:
			Util.Stack(gui, slot, WIRECOLORARRAY[code][3], num, "��dPurple ��fWire", lore);
			break;
		case -1:
			gui.setItem(slot, new ItemStack(Material.BARRIER, num));
			break;
		}
	}

	public static void fillWire(Inventory inv, int slot, int color, int code) {
		fillWire(inv, slot, color, 1, code);
	}

	public void checkConnection(Player p, int code, int slot) {
		new BukkitRunnable() {
			public void run() {
				if (slot % 9 == 6 && slot < 54 && slot / 9 != 1 && slot / 9 != 4) {
					int idx = yToidx(slot / 9);
					Util.debugMessage(slot + "���� ���� Ȯ��");
					if (!(gui.get(code).getItem(slot) == null)
							&& gui.get(code).getItem(slot).getType() == WIRECOLORARRAY[code][idx]) {
						Util.debugMessage("�����" + (slot + 2) + "�� ��� ����");
						Util.Stack(gui.get(code), slot + 2, Material.YELLOW_STAINED_GLASS_PANE, 1, " ");// ���� ���� ǥ��
						connected[code][idx] = true;
						for (int i = 0; i < 4; i++)
							if (connected[code][i] == false) {
								Util.debugMessage(i + "�� ����ȵ�");
								return;
							}
						onClear(p, code);
					} else {
						Util.debugMessage("����ȵ�" + (slot + 2) + "�� ���� ����");
						Util.Stack(gui.get(code), slot + 2, Material.BLACK_STAINED_GLASS_PANE, 1, " ");// ���� ���� ǥ��
						connected[code][idx] = false;
					}
				}
			}
		}.runTaskLater(main, 0L);

	}

	@EventHandler
	public void onClick(InventoryClickEvent e) {

		String title = e.getView().getTitle();
		if (title.contains("FixWiring")) {
			Util.debugMessage("Ŭ�� �νĵ�");
			int slot = e.getRawSlot();
			int code = Integer.parseInt(title.replace("FixWriting", ""));
			ItemStack itemstack = e.getCurrentItem();

			// Inventory gui = e.getClickedInventory();
			// Player p = (Player) e.getWhoClicked();

			if (e.isRightClick())
				Util.debugMessage("��Ŭ�� �νĵ�");
			if (e.getCurrentItem() != null) {
				if (!e.isRightClick() || // ��Ŭ���� ���
						(slot % 9 != 2 && slot % 9 != 6) || // Ŭ�� ������ x��ǥ
						slot / 9 == 1 || slot / 9 == 4 // Ŭ�� �Ұ��� y��ǥ
				) { //
					Util.debugMessage("Ŭ�� �Ұ�");
					e.setCancelled(true);
				}
				if ((e.getCursor().getType() != Material.AIR || itemstack.getAmount() == 1)
						&& (slot % 9 == 1 || slot % 9 == 2 || slot % 9 == 7)) {// ������ �ϳ��Ͻ� Ŭ�� �Ұ� &
					Util.debugMessage("Ŭ�� �Ұ�");
					e.setCancelled(true);
				}
			}
			checkConnection(code, slot);
		}
	}

	@EventHandler
	public void onDrag(InventoryDragEvent e) {

		String title = e.getView().getTitle();
		if (!title.contains("FixWiring"))
			return;
		int code = Integer.parseInt(title.replace("FixWriting", ""));
		if (!e.getRawSlots().isEmpty()) {
			for (int slot : e.getRawSlots()) {
				checkConnection(code, slot);
			}
		}

	}
}