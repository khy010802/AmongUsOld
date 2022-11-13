package bepo.au.sabo;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;

import org.bukkit.inventory.ItemStack;

import bepo.au.GameTimer;
import bepo.au.Main;
import bepo.au.base.Mission;
import bepo.au.base.PlayerData;
import bepo.au.base.Sabotage;
import bepo.au.function.AdminMap;
import bepo.au.manager.BossBarManager;
import bepo.au.manager.BossBarManager.BossBarList;
import bepo.au.utils.Util;

import java.util.Random;

public class S_Communication extends Sabotage {
	
	Random random = new Random();
	private final int maxslot = 27;
	private final int maxNum = 10; // 주파수의 최대숫자임. 1 : 에러 | 2 : 한번클릭하면됨 | 3 : 최대 두번클릭 | 5~6 : 적당 | 10 : 해보니까 할 만하긴함
								   // 20+ 오래걸릴듯
	private int[] answerStatus;
	private int[] currentStatus;
	private final Material[] Color ={Material.LIME_WOOL,Material.ORANGE_WOOL,Material.WHITE_WOOL};  // 각 인덱스는 정답과의 거리임.
																									// 거리는 maxNum에 영향을
																									// 받음. (예시)정답이 1이고
																									// 현재 2 이라면 1번째 인덱스.
	
	public static boolean Activated = false;
	
	public S_Communication(MissionType mt2, String name, String korean, int clear, Location loc) {
		super(mt2, name, korean, clear, loc, SaboType.COMM, 0);
	}
	
	public void onRestart() {
		BossBarManager.updateBossBar(BossBarList.TASKS, true);
	}
	
	public void onAssigned(Player p) {
		assign(p);
		initialize_s_communications(p);
		
		BossBarManager.updateBossBar(BossBarList.TASKS, true);
		
		PlayerData pd = PlayerData.getPlayerData(p.getName());
		
		if(pd != null) {
			
			if(pd.isWatchingCCTV()) {
				pd.exitCCTV(p);
				p.sendMessage(Main.PREFIX + "§c통신 기기가 파손되어 CCTV를 확인할 수 없습니다.");
			}
			
			if(GameTimer.IMPOSTER.contains(p.getName())) return;
			for(Mission m : pd.getMissions()) {
				if(!(m instanceof Sabotage)) m.shineReset();
			}
		}
		
	}
	
	public void onStart(Player p, int i) {
		s_communicationOpen(p);
	}
	
	public void onStop(Player p, int i) {

	}
	
	public void onClear(Player p, int i) {
		Activated = false;
		
		saboGeneralClear();
		
		for(Player ap : Bukkit.getOnlinePlayers()) {
			PlayerData pd = PlayerData.getPlayerData(ap.getName());
			if(pd != null && !GameTimer.IMPOSTER.contains(ap.getName())) {
				for(Mission m : pd.getMissions()) {
					m.shinePosition(m.getOrdered());
				}
			}
		}
		
		BossBarManager.updateBossBar(BossBarList.TASKS, false);
	}
	
	
	/*
	 * 명령어 쳤을 때 실행됨, GUI 열기 시도.
	 */
	public void s_communicationOpen(Player p) {
		
		if (Activated) {
			p.openInventory(gui.get(0));
		} else {
			Util.debugMessage("통신 사보타주는 아직 시작되지 않았습니다");
		}
	}

	/*
	 * 명령어 쳤을 때 실행됨. 사보타주 시작.
	 */
	public void s_communicationsStart() {
		if (Activated) {
			Util.debugMessage("통신 사보타주는 이미 시작되었습니다.");
		} else {
			
			Util.debugMessage("통신 사보타주 실행됨");
		}
	}

	/*
	 * 초기화 ; GUI를 만듦.
	 */
	private void initialize_s_communications(Player p) {
		
		answerStatus = new int[5];
		currentStatus = new int[5];
		
		while (true) {
			for (int i = 0; i < 5; i++) {
				answerStatus[i] = random.nextInt(maxNum); // 옳은 주파수 만들기, 주파수는 0~4값.
				currentStatus[i] = random.nextInt(maxNum); // 현재 주파수 세팅
			}
			if (!Arrays.equals(answerStatus, currentStatus))
				break; // 정답과 현재의 값이 같으면 반복.
		}
		
		if(p.getOpenInventory() != null && p.getOpenInventory().getTitle().contains(AdminMap.guiName)) p.closeInventory();
		
		Activated = true;
		uploadInventory(p, maxslot, "Communication");
		gui.get(0).setMaxStackSize(1);
		setGUI(); // GUI 만들기

	}
	/*
	 * GUI를 만든다.
	 */

