package bepo.au.sabo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import bepo.au.base.Sabotage;
import bepo.au.utils.Util;

import java.util.UUID;

public class S_Fingerprint extends Sabotage {
	
	private final int maxslot = 54;
	private final String guiName = "S_Fingerprint";
	public static ArrayList<UUID> upperPlayerList= new ArrayList<>();
	public static ArrayList<UUID> lowerPlayerList= new ArrayList<>();

	public static boolean Activated = false;
	
	public S_Fingerprint(MissionType mt2, String name, String korean, int clear, Location loc) {
		super(mt2, name, korean, clear, loc, SaboType.NUCL, 0);
	}
	
	public void onRestart() {
		
	}
	
	public void onAssigned(Player p) {
		assign(p);
		initialize_Fingerprint(p);
	}
	
	public void onStart(Player p, int i) {
		s_fingerprintOpen(p, i == 0);
	}
	
	public void onStop(Player p, int i) {
		
	}
	
	public void onClear(Player p, int i) {
		Activated = false;
		saboGeneralClear();
	}
	
	/*
	 * ��ɾ� ���� �� �����, GUI ���� �õ�.
	 */
	public void s_fingerprintOpen(Player pl, boolean isLocationUpper) {
		if (Activated) {
			pl.openInventory(gui.get(isLocationUpper ? 0 : 1));
		} else {
			Util.debugMessage("���ڷ� �纸Ÿ�ְ� ���� ���۵��� �ʾҽ��ϴ�");
		}
	}

	/*
	 * ��ɾ� ���� �� �����. �纸Ÿ�� ����.
	 */

	/*
	 * �ʱ�ȭ ; GUI�� ������.
	 */
	private void initialize_Fingerprint(Player p) {
		for(int i=0;i<2;i++) {
			uploadInventory(p, maxslot, guiName + " " + i);
			gui.get(i).setMaxStackSize(1);
		}
		
		setGUI(true); setGUI(false);
		
		if(Activated == false) {
			Activated = true;
			upperPlayerList.clear();
			lowerPlayerList.clear();
		}
		
		
		 // GUI �����
	}

	/*
	 * GUI�� �����.
	 */

	private void setGUI(boolean isUpper) {
		
		List<String> lore = Arrays.asList("��7Ŭ���� Ȱ��ȭ�ϼ���");
		for (int slot = 0; slot < maxslot; slot++) {
			Util.Stack(gui.get(isUpper ? 0 : 1), slot, Material.RED_STAINED_GLASS, 1, "��4Ȱ��ȭ �Ǿ����� ����", lore);
		}
		
		
	}

	/*
	 * Ȱ��ȭ
	 */

	private void activate(boolean isUpper, UUID id) {
		Util.debugMessage(isUpper + " Ȱ��ȭ");
		List<String> lore = Arrays.asList("��7�κ��丮�� ������ Ȱ��ȭ�� Ǯ���ϴ�.");
		if(isUpper) {
			upperPlayerList.add(id);
		} else {
			lowerPlayerList.add(id);
		}
		for (int slot = 0; slot < maxslot; slot++) {
			Util.Stack(gui.get(isUpper ? 0 : 1), slot, Material. BLUE_STAINED_GLASS, 1, "��bȰ��ȭ ��",lore);
		}
		check();
	}
	/*
	 * ��Ȱ��ȭ
	 */
	private void deactivate(boolean isUpper, UUID id) {
		Util.debugMessage(isUpper + " ��Ȱ��ȭ");
		
		if(isUpper) {
			upperPlayerList.remove(id);
		} else {
			lowerPlayerList.remove(id);
		}
		setGUI(isUpper);
	}
	/*
	 * Ŭ���� Ȯ��
	 */
	private void check() {
		if(upperPlayerList.size()!=0&&lowerPlayerList.size()!=0){
		Util.debugMessage("�纸Ÿ�� Ŭ����");
		Activated = false;
		Sabotage.saboClear(0);
		}
	}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	@EventHandler
	private void onClick(InventoryClickEvent e) {
		if(!checkPlayer(e)) return;
			Util.debugMessage("Ŭ�� �νĵ�");
			ItemStack itemstack = e.getCurrentItem();
			Player p = (Player) e.getWhoClicked();
			if (e.getClick().equals(ClickType.DOUBLE_CLICK) || e.isShiftClick() == true) { // ����Ŭ��,����ƮŬ�� ����
				Util.debugMessage("���� Ŭ�� �Ұ�");
				e.setCancelled(true);
				return;
			}
			if (itemstack != null) {
				if (itemstack.getType()==Material.RED_STAINED_GLASS) {
						Util.debugMessage("������ Ŭ��");
						activate(getCode(e.getView().getTitle()) == 0 ? true : false, p.getUniqueId());
						e.setCancelled(true);
					} else {
						Util.debugMessage("Ŭ�� �Ұ�");
						e.setCancelled(true);
					}
				}
		}
	
	@EventHandler
	private void onClose(InventoryCloseEvent e) {
		if(!Activated || !checkPlayer(e)) return;
			Bukkit.broadcastMessage("�ݱ� �ߵ� : " + e.getPlayer().getName());
			deactivate(getCode(e.getView().getTitle()) == 0 ? true : false, e.getPlayer().getUniqueId());
	}

}
