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
	 * ��ɾ� ���� �� �����, GUI ���� �õ�.
	 */
	public void stablizeSteeringOpen(Player pl, Main m) {

		main = m;
		p = pl;
		initialize_stablizeSteering();
		p.openInventory(gui);

	}

	/*
	 * �ʱ�ȭ ; GUI�� ����.
	 */
	private void initialize_stablizeSteering() {
		int x, y;
		while (true) {
			x = random.nextInt(9);
			y = random.nextInt(5);
			if (!(2 < x && x < 5 && 0 < y && y < 4))
				break; // ��� 3x3ĭ�� ����
		}
		int elytraSlot = x + y * 9;
		gui = Bukkit.createInventory(p, maxslot, guiName);
		gui.setMaxStackSize(1);
		setGUI(elytraSlot); // GUI �����

	}
	/*
	 * GUI�� �����.
	 */

	private void setGUI(int elytraSlot) {
		List<String> lore = Arrays.asList("��7�������� �� ����� �� �������� �ű⼼��.");
		for (int slot = 0; slot < maxslot; slot++) {
			// int x = slot % 9, y = slot / 9;
			if (slot == 22)
				; // ����� ��ĭ
			else if (slot == 21 || slot == 23 || slot == 13 || slot == 31) { // ���� 4ĭ�� �Ͼ��
				Util.Stack(gui, slot, Material.WHITE_STAINED_GLASS_PANE, 1, " ");
			} else if (slot == elytraSlot) { // �ѳ��� ������ġ
				Util.Stack(gui, slot, Material.ELYTRA, 1, "��f������", lore);
			} else {
				Util.Stack(gui, slot, Material.GRAY_STAINED_GLASS_PANE, 1, " ");
			}

		}
	}

	/*
	 * Ŭ���� Ȯ��
	 */
	private void check() {
		new BukkitRunnable() {
			public void run() {
				if(gui.getItem(22).getType()==Clickable[0]) {
					Util.debugMessage("Ŭ����!");
				}
			}
		}.runTaskLater(main, 0L);
	}

	

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	@EventHandler
	private void onClick(InventoryClickEvent e) {
		if (e.getView().getTitle().equals(guiName)) {
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
				if (Arrays.asList(Clickable).contains(itemstack.getType())) {
					;
				} else {
					Util.debugMessage("Ŭ�� �Ұ�");
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
