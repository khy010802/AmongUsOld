package bepo.au.missions;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import bepo.au.Main;
import bepo.au.base.Mission;
import bepo.au.base.TimerBase;
import bepo.au.utils.Util;

//8틱 -> 2틱
public class H_InspectSample extends Mission{
	
	public H_InspectSample(MissionType mt, String name, String korean, int clear, Location loc) {
		super(mt, name, korean, clear, loc);
	}
	
	public void onAssigned(Player p) {
		assign(p);
	}
	
	public void onStart(Player p, int code) {
		uploadInventory(p, 54, "InspectSample");
		inspectsample(p);
	}
	
	public void onClear(Player p, int code) {
		if(timer != null && timer.GetTimerRunning()) timer.StopTimer();
		timer = null;
		generalClear(p, code);
	}
	
	public void onStop(Player p, int code) {
		
	}
	
	@Override
	public String getScoreboardMessage() {
		
		String s = getKoreanName();
		if(timer != null) {
			s += " (" + remain_time + "s)";
			s = "§e" + s;
		}
		if (cleared.size() > 0) {
			s = "§a" + s;
		}
		return s;
	}
	
	public class Timer extends TimerBase {

		@Override
		public void EventStartTimer() { // 타이머 시작
			Util.debugMessage(" 타이머 시작됨");
			P_timer = new PreparingTimer();
			P_timer.StartTimer(45, false, 1);
		}

		@Override
		public void EventRunningTimer(int count) {
			Player p = getPlayer();

			if (p != null && p.getOpenInventory().getTitle().split(" ")[0].equals("InspectSample")) {
				ItemStack[] temp = gui.get(0).getContents();
				gui.set(0, Bukkit.createInventory(p, 54, "InspectSample " + count));
				gui.get(0).setContents(temp);
				new BukkitRunnable() {
					public void run() {
						p.openInventory(gui.get(0));
					}
				}.runTaskLater(Main.getInstance(), 1L);
				
			}
			remain_time = count;
		}

		@Override
		public void EventEndTimer() {
			ItemStack[] temp = gui.get(0).getContents();
			Player p = getPlayer();
			if(p != null) {
				gui.set(0, Bukkit.createInventory(p, 54, "InspectSample"));
				gui.get(0).setContents(temp);
				status = 4;
				
				if (p.getOpenInventory().getTitle().split(" ")[0].equals("InspectSample")) {
					inspectsampleagain(p);
				}
			}
			

		}

	}

	public class PreparingTimer extends TimerBase {// 호퍼 옮기는 타이머
		int hopperidx = 0;

		@Override
		public void EventStartTimer() {
			// TODO Auto-generated method stub

		}

		@Override
		public void EventRunningTimer(int count) {
			Player p = getPlayer();
			if(p == null) return;
			
			
			
			if (count == 45) {
				Util.debugMessage("호퍼 이동 타입 0");
				Util.Stack(gui.get(0), hopperidx, Material.WHITE_STAINED_GLASS_PANE, 1, " ");
				hopperidx = 0;
				Util.Stack(gui.get(0), hopperidx, Material.HOPPER, 1, " ");

			} else if (count % 10 == 8 && count < 43) {
				Util.debugMessage("호퍼 이동 타입 1");
				hopperidx++;
				Util.Stack(gui.get(0), hopperidx - 1, Material.WHITE_STAINED_GLASS_PANE, 1, " ");
				Util.Stack(gui.get(0), hopperidx, Material.HOPPER, 1, " ");

			} else if (count % 10 == 2 && count < 43 && count > 8) {
				Util.debugMessage("호퍼 이동 타입 2");
				hopperidx++;
				Util.Stack(gui.get(0), hopperidx - 1, Material.WHITE_STAINED_GLASS_PANE, 1, " ");
				Util.StackPotion(gui.get(0), 18 + hopperidx, Color.BLUE, 1, "§f확인되지 않은 시약");
				if (getPlayer().getOpenInventory().getTitle().split(" ")[0].equals("InspectSample"))
					p.playSound(p.getLocation(), Sound.ITEM_BUCKET_EMPTY, 1.0f, 1.2f);
				Util.Stack(gui.get(0), hopperidx, Material.HOPPER, 1, " ");

			} else if (count == 0) {
				Util.StackPotion(gui.get(0), 18 + hopperidx, Color.BLUE, 1, "§f확인되지 않은 시약");
				if (p.getOpenInventory().getTitle().split(" ")[0].equals("InspectSample"))
					p.playSound(p.getLocation(), Sound.ITEM_BUCKET_EMPTY, 1.0f, 1.2f);
			}
			if (p.getOpenInventory().getTitle().split(" ")[0].equals("InspectSample")) {
				//p.openInventory(gui.get(0));
			}
		}

		@Override
		public void EventEndTimer() {
			// TODO Auto-generated method stub

		}

	}

	Timer timer;
	PreparingTimer P_timer;
	int status = 0; // 0 실행안됨 | 1,2 실행됨 | 3 시약분석중 | 4 분석가능 | 100 클리어 상태
	int bad;
	private int remain_time = 60;
	final int time = 60; // 기다림 시간

