package bepo.au.missions;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import bepo.au.Util;
import bepo.au.base.Mission;
import java.util.Random;

public class E_StablizeSteering extends Mission {

	Random random = new Random();
	private final int maxslot = 45;
	private final String guiName = "StablizeSteering";
	private Material[] Clickable = {Material.ELYTRA};
	/*
	 * ��ɾ� ���� �� �����, GUI ���� �õ�.
	 */
	
	public E_StablizeSteering(MissionType mt, String name, String korean, int clear, Location loc) {
		super(mt, name, korean, clear, loc);
	}
	
	@Override
	public void onAssigned(Player p) {
		assign(p);
		uploadInventory(p, maxslot, guiName);
		int x, y;
		while (true) {
			x = random.nextInt(9);
			y = random.nextInt(5);
			if (!(2 < x && x < 5 && 0 < y && y < 4))
				break; // ��� 3x3ĭ�� ����
		}
		int elytraSlot = x + y * 9;
		setGUI(elytraSlot); // GUI �����

	}
	
	@Override
	public void onStart(Player p, int code) {
		p.openInventory(gui.get(0));
	}
	
	@Override
	public void onStop(Player p, int code) {
		p.getInventory().remove(Material.ELYTRA);
	}
	
	@Override
	public void onClear(Player p, int code) {
		generalClear(p, code);
	}

	private void setGUI(int elytraSlot) {
		List<String> lore = Arrays.asList("��7�������� �� ����� �� �������� �ű⼼��.");
		for (int slot = 0; slot < maxslot; slot++) {
			// int x = slot % 9, y = slot / 9;
			if (slot == 22)
				; // ����� ��ĭ
			else if (slot == 21 || slot == 23 || slot == 13 || slot == 31) { // ���� 4ĭ�� �Ͼ��
				Util.Stack(gui.get(0), slot, Material.WHITE_STAINED_GLASS_PANE, 1, " ");
			} else if (slot == elytraSlot) { // �ѳ��� ������ġ
				Util.Stack(gui.get(0), slot, Material.ELYTRA, 1, "��f������", lore);
			} else {
				Util.Stack(gui.get(0), slot, Material.GRAY_STAINED_GLASS_PANE, 1, " ");
			}

		}
	}

	/*
	 * Ŭ���� Ȯ��
	 */
	private void check(Player p) {
		new BukkitRunnable() {
			public void run() {
				if(gui.get(0).getItem(22).getType()==Clickable[0]) {
					Util.debugMessage("Ŭ����!");
					onClear(p, 1);
				}
			}
		}.runTaskLater(main, 0L);
	}

	

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	@EventHandler
	private void onClick(InventoryClickEvent e) {
		if(!checkPlayer(e)) return;

		Util.debugMessage("Ŭ�� �νĵ�");
		ItemStack itemstack = e.getCurrentItem();

		// Inventory gui = e.getClickedInventory();
		// Player p = (Player) e.getWhoClicked();

		if (e.getClick().equals(ClickType.DOUBLE_CLICK) || e.isShiftClick() == true) { // ����Ŭ��,����ƮŬ�� ����
			Util.debugMessage("���� Ŭ�� �Ұ�");
			e.setCancelled(true);
		}
		if (itemstack != null) {
			if (Arrays.asList(Clickable).contains(itemstack.getType())) {
				
			} else {
				Util.debugMessage("Ŭ�� �Ұ�");
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
