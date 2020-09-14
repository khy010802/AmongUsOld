package bepo.au.base;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.InventoryCloseEvent.Reason;
import org.bukkit.scheduler.BukkitRunnable;

import bepo.au.Main;
import bepo.au.function.MissionList;
import bepo.au.utils.ColorUtil;
import bepo.au.utils.PlayerUtil;

public abstract class Sabotage extends Mission {

	public Sabotage(MissionType mt, String name, String korean, int required_clear, Location loc, SaboType st, int id) {
		super(mt, name, korean, required_clear, loc);
		this.type = st;
		this.id = id;
	}

	public enum SaboType {
		DOOR, ELEC, OXYG, COMM, NUCL;
	}

	/*
	 * 작동 알고리즘
	 * 
	 * 치명적(전기/통신/원자로/산소) 사보타지 = 0 / 문 사보타지 = 1~7
	 * 
	 * 임포스터가 문/치명적 사보타지 실행 -> if (치명일 시) 모든 사보타지의 Remain_Tick 이 0인가? (문일시) 해당 사보타지와
	 * 치명적 사보타지의 Remain_Tick이 0인가? if 해당 사보타지의 Sabo_Cool 이 0인가? => 실행 성공
	 * 
	 * 타이머 가동 중... saboClear() 발동 => Sabo_Cool 재설정 및 Remain_Tick 활성화, Remain_Tick
	 * 0으로 설정 회의 발생 => 문/산소/원자로는 saboClear() 처리, 나머지는 아님 Remain_Tick 0 도달 => 문 사보타지면
	 * saboClear() 처리, 치명적 사보타지면 saboClear() 처리 후 게임 종료
	 *
	 * 전기/통신 지속시간 = 무한, 즉 Remain_Tick를 MAX_VALUE로 설정할 것
	 */

	// 사보타지 지속 시간. 0이 되면 종료
	public static int[] Remain_Tick = { 0, 0, 0, 0, 0, 0, 0, 0 };

	// 쿨타임. 0이 되면 사용 가능. 회의 종료 시 100틱(5초) 고정
	public static int[] Sabo_Cool = { 100, 100, 100, 100, 100, 100, 100, 100 };

	// 사보타지 목록
	public static Sabotage[] Sabos = new Sabotage[2];

	public static int Activated_Sabo = -1;

	public static SaboMainTimer smt;

	private final SaboType type;
	private final int id;

	// 게임 시작 및 회의 종료 후
	public static void saboResetAll(boolean first) {
		if (first) {
			smt = new SaboMainTimer();
			smt.runTaskTimer(Main.getInstance(), 1L, 1L);
		}
		Remain_Tick = new int[] { 0, 0, 0, 0, 0, 0, 0, 0 };
		Sabo_Cool = new int[] { 100, 100, 100, 100, 100, 100, 100, 100 };
	}

	// 발동 시 0, 발동 불가 시 남은 틱을 반환
	public static final int saboActivate(SaboType type, int id) {
		if (canActivate(id)) {

			int s_id = id == 0 ? 0 : 1;

			Sabotage st = null;

			for (Sabotage s : MissionList.SABOTAGE) {
				if (s.getType() == type) {
					st = s.getClone();
					break;
				}
			}

			Sabos[s_id] = st;
			registerSabo(st);
			for (PlayerData pd : PlayerData.getPlayerDataList()) {
				Sabotage s = st.getClone();
				pd.addMission(null, s);
			}

			st.onAssigned(null);

			Activated_Sabo = id;
			if (id == 0 && (st.getType() == SaboType.COMM || st.getType() == SaboType.ELEC))
				Remain_Tick[id] = Integer.MAX_VALUE;
			else
				Remain_Tick[id] = 30 * 20;
			Sabo_Cool[id] = 30 * 20;

			return 0;
		} else {
			return Sabo_Cool[id];
		}
	}

	public static final void saboClear(int id) {
		// 미션 제거, 쿨타임 돌리기

		if (id == 0) {
			for (PlayerData pd : PlayerData.getPlayerDataList()) {
				Sabotage stm = null;
				for (Mission m : pd.getMissions())
					if (m instanceof Sabotage && ((Sabotage) m).getId() == 0)
						stm = (Sabotage) m;
				if (stm != null) {
					HandlerList.unregisterAll(stm);
					pd.getMissions().remove(stm);
				}
			}
			
			
			
		}

		int s_id = id == 0 ? 0 : 1;
		Sabos[s_id].onClear(null, id);
		Sabos[s_id] = null;

		
		
		Activated_Sabo = -1;
		Remain_Tick[id] = -1;
		Sabo_Cool[id] = 600;
	}

	public static final boolean canActivate(int id) {
		if (id == 0) {
			for (int i = 1; i < 7; i++)
				if (isActivating(i))
					return false;
		}
		return !isActivating(id) && Sabo_Cool[id] == 0;
	}

	public static final boolean isActivating(int id) {
		return Remain_Tick[0] > 0 || Remain_Tick[id] > 0;
	}
	
	@Override
	public String getScoreboardMessage() {
		return "§c" + getKoreanName();
	}

	public final SaboType getType() {
		return this.type;
	}

	public final int getId() {
		return this.id;
	}

	public Sabotage getClone() {
		try {
			return (Sabotage) this.clone();
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	public final void saboGeneralClear() {
		for(Player ap : Bukkit.getOnlinePlayers()) {
			if(gui_title.contains(ap.getOpenInventory().getTitle())) {
				ap.closeInventory(Reason.PLUGIN);
			}
			for(Location loc : locs) PlayerUtil.removeGlowingBlock(ap, loc);
		}
	}
	
	private static void registerSabo(Sabotage s) {
		Bukkit.getPluginManager().registerEvents(s, Main.getInstance());
		for(Location loc : s.getLocations()) {
			for(Player ap : Bukkit.getOnlinePlayers()) PlayerUtil.spawnGlowingBlock(ap, loc, ColorUtil.RED);
		}
	}

	//
	public static class SaboMainTimer extends BukkitRunnable {

		public void run() {
			for (int id = 0; id < 8; id++) {
				if (Remain_Tick[id] > 0) {
					Remain_Tick[id]--;
					if (id == 0) {
						if (Remain_Tick[id] % 40 == 0)
							; // 치명적 사보타지 소리 효과
						if (Remain_Tick[id] == 0)
							; // 게임 종료
					} else if (Remain_Tick[id] == 0) {
						saboClear(id);
					}
				} else if (Remain_Tick[id] < 0) { // 클리어
					Remain_Tick[id] = 0;
				}

				if (Remain_Tick[0] <= 0) {
					if (Sabo_Cool[id] > 0)
						Sabo_Cool[id]--;
				}

			}
		}

	}

}