	private List<String> lore = Arrays.asList("§7", "§71. 오른쪽 아래 파란색 버튼을 누른다.", "§72. " + time + "초 동안 기다린다.",
			"§73. 이상 표본을 선택한다.", "§7잘못된 표본을 선택하면 다시 시작합니다.", "§7기다리는 동안 다른 곳에 가도 됩니다.");

	
	public void inspectsample(Player p) {
		Util.debugMessage("inspctsample 실행");
		switch (status) {
		case 0: // 실행 됨

			Util.debugMessage("status 0 실행");
			bad = Util.random(0, 4); // 이상 표본 만들기
			gui.set(0, Bukkit.createInventory(p, 54, "InspectSample"));
			for (int slot = 0; slot < 54; slot++) {
				switch (slot) {
				case 0:
					Util.Stack(gui.get(0), slot, Material.HOPPER, 1, " ");
					break;
				case 18:
				case 20:
				case 22:
				case 24:
				case 26:
					Util.Stack(gui.get(0), slot, Material.GLASS_BOTTLE, 1, "§f빈 시약 병", "§4클릭불가");
					break;
				case 36:
				case 38:
				case 40:
				case 42:
				case 44:
					Util.Stack(gui.get(0), slot, Material.GRAY_STAINED_GLASS_PANE, 1, " ", "§4클릭불가");
					break;
				case 49:
					Util.Stack(gui.get(0), slot, Material.BOOK, 1, "§f§l표본 분석", lore);
					break;
				case 53:
					Util.Stack(gui.get(0), slot, Material.BLUE_STAINED_GLASS_PANE, 1, "§a§l시약 추가하기");
					break;
				default:
					Util.Stack(gui.get(0), slot, Material.WHITE_STAINED_GLASS_PANE, 1, " "); // 배경
				}
			}

			status = 1;
		case 1: // 아래 과정 반복 방지
			p.openInventory(gui.get(0)); // gui.get(0) 열기
			break;
		case 2: // 시약 준비중
			Util.debugMessage("status 2 실행");
			Util.Stack(gui.get(0), 53, Material.ORANGE_STAINED_GLASS_PANE, 1, " ", "§4클릭불가");
			timer = new Timer();
			timer.StartTimer(time, true, 20);
			status = 3;
			p.openInventory(gui.get(0));
			break;
		case 4:
			Util.debugMessage("status 4 실행");
			prepareSample();// 시약 준비 완료
			p.openInventory(gui.get(0));
			break;
		}
		Util.debugMessage("switch문 빠져나옴");
		p.openInventory(gui.get(0)); // gui.get(0) 열기
	}

	public void inspectsampleagain(Player p) {
		inspectsample(p);
	}

	public void prepareSample() {
		Util.debugMessage("시약 준비완료 단계");

		for (int slot = 36; slot <= 44; slot += 2)
			Util.Stack(gui.get(0), slot, Material.GREEN_STAINED_GLASS_PANE, 1, "§f표본을 선택하세요");
		for (int slot = 18; slot < 27; slot += 2) {
			if ((slot - 18) / 2 != bad)
				Util.StackPotion(gui.get(0), slot, Color.BLUE, 1, "§f정상 시약");
			else
				Util.StackPotion(gui.get(0), slot, Color.RED, 1, "§c이상 시약");
		}

	}

	public void setStatus(int num) {
		status = num;
	}

	public void checkSample(Player p, int num) {
		Util.debugMessage(num + "과" + bad + "비교");
		if (num == bad) {
			for (int slot = 36; slot <= 44; slot += 2)
				Util.Stack(gui.get(0), slot, Material.GRAY_STAINED_GLASS_PANE, 1, " ", "§4클릭불가");
			status = 100;
			onClear(p, 0);
		} else {
			p.playSound(p.getLocation(), Sound.ITEM_SHIELD_BREAK, 1.0f, 0.1f);
			Util.debugMessage(" 틀림, 재시작");
			status = 0;
			inspectsampleagain(p);
		}

	}

	@EventHandler
	public void onClick(InventoryClickEvent e) {
		
		if(!checkPlayer(e)) return;
		
		Player p = (Player) e.getWhoClicked();
		
		// Inventory inv = e.getClickedInventory();
		// Player p = (Player) e.getWhoClicked();

		if (e.getCurrentItem() != null) {
			Material item = e.getCurrentItem().getType();
			if (item != Material.GREEN_STAINED_GLASS_PANE && item != Material.BLUE_STAINED_GLASS_PANE) {
				e.setCancelled(true);
			}
			if (item == Material.BLUE_STAINED_GLASS_PANE) {
				status = 2;
				inspectsampleagain(p);
			}
			if (item == Material.GREEN_STAINED_GLASS_PANE) {
				checkSample(p, (e.getRawSlot() % 9) / 2);
				e.setCancelled(true);
			}
		}
	}
}
