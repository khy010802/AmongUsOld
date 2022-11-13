package bepo.au.sabo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
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
	 * 명령어 쳤을 때 실행됨, GUI 열기 시도.
	 */
	public void s_fingerprintOpen(Player pl, boolean isLocationUpper) {
		if (Activated) {
			pl.openInventory(gui.get(isLocationUpper ? 0 : 1));
		} else {
			Util.debugMessage("원자로 사보타주가 아직 시작되지 않았습니다");
		}
	}

	/*
	 * 명령어 쳤을 때 실행됨. 사보타주 시작.
	 */

	/*
	 * 초기화 ; GUI를 설정함.
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
		
		
		 // GUI 만들기
	}

	/*
	 * GUI를 만든다.
	 */

	private void setGUI(boolean isUpper) {
		
		List<String> lore = Arrays.asList("§7클릭해 활성화하세요");
		for (int slot = 0; slot < maxslot; slot++) {
			Util.Stack(gui.get(isUpper ? 0 : 1), slot, Material.RED_STAINED_GLASS, 1, "§4활성화 되어있지 않음", lore);
		}
		
		
	}

	/*
	 * 활성화
	 */

	private void activate(boolean isUpper, UUID id) {
		Util.debugMessage(isUpper + " 활성화");
		List<String> lore = Arrays.asList("§7인벤토리를 닫으면 활성화가 풀립니다.");
		if(isUpper) {
			upperPlayerList.add(id);
		} else {
			lowerPlayerList.add(id);
		}
		for (int slot = 0; slot < maxslot; slot++) {
			Util.Stack(gui.get(isUpper ? 0 : 1), slot, Material. BLUE_STAINED_GLASS, 1, "§b활성화 됨",lore);
		}
		check();
	}
	/*
	 * 비활성화
	 */
	private void deactivate(boolean isUpper, UUID id) {
		Util.debugMessage(isUpper + " 비활성화");
		
		if(isUpper) {
			upperPlayerList.remove(id);
		} else {
			lowerPlayerList.remove(id);
		}
		setGUI(isUpper);
	}
	/*
	 * 클리어 확인
	 */
	private void check() {
		if(upperPlayerList.size()!=0&&lowerPlayerList.size()!=0){
		Util.debugMessage("사보타주 클리어");
		Activated = false;
		Sabotage.saboClear(0);
		}
	}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	@EventHandler
	private void onClick(InventoryClickEvent e) {
		if(!checkPlayer(e)) return;
			Util.debugMessage("클릭 인식됨");
			ItemStack itemstack = e.getCurrentItem();
			Player p = (Player) e.getWhoClicked();
			if (e.getClick().equals(ClickType.DOUBLE_CLICK) || e.isShiftClick() == true) { // 더블클릭,쉬프트클릭 금지
				Util.debugMessage("더블 클릭 불가");
				e.setCancelled(true);
				return;
			}
			if (itemstack != null) {
				if (itemstack.getType()==Material.RED_STAINED_GLASS) {
						Util.debugMessage("빨간색 클릭");
						activate(getCode(e.getView().getTitle()) == 0 ? true : false, p.getUniqueId());
						e.setCancelled(true);
					} else {
						Util.debugMessage("클릭 불가");
						e.setCancelled(true);
					}
				}
		}
	
	@EventHandler
	private void onClose(InventoryCloseEvent e) {
		if(!Activated || !checkPlayer(e)) return;
			//Bukkit.broadcastMessage("닫기 발동 : " + e.getPlayer().getName());
			deactivate(getCode(e.getView().getTitle()) == 0 ? true : false, e.getPlayer().getUniqueId());
	}

}