	private void setGUI() {
		List<String> lore = Arrays.asList("§7양털을 클릭해 모든 양털을 초록색으로 만드세요.");
		for (int slot = 0; slot < maxslot; slot++) {
			int x = slot % 9, y = slot / 9;

			if (10 < slot && slot < 16) {
				Util.debugMessage(slot + "에 양털 " + currentStatus[x - 2] + "와 " + answerStatus[x - 2] + "차 비교");
				int dif = difference(currentStatus[x - 2], answerStatus[x - 2], maxNum);
				Util.debugMessage("거리 : " + dif);
				if (dif >= Color.length)
					dif = Color.length - 1; // 정해진 색이 없을때
				Util.Stack(gui.get(0), slot, Color[dif], 1, "§f" + currentStatus[x - 2],
						Arrays.asList("§7좌클릭 : 증가", "§7우클릭 : 감소"));
			} else if (x == 0 && y == 1) {
				Util.Stack(gui.get(0), slot, Material.BOOK, 1, "§f전등 수리", lore);
			} else {
				Util.Stack(gui.get(0), slot, Material.WHITE_STAINED_GLASS_PANE, 1, " ");
			}

		}
	}

	/*
	 * 두 수사이의 차(거리) 구하기
	 */
	private int difference(int a, int b, int max) {
		Util.debugMessage(a + "와" + b + "를 비교합니다.");
		int temp = Math.abs(a - b);
		Util.debugMessage("차 : " + temp);
		if (temp > max / 2) {
			return max - temp;
		}

		return temp;
	}

	/*
	 * 레버를 토글
	 */
	private void toggleStatus(Player p, int idx, boolean increase) {
		p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 1.0f, 1.0f);
		Util.debugMessage("┌-------토글-------┐");
		Util.debugMessage(idx + "번 주파수 토글");
		if (increase) {
			if (++currentStatus[idx] > maxNum - 1)
				currentStatus[idx] = 0;
		} else if (--currentStatus[idx] < 0)
			currentStatus[idx] = maxNum - 1;
		int dif = difference(currentStatus[idx], answerStatus[idx], maxNum);
		if (dif >= Color.length)
			dif = Color.length - 1; // 정해진 색이 없을때 가장 뒤에 있는 색을 고름.
		Util.Stack(gui.get(0), 11 + idx, Color[dif], 1, "§f" + currentStatus[idx], Arrays.asList("§7좌클릭 : 증가", "§7우클릭 : 감소"));
		p.updateInventory();
		if (dif == 0)
			check();
		Util.debugMessage("└------토글 끝------┘");
	}

	/*
	 * 클리어 확인
	 */
	private void check() {
		for (int i = 0; i < 5; i++) {
			int dif = difference(currentStatus[i], answerStatus[i], maxNum);
			Util.debugMessage("클리어 확인 " + Integer.toString(dif));
			if (dif != 0)
				return;
		}
		Util.debugMessage("사보타주 클리어");
		Sabotage.saboClear(0);
	}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	@EventHandler
	public void onClick(InventoryClickEvent e) {
		
		if(!checkPlayer(e)) return;
		if(!(e.getAction() == InventoryAction.PICKUP_ALL || e.getAction() == InventoryAction.PICKUP_HALF || e.getAction() == InventoryAction.PICKUP_ONE)) return;
		Player p = (Player) e.getWhoClicked();
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
				if (Arrays.asList(Color).contains(itemstack.getType())) {
					if (e.isLeftClick()) {
						Util.debugMessage("좌클릭 인식됨");
						toggleStatus(p, slot % 9 - 2, true);
						e.setCancelled(true);
					} else {
						Util.debugMessage("우클릭 인식됨");
						toggleStatus(p, slot % 9 - 2, false);
						e.setCancelled(true);
					}

				} else {
					Util.debugMessage("클릭 불가");
					e.setCancelled(true);
				}
			}
	}
	/*
	 * @EventHandler public void onDrag(InventoryEvent e) { if
	 * (e.getView().getTitle().equals("EmptyChute") && !e.getRawSlots().isEmpty()) {
	 * ; }
	 * 
	 * }
	 */

}
