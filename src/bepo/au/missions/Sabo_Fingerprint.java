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
	private boolean isUpper;// true: 위쪽
	private static ArrayList<UUID> upperPlayerList= new ArrayList<>();
	private static ArrayList<UUID> lowerPlayerList= new ArrayList<>();

	/*
	 * 명령어 쳤을 때 실행됨, GUI 열기 시도.
	 */
	public void s_fingerprintOpen(Player pl, Main m, boolean isLocationUpper) {
		main = m;
		p = pl;
		isUpper = isLocationUpper;
		if (started) {
			p.openInventory(gui);
		} else {
			Util.debugMessage("원자로 사보타주가 아직 시작되지 않았습니다");
		}
	}

	/*
	 * 명령어 쳤을 때 실행됨. 사보타주 시작.
	 */

	public void s_fingerprintStart() {
		if (started) {
			Util.debugMessage("원자로 사보타주는 이미 시작되었습니다.");
		} else {
			initialize_Fingerprint();
			Util.debugMessage("원자로 사보타주 실행됨");
		}
	}

	/*
	 * 초기화 ; GUI를 설정함.
	 */
	private void initialize_Fingerprint() {
		started = true;
		upperPlayerList.clear();
		lowerPlayerList.clear();
		gui = Bukkit.createInventory(p, maxslot, guiName);
		gui.setMaxStackSize(1);
		setGUI(); // GUI 만들기
	}

	/*
	 * GUI를 만든다.
	 */

	private void setGUI() {
		List<String> lore = Arrays.asList("§7클릭해 활성화하세요");
		for (int slot = 0; slot < maxslot; slot++) {
			Util.Stack(gui, slot, Material.RED_STAINED_GLASS, 1, "§4활성화 되어있지 않음", lore);
		}
	}

	/*
	 * 활성화
	 */

	private void activate(UUID id) {
		Util.debugMessage(isUpper + " 활성화");
		List<String> lore = Arrays.asList("§7인벤토리를 닫으면 활성화가 풀립니다.");
		for (int slot = 0; slot < maxslot; slot++) {
			Util.Stack(gui, slot, Material. BLUE_STAINED_GLASS, 1, "§4활성화 되어있지 않음",lore);
			if(isUpper) {
			upperPlayerList.add(p.getUniqueId());
			}else {
			lowerPlayerList.add(p.getUniqueId());
			}
		}
		check();
	}
	/*
	 * 비활성화
	 */
	private void deactivate(UUID id) {
		Util.debugMessage(isUpper + " 비활성화");
		setGUI();
		if(isUpper) {
			upperPlayerList.remove(p.getUniqueId());
			}else {
			lowerPlayerList.remove(p.getUniqueId());
			}
	}
	/*
	 * 클리어 확인
	 */
	private void check() {
		if(upperPlayerList.size()!=0&&lowerPlayerList.size()!=0){
		Util.debugMessage("사보타주 클리어");
		started = false;
		}
	}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	@EventHandler
	private void onClick(InventoryClickEvent e) {
		if (e.getView().getTitle().equals(guiName)) {
			Util.debugMessage("클릭 인식됨");
			ItemStack itemstack = e.getCurrentItem();
			Player p = (Player) e.getWhoClicked();
			if (e.getClick().equals(ClickType.DOUBLE_CLICK) || e.isShiftClick() == true) { // 더블클릭,쉬프트클릭 금지
				Util.debugMessage("더블 클릭 불가");
				e.setCancelled(true);
			}
			if (itemstack != null) {
				if (itemstack.getType()==Material.RED_STAINED_GLASS) {
						Util.debugMessage("빨간색 클릭");
						activate(p.getUniqueId());
						e.setCancelled(true);
					}

				} else {
					Util.debugMessage("클릭 불가");
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
