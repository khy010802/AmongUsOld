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

	int roomNum = -1;

	final Material[] material = { Material.RED_STAINED_GLASS, // ����0
			Material.REDSTONE_BLOCK, // Ȱ��ȭ�� ����1
			Material.YELLOW_STAINED_GLASS_PANE, // ����2
			Material.GRAY_STAINED_GLASS_PANE, // ȸ�����3
			Material.WHITE_STAINED_GLASS_PANE, // �Ͼ���4
			Material.LIGHT_GRAY_STAINED_GLASS_PANE// ��ȸ��5
	};
	final String[] rooms = { "�Ϻ� ����", // ����
			"��� ����", "�����", "��ȣ�� �����", "���ǵ��� ���� ��", "���ؽ�", "��Ž�", "��� ���޽�", "���Ƚ�" };// ���

	public E_DivertPower(MissionType mt2, String name, String korean, int clear, Location loc) {
		super(mt2, name, korean, clear, loc);
	}
	
	public void onAssigned(Player p) {
		assign(p);
		uploadInventory(p, 54, "DivertPower 0");
		uploadInventory(p, 27, "DivertPower 1");
		roomNum = Util.random(1, 7) - 1;
		if(roomNum >= 4) roomNum++;
		
		Location elec = locs[0];
		Location loc1 = locs[roomNum+1];
		locs = new Location[] { elec, loc1 };
	}
	
	public void onStart(Player p, int i) {
		if(i == 0) reset1(p);
		else if(cleared.contains((Integer) 0)) reset2(p);
	}
	
	public void onStop(Player p, int i) {
		//p.getInventory().remove(Material.ELYTRA);
	}
	
	public void onClear(Player p, int i) {
		generalClear(p, i);
	}

	public void reset1(Player p) {
		for (int slot = 0; slot < 54; slot++) {// gui�κ��丮
			int y = slot / 9, x = slot % 9;
			Util.debugMessage(slot + "�� �������� ä��ϴ�" + x + "," + y);
			if (y == 3 || y == 5)
				Util.Stack(gui.get(0), slot, x == roomNum ? Material.AIR : material[3], 1, " "); // ��� ������ ä���
			else if (x == 4) {
				Util.Stack(gui.get(0), slot, material[3], 1, " ");// ��� ������ ä���
			} else if (y == 4) { // y�� 7�϶�
				if (x == roomNum) {
					Util.Stack(gui.get(0), slot, material[1], 1, "��e" + rooms[x] + " ��f����", "��7���� �÷� ������ �����ϼ���."); // Ŭ������ ����
				} else {
					Util.Stack(gui.get(0), slot, material[0], 1, "��e" + rooms[x] + " ��f����", "��4Ŭ���Ұ�"); // Ŭ���Ұ� ����
				}
			} else if (x == 4) {
				Util.Stack(gui.get(0), slot, material[3], 1, " ");
			} else if (y == 1 || y == 2)
				Util.Stack(gui.get(0), slot, material[2], 1, " ");
			else if (y == 0)
				Util.Stack(gui.get(0), slot, material[5], 1, " ");
		}
		p.openInventory(gui.get(0));
	}

	public void reset2(Player p) {
		for (int slot = 0; slot < 27; slot++) {// gui�κ��丮
			int y = slot / 9, x = slot % 9;
			if (y == 0 || y == 2)
				Util.Stack(gui.get(1), slot, material[4], 1, " ");
			else if (x < 4)
				Util.Stack(gui.get(1), slot, material[2], 1, " ");
			else if (x == 4)
				Util.Stack(gui.get(1), slot, Material.OAK_FENCE_GATE, 1, "��f����", "��aŬ���� ������ �����ϼ���");
			else
				Util.Stack(gui.get(1), slot, material[3], 1, "�� ");
		}
		p.openInventory(gui.get(1));
	}

	public void checkLever(Player p, int num) {
		Util.debugMessage(num + "�� checklever ����");
		if (num / 9 == 3) {
			Util.debugMessage("0��");
			Util.Stack(gui.get(0), num - 27, material[2], 1, " ");
			Util.Stack(gui.get(0), num - 18, material[2], 1, " ");
			for (int slot = 0; slot < 18; slot++) {
				if (slot % 9 != num % 9)
					Util.Stack(gui.get(0), slot, material[5], 1, " ");
			} // ������ ���� 1�ܰ�
			clear(p, 1); // Ŭ����
		}
		if (num / 9 == 4) {
			Util.debugMessage("1��");
			for (int slot = 0; slot < 27; slot++) {
				if (slot / 9 == 0)
					Util.Stack(gui.get(0), slot, material[5], 1, " ");// ���� 2�ܰ�
				else
					Util.Stack(gui.get(0), slot, material[2], 1, " ");
			}
		} else if (num / 9 == 5) {
			Util.debugMessage("2��");
			Util.Stack(gui.get(0), num - 36, material[5], 1, " ");
			Util.Stack(gui.get(0), num - 45, material[5], 1, " ");
			for (int slot = 0; slot < 18; slot++)
				if (slot % 9 != num % 9)
					Util.Stack(gui.get(0), slot, material[2], 1, " ");// ������ ���� 3�ܰ�
		}
		for (int slot = 4; slot < 24; slot += 9)
			Util.Stack(gui.get(0), slot, material[3], 1, " ");
		new BukkitRunnable() {

			@Override
			public void run() {
				p.updateInventory();

			}
		}.runTaskLater(main, 0);

	}

	public void clear(Player p, int part) {
		switch (part) {
		case 1:
			onClear(p, 0);
			break;
		case 2:
			for (int slot = 13; slot < 18; slot++)
				Util.Stack(gui.get(1), slot, material[2], 1, " ");
			onClear(p, 1);
			break;
		}
	}

	@EventHandler
	public void onClick(InventoryClickEvent e) {
		
		if(!checkPlayer(e)) return;
		
		Player p = (Player) e.getWhoClicked();
		
			Util.debugMessage((e.getCurrentItem() == null) + "," + (e.getCursor().getType() == material[1]) + "Ŭ�� �νĵ�");
			int slot = e.getRawSlot();
			ItemStack itemstack = e.getCurrentItem();
			if (e.getCurrentItem() != null) {
				Material item = itemstack.getType();
				if (item == material[1])
					;
				else if (item == Material.OAK_FENCE_GATE) {
					clear(p, 2);
					e.setCancelled(true);
				} else
					e.setCancelled(true);
			} else {
				Util.debugMessage("���� Ŭ����");
				if (e.getCursor().getType() == material[1]) {
					Util.debugMessage("���彺�� �� �������");
					checkLever(p, slot);
				}

			}

	}

	@EventHandler
	public void onDrag(InventoryDragEvent e) {

		if(!checkPlayer(e)) return;
		
		if (!e.getRawSlots().isEmpty()) {
			Util.debugMessage("�巡�� �νĵ�");
			for (int slot : e.getRawSlots()) {
				if (gui.get(0).getItem(slot) == null) {
					checkLever((Player) e.getWhoClicked(), slot);
					break;
				}
			}
		}

	}

}
