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
	 * �۵� �˰���
	 * 
	 * ġ����(����/���/���ڷ�/���) �纸Ÿ�� = 0 / �� �纸Ÿ�� = 1~7
	 * 
	 * �������Ͱ� ��/ġ���� �纸Ÿ�� ���� -> if (ġ���� ��) ��� �纸Ÿ���� Remain_Tick �� 0�ΰ�? (���Ͻ�) �ش� �纸Ÿ����
	 * ġ���� �纸Ÿ���� Remain_Tick�� 0�ΰ�? if �ش� �纸Ÿ���� Sabo_Cool �� 0�ΰ�? => ���� ����
	 * 
	 * Ÿ�̸� ���� ��... saboClear() �ߵ� => Sabo_Cool �缳�� �� Remain_Tick Ȱ��ȭ, Remain_Tick
	 * 0���� ���� ȸ�� �߻� => ��/���/���ڷδ� saboClear() ó��, �������� �ƴ� Remain_Tick 0 ���� => �� �纸Ÿ����
	 * saboClear() ó��, ġ���� �纸Ÿ���� saboClear() ó�� �� ���� ����
	 *
	 * ����/��� ���ӽð� = ����, �� Remain_Tick�� MAX_VALUE�� ������ ��
	 */

	// �纸Ÿ�� ���� �ð�. 0�� �Ǹ� ����
	public static int[] Remain_Tick = { 0, 0, 0, 0, 0, 0, 0, 0 };

	// ��Ÿ��. 0�� �Ǹ� ��� ����. ȸ�� ���� �� 100ƽ(5��) ����
	public static int[] Sabo_Cool = { 100, 100, 100, 100, 100, 100, 100, 100 };

	// �纸Ÿ�� ���
	public static Sabotage[] Sabos = new Sabotage[2];

	public static int Activated_Sabo = -1;

	public static SaboMainTimer smt;

	private final SaboType type;
	private final int id;

	// ���� ���� �� ȸ�� ���� ��
	public static void saboResetAll(boolean first) {
		if (first) {
			smt = new SaboMainTimer();
			smt.runTaskTimer(Main.getInstance(), 1L, 1L);
		}
		Remain_Tick = new int[] { 0, 0, 0, 0, 0, 0, 0, 0 };
		Sabo_Cool = new int[] { 100, 100, 100, 100, 100, 100, 100, 100 };
	}

	// �ߵ� �� 0, �ߵ� �Ұ� �� ���� ƽ�� ��ȯ
	public static final int saboActivate(SaboType type, int id) {
		if (canActivate(id)) {

			int s_id = id == 0 ? 0 : 1;

			Sabotage st = null;

			for (Sabotage s : MissionList.SABOTAGE) {
				Bukkit.broadcastMessage("�纸Ÿ�� : " + s + ", " + type);
				if (s.getType() == type) {
					st = s.getClone();
					break;
				}
			}

			Sabos[s_id] = st;
			for (PlayerData pd : PlayerData.getPlayerDataList()) {
				Sabotage s = st.getClone();
				pd.addMission(null, s);
				s.shinePosition(false);
			}

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
		// �̼� ����, ��Ÿ�� ������

		if (id == 0) {
			for (PlayerData pd : PlayerData.getPlayerDataList()) {
				Sabotage stm = null;
				for (Mission m : pd.getMissions())
					if (m instanceof Sabotage && ((Sabotage) m).getId() == 0)
						stm = (Sabotage) m;
				if (stm != null) {
					HandlerList.unregisterAll(stm);
					stm.shineReset();
					pd.getMissions().remove(stm);
					
					if(stm.getPlayer() != null) {
						Player p = stm.getPlayer();
						if(p.getOpenInventory() != null && stm.getTitles().contains(p.getOpenInventory().getTitle())) p.closeInventory();
					}
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
		return "��c" + getKoreanName();
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
	
	public final void saboGeneralClear(int i) {
		for(Player ap : Bukkit.getOnlinePlayers()) {
			if(gui_title.get(i).contains(ap.getOpenInventory().getTitle())) {
				ap.closeInventory(Reason.PLUGIN);
			}
			PlayerUtil.removeGlowingBlock(ap, locs.get(i));
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

	//
	public static class SaboMainTimer extends BukkitRunnable {

		public void run() {
			for (int id = 0; id < 8; id++) {
				if (Remain_Tick[id] > 0) {
					Remain_Tick[id]--;
					if (id == 0) {
						if (Remain_Tick[id] % 40 == 0)
							; // ġ���� �纸Ÿ�� �Ҹ� ȿ��
						if (Remain_Tick[id] == 0)
							; // ���� ����
					} else if (Remain_Tick[id] == 0) {
						saboClear(id);
					}
				} else if (Remain_Tick[id] < 0) { // Ŭ����
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