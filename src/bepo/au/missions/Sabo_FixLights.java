package bepo.au.missions;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;


import java.util.Random;

public class Sabo_FixLights implements Listener {
	Main main;
	Player p;
	Random random = new Random();
	int maxslot = 45;
	Inventory gui;
	static boolean started = false;
	static boolean[] leverStatus = new boolean[5];
	static boolean[] connected = new boolean[5];

	/*
	 * ���ɾ� ���� �� �����, gui ���� �õ�.
	 */
	public void s_fixLights(Player pl, Main m) {
		
		main = m;
		p = pl;
		if (started) {
			p.openInventory(gui);
		} else {
			Util.debugMessage("���� �纸Ÿ�ִ� ���� ���۵��� �ʾҽ��ϴ�");
		}
	}

	/*
	 * ���ɾ� ���� �� �����
	 */
	public void s_fixLightsStart() {
		if (started) {
			Util.debugMessage("���� �纸Ÿ�ִ� �̹� ���۵Ǿ����ϴ�.");
		} else {
			initialize_fixLights();
			Util.debugMessage("���� �纸Ÿ�� �����");
		}
	}

	/*
	 * �ʱ�ȭ ; gui�� ����.
	 */
	public void initialize_fixLights() {
		started = true;
		gui = Bukkit.createInventory(null, maxslot, "S_FixLights");
		gui.setMaxStackSize(1);
		for (int i = 0; i < 5; i++)
			connected[i] = false;
		int init_connected = 1 + random.nextInt(4); // ����� ���� ���� Ȯ���� �� ŭ
		for (int i = 0; i < init_connected; i++)
			connected[random.nextInt(5)] = true; // ����� ������ 1~4��
		for (int i = 0; i < 5; i++) {
			leverStatus[i] = random.nextBoolean();
		} // ������ ���´� ����
		setGUI(); // gui �����
	}
	/*
	 * GUI�� �����.
	 */

	public void setGUI() {
		List<String> lore = Arrays.asList("��7�Ʒ��� ������ �������� �� ������ Ŭ�����ּ���.");
		for (int slot = 0; slot < maxslot; slot++) {
			int x = slot % 9, y = slot / 9;

			if (x % 2 == 0 && y == 1)
				Util.Stack(gui, slot,
						(leverStatus[x / 2] ? Material.RED_STAINED_GLASS_PANE : Material.BLUE_STAINED_GLASS_PANE), 1,
						"��f����");
			else if (x % 2 == 0 && y == 3)
				Util.Stack(gui, slot, (connected[x / 2] ? Material.LANTERN : Material.SOUL_LANTERN), 1,
						(connected[x / 2] ? "��a�����" : "��4����ȵ�"));
			else if (x == 4 && y == 4)
				Util.Stack(gui, slot, Material.BOOK, 1, "��f���� ����", lore);
			else
				Util.Stack(gui, slot, Material.WHITE_STAINED_GLASS_PANE, 1, " ");
		}
	}

	/*
	 * ������ ���
	 */
	public void toggleLever(int idx) {
		Util.debugMessage("���� ���");
		connected[idx] = !connected[idx];
		leverStatus[idx] = !leverStatus[idx];
		updateGUI();
		check();
		
	}

	/*
	 * Ŭ���� Ȯ��
	 */
	public void check() {
		for (Boolean connection : connected) {
			Util.debugMessage(connection.toString());
			if (!connection)
				return;
		}
		Util.debugMessage("�纸Ÿ�� Ŭ����");
		started=false;
		for (HumanEntity he : gui.getViewers()) {((Player) he).closeInventory();}
	}

	/*
	 * gui�� �����ִ� ��� �÷��̾� ������Ʈ
	 */
	public void updateGUI() {
		Util.debugMessage("GUI ������Ʈ");
		for (int slot = 9; slot < 18; slot += 2)
			Util.Stack(gui, slot,
					(leverStatus[slot % 9 / 2] ? Material.RED_STAINED_GLASS_PANE : Material.BLUE_STAINED_GLASS_PANE), 1,
					"��f����");
		for (int slot = 27; slot < 36; slot += 2)
			Util.Stack(gui, slot, (connected[slot % 9 / 2] ? Material.LANTERN : Material.SOUL_LANTERN), 1,
					(connected[slot % 9 / 2] ? "��a�����" : "��4����ȵ�"));
		for (HumanEntity he : gui.getViewers()) {((Player) he).updateInventory();}  //�÷��̺� �κ� ������Ʈ
		
	}
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	@EventHandler
	public void onClick(InventoryClickEvent e) {
		if (e.getView().getTitle().equals("S_FixLights")) {
			Util.debugMessage("Ŭ�� �νĵ�");
			int slot = e.getRawSlot();
			ItemStack itemstack = e.getCurrentItem();

			// Inventory gui = e.getClickedInventory();
			// Player p = (Player) e.getWhoClicked();

			if (e.getClick().equals(ClickType.DOUBLE_CLICK) || e.isShiftClick() == true) { // ����Ŭ��,����ƮŬ�� ����
				Util.debugMessage("���� Ŭ�� �Ұ�");
				e.setCancelled(true);
			}
			if (itemstack != null) {
				if (itemstack.getType() == Material.BLUE_STAINED_GLASS_PANE
						|| itemstack.getType() == Material.RED_STAINED_GLASS_PANE) {
					Util.debugMessage("���� Ŭ�� �νĵ�");
					toggleLever(slot % 9 / 2);
					e.setCancelled(true);
				} else {
					Util.debugMessage("Ŭ�� �Ұ�");
					e.setCancelled(true);
				}
			}
		}
	}
	/*
	@EventHandler
	public void onDrag(InventoryEvent e) {
		if (e.getView().getTitle().equals("EmptyChute") && !e.getRawSlots().isEmpty()) {
			;
		}

	}*/

}