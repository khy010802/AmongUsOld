package bepo.au.missions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class Sabo_Fingerprint implements Listener {
	Main main;
	Player p;
	private final int maxslot = 54;
	private Inventory gui;
	private final String guiName = "S_Fingerprint";
	private static boolean started = false;
	private boolean isUpper;// true: ����
	private static ArrayList<UUID> upperPlayerList= new ArrayList<>();
	private static ArrayList<UUID> lowerPlayerList= new ArrayList<>();

	/*
	 * ��ɾ� ���� �� �����, GUI ���� �õ�.
	 */
	public void s_fingerprintOpen(Player pl, Main m, boolean isLocationUpper) {
		main = m;
		p = pl;
		isUpper = isLocationUpper;
		if (started) {
			p.openInventory(gui);
		} else {
			Util.debugMessage("���ڷ� �纸Ÿ�ְ� ���� ���۵��� �ʾҽ��ϴ�");
		}
	}

	/*
	 * ��ɾ� ���� �� �����. �纸Ÿ�� ����.
	 */

	public void s_fingerprintStart() {
		if (started) {
			Util.debugMessage("���ڷ� �纸Ÿ�ִ� �̹� ���۵Ǿ����ϴ�.");
		} else {
			initialize_Fingerprint();
			Util.debugMessage("���ڷ� �纸Ÿ�� �����");
		}
	}

	/*
	 * �ʱ�ȭ ; GUI�� ������.
	 */
	private void initialize_Fingerprint() {
		started = true;
		upperPlayerList.clear();
		lowerPlayerList.clear();
		gui = Bukkit.createInventory(p, maxslot, guiName);
		gui.setMaxStackSize(1);
		setGUI(); // GUI �����
	}

	/*
	 * GUI�� �����.
	 */

	private void setGUI() {
		List<String> lore = Arrays.asList("��7Ŭ���� Ȱ��ȭ�ϼ���");
		for (int slot = 0; slot < maxslot; slot++) {
			Util.Stack(gui, slot, Material.RED_STAINED_GLASS, 1, "��4Ȱ��ȭ �Ǿ����� ����", lore);
		}
	}

	/*
	 * Ȱ��ȭ
	 */

	private void activate(UUID id) {
		Util.debugMessage(isUpper + " Ȱ��ȭ");
		List<String> lore = Arrays.asList("��7�κ��丮�� ������ Ȱ��ȭ�� Ǯ���ϴ�.");
		for (int slot = 0; slot < maxslot; slot++) {
			Util.Stack(gui, slot, Material. BLUE_STAINED_GLASS, 1, "��4Ȱ��ȭ �Ǿ����� ����",lore);
			if(isUpper) {
			upperPlayerList.add(p.getUniqueId());
			}else {
			lowerPlayerList.add(p.getUniqueId());
			}
		}
		check();
	}
	/*
	 * ��Ȱ��ȭ
	 */
	private void deactivate(UUID id) {
		Util.debugMessage(isUpper + " ��Ȱ��ȭ");
		setGUI();
		if(isUpper) {
			upperPlayerList.remove(p.getUniqueId());
			}else {
			lowerPlayerList.remove(p.getUniqueId());
			}
	}
	/*
	 * Ŭ���� Ȯ��
	 */
	private void check() {
		if(upperPlayerList.size()!=0&&lowerPlayerList.size()!=0){
		Util.debugMessage("�纸Ÿ�� Ŭ����");
		started = false;
		}
	}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	@EventHandler
	private void onClick(InventoryClickEvent e) {
		if (e.getView().getTitle().equals(guiName)) {
			Util.debugMessage("Ŭ�� �νĵ�");
			ItemStack itemstack = e.getCurrentItem();
			Player p = (Player) e.getWhoClicked();
			if (e.getClick().equals(ClickType.DOUBLE_CLICK) || e.isShiftClick() == true) { // ����Ŭ��,����ƮŬ�� ����
				Util.debugMessage("���� Ŭ�� �Ұ�");
				e.setCancelled(true);
			}
			if (itemstack != null) {
				if (itemstack.getType()==Material.RED_STAINED_GLASS) {
						Util.debugMessage("������ Ŭ��");
						activate(p.getUniqueId());
						e.setCancelled(true);
					}

				} else {
					Util.debugMessage("Ŭ�� �Ұ�");
					e.setCancelled(true);
				}
			}
		}
	
@EventHandler
	private void onClose(InventoryCloseEvent e) {
		if(started&&p.getOpenInventory().getTitle()==guiName) {
			deactivate(e.getPlayer().getUniqueId());
		}
	}

}
