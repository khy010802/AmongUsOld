package bepo.au.sabo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent.Reason;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import bepo.au.GameTimer;
import bepo.au.base.PlayerData;
import bepo.au.base.Sabotage;
import bepo.au.utils.Util;

import java.util.Random;

public class S_FixLights extends Sabotage {

	private static Inventory gui = null;
	
	private String guiName = "S_FixLights";
	Random random = new Random();
	int maxslot = 45;
	
	static boolean[] leverStatus = new boolean[5];
	static boolean[] connected = new boolean[5];

	public static boolean Activated = false;
	
	public S_FixLights(MissionType mt2, String name, String korean, int clear, Location loc) {
		super(mt2, name, korean, clear, loc, SaboType.ELEC, 0);
	}
	
	public void onRestart() {
		for(Player ap : Bukkit.getOnlinePlayers()) {
			PlayerData pd = PlayerData.getPlayerData(ap.getName());
			if(pd != null) {
				if(pd.isAlive() && !GameTimer.IMPOSTER.contains(ap.getName())) ap.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 1000000, 0, true));
			}
		}
	}
	
	public void onAssigned(Player p) {
		assign(p);
		gui_title.add(guiName);
		initialize_fixLights();
		setGUI();
	}
	
	public void onStart(Player p, int i) {
		s_fixLightsOpen(p);
	}
	
	public void onStop(Player p, int i) {
		
	}
	
	public void onClear(Player p, int i) {
		Activated = false;
		gui = null;
		saboGeneralClear();
	}
	
	/*
	 * 명령어 쳤을 때 실행됨, gui 열기 시도.
	 */
	public void s_fixLightsOpen(Player p) {

		if (Activated) {
			p.openInventory(gui);
		} else {
			Util.debugMessage("전등 사보타주는 아직 시작되지 않았습니다");
		}
	}


	/*
	 * 초기화 ; gui를 만듦.
	 */
	public void initialize_fixLights() {
		if(Activated == false) {
			Activated = true;
			gui = Bukkit.createInventory(null, maxslot, guiName);
			gui.setMaxStackSize(1);
			for (int i = 0; i < 5; i++)
				connected[i] = false;
			int init_connected = 1 + random.nextInt(4); // 연결된 수가 적을 확률이 더 큼
			for (int i = 0; i < init_connected; i++)
				connected[random.nextInt(5)] = true; // 연결된 레버는 1~4개
			for (int i = 0; i < 5; i++) {
				leverStatus[i] = random.nextBoolean();
			}
			
			onRestart();
		}
		
		// 레버의 상태는 랜덤
		setGUI(); // gui 만들기
	}
	/*
	 * GUI를 만든다.
	 */

	public void setGUI() {
		List<String> lore = Arrays.asList("§7아래의 전등이 켜지도록 위 레버를 클릭해주세요.");
		for (int slot = 0; slot < maxslot; slot++) {
			int x = slot % 9, y = slot / 9;

			if (x % 2 == 0 && y == 1)
				Util.Stack(gui, slot,
						(leverStatus[x / 2] ? Material.RED_STAINED_GLASS_PANE : Material.BLUE_STAINED_GLASS_PANE), 1,
						"§f레버");
			else if (x % 2 == 0 && y == 3)
				Util.Stack(gui, slot, (connected[x / 2] ? Material.LANTERN : Material.SOUL_LANTERN), 1,
						(connected[x / 2] ? "§a연결됨" : "§4연결안됨"));
			else if (x == 4 && y == 4)
				Util.Stack(gui, slot, Material.BOOK, 1, "§f전등 수리", lore);
			else
				Util.Stack(gui, slot, Material.WHITE_STAINED_GLASS_PANE, 1, " ");
		}
	}

	/*
	 * 레버를 토글
	 */
	public void toggleLever(int idx) {
		Util.debugMessage("레버 토글");
		connected[idx] = !connected[idx];
		leverStatus[idx] = !leverStatus[idx];
		updateGUI();
		check();
	}

	/*
	 * 클리어 확인
	 */
	public void check() {
		for (Boolean connection : connected) {
			Util.debugMessage(connection.toString());
			if (!connection)
				return;
		}
		for(Player ap : Bukkit.getOnlinePlayers()) {
			ap.removePotionEffect(PotionEffectType.BLINDNESS);
		}
		Util.debugMessage("사보타주 클리어");
		List<HumanEntity> helist = new ArrayList<HumanEntity>();
		for (HumanEntity he : gui.getViewers()) {
			helist.add(he);
		}
		for(HumanEntity he : helist) he.closeInventory(Reason.PLUGIN);
		Sabotage.saboClear(0);
		
	}

	/*
	 * gui를 보고있는 모든 플레이어 업데이트
	 */
	public void updateGUI() {
		Util.debugMessage("GUI 업데이트");
		for (int slot = 9; slot < 18; slot += 2)
			Util.Stack(gui, slot,
					(leverStatus[slot % 9 / 2] ? Material.RED_STAINED_GLASS_PANE : Material.BLUE_STAINED_GLASS_PANE), 1,
					"§f레버");
		for (int slot = 27; slot < 36; slot += 2)
			Util.Stack(gui, slot, (connected[slot % 9 / 2] ? Material.LANTERN : Material.SOUL_LANTERN), 1,
					(connected[slot % 9 / 2] ? "§a연결됨" : "§4연결안됨"));
		for (HumanEntity he : gui.getViewers()) {((Player) he).updateInventory();}  //플레이별 인벤 업데이트
		
	}
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	@EventHandler
	public void onClick(InventoryClickEvent e) {
		if(!checkPlayer(e)) return;
		
			Util.debugMessage("클릭 인식됨");
			int slot = e.getRawSlot();
			ItemStack itemstack = e.getCurrentItem();

			// Inventory gui = e.getClickedInventory();
			// Player p = (Player) e.getWhoClicked();

			if (e.getClick().equals(ClickType.DOUBLE_CLICK) || e.isShiftClick() == true) { // 더블클릭,쉬프트클릭 금지
				Util.debugMessage("더블 클릭 불가");
				e.setCancelled(true);
			}
			if (itemstack != null) {
				if (itemstack.getType() == Material.BLUE_STAINED_GLASS_PANE
						|| itemstack.getType() == Material.RED_STAINED_GLASS_PANE) {
					Util.debugMessage("레버 클릭 인식됨");
					toggleLever(slot % 9 / 2);
					e.setCancelled(true);
				} else {
					Util.debugMessage("클릭 불가");
					e.setCancelled(true);
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
