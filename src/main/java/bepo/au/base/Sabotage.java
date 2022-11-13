package bepo.au.base;

import bepo.au.GameTimer;
import bepo.au.GameTimer.Status;
import bepo.au.GameTimer.WinReason;
import bepo.au.Main;
import bepo.au.Main.SETTING;
import bepo.au.function.MissionList;
import bepo.au.manager.LocManager;
import bepo.au.sabo.S_Communication;
import bepo.au.sabo.S_Fingerprint;
import bepo.au.sabo.S_FixLights;
import bepo.au.sabo.S_Oxygen;
import bepo.au.utils.PlayerUtil;
import bepo.au.utils.Util;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.InventoryCloseEvent.Reason;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

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
	public static Sabotage Sabos = null;

	public static int Activated_Sabo = -1;

	public static SaboMainTimer smt;

	private final SaboType type;
	private final int id;
	
	public static String getRoomById(int id) { return getRoomById(id, true); }
	
	public static String getRoomById(int id, boolean english) {
		
		if(!english) {
			switch(id) {
			case 1: return "��� ����";
			case 2: return "���Ƚ�";
			case 3: return "�Ϻ� ����";
			case 4: return "�ǹ���";
			case 5: return "�����";
			case 6: return "�Ĵ�";
			case 7: return "â��";
			}
		} else {
			switch(id) {
			case 1: return "UpperEngine";
			case 2: return "Security";
			case 3: return "LowerEngine";
			case 4: return "MedBay";
			case 5: return "Electrical";
			case 6: return "Cafeteria";
			case 7: return "Storage";
			}
		}
		return null;
	}
	
	public static boolean saboActivate(Player p) {
		PlayerData pd = PlayerData.getPlayerData(p.getName());
		int id = 0;
		if(pd.getSelectedSabo() == SaboType.DOOR) id = pd.getSelectedSaboDoor();
		boolean crit = Sabotage.isActivating(0);
		int tick = Sabotage.saboActivate(pd.getSelectedSabo(), id);
		
		if(tick == 0 && !crit) {
			p.playSound(p.getLocation(), Sound.BLOCK_GLASS_BREAK, 1.0F, 1.0F);
			return true;
		} else if(crit){
			p.sendMessage(Main.PREFIX + "��cġ���� �纸Ÿ�� �ߵ� �߿� �ߵ��� �� �����ϴ�.");
		} else if(tick < 0) {
			p.sendMessage(Main.PREFIX + "��c���� �ݾ��� ���� ġ���� �纸Ÿ���� �ߵ��� �� �����ϴ�.");
		} else {
			p.sendMessage(Main.PREFIX + "��f" + (tick / 20 + 1) + "��c�� �� �ߵ��� �� �ֽ��ϴ�.");
		}
		return false;
	}

	// ���� ���� �� ȸ�� ���� ��
	public static void saboResetAll(boolean first) {
		if (first) {
			
			smt = new SaboMainTimer();
			smt.runTaskTimer(Main.getInstance(), 1L, 1L);
		}
		Remain_Tick = new int[] { 0, 0, 0, 0, 0, 0, 0, 0 };
		Sabo_Cool = new int[] { 100, 100, 100, 100, 100, 100, 100, 100 };
		if(S_Communication.Activated || S_FixLights.Activated) {
			Sabos.onRestart();
			Remain_Tick[0] = Integer.MAX_VALUE;
			Sabo_Cool[0] = SETTING.SABO_COOL_SEC.getAsInteger() * 20;
		}
	}
	
	public static void saboStopAll() {
		if(smt != null && !smt.isCancelled()) smt.cancel();
		smt = null;
		Sabos = null;
		S_Fingerprint.Activated = false;
		S_Oxygen.Activated = false;
		S_FixLights.Activated = false;
		S_Communication.Activated = false;
	}

	// �ߵ� �� 0, �ߵ� �Ұ� �� ���� ƽ�� ��ȯ, ���� ���� �ߵ� �Ұ� �� -1 ��ȯ
	private static final int saboActivate(SaboType type, int id) {
		if (canActivate(id)) {

			int s_id = id == 0 ? 0 : 1;
			
				Sabotage st = null;

				for (Sabotage s : MissionList.SABOTAGE) {
					if (s.getType() == type) {
						st = s.getClone();
						break;
					}
				}

				Activated_Sabo = id;
				if (id == 0 && (st.getType() == SaboType.COMM || st.getType() == SaboType.ELEC))
					Remain_Tick[id] = Integer.MAX_VALUE;
				else if(id == 0) {
					Remain_Tick[id] = SETTING.SABO_CRIT_DURA_SEC.getAsInteger() * 20;
					Sabo_Cool[id] = SETTING.SABO_COOL_SEC.getAsInteger() * 20;
				} else {
					Remain_Tick[id] = 10 * 20;
					Sabo_Cool[id] = 30 * 20;
				}
				
				if(s_id == 0) {
					Sabos = st;
					for (PlayerData pd : PlayerData.getPlayerDataList()) {
						Sabotage s = st.getClone();
						pd.addMission(null, s);
						s.shinePosition(false);
					}
				} else closeDoor(id);
				
				return 0;
			} else {
				if(id == 0 && Sabo_Cool[id] == 0) return -1;
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
			
			for(Player ap : Bukkit.getOnlinePlayers()) PlayerUtil.toggleRedEffect(ap, false);
			Sabos.onClear(null, id);
			Sabos = null;
			Sabo_Cool[id] = SETTING.SABO_COOL_SEC.getAsInteger() * 20;
		} else {
			openDoor(id);
		}
		
		Activated_Sabo = -1;
		Remain_Tick[id] = -1;
		
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
	
	private final static long DOOR_DELAY = 2L;
	
	// �� �纸Ÿ��
	public static void closeDoor(int id) {
		List<Location> locs = LocManager.getLoc("Door_" + getRoomById(id));
		if(locs == null || locs.size() < 2) return;
		
		List<Location> uppers = new ArrayList<Location>();
		List<Location> lowers = new ArrayList<Location>();
		
		for(int i=0;i<locs.size();i++) {
			if(i % 2 == 0) uppers.add(locs.get(i));
			else lowers.add(locs.get(i));
		}
		
		for(int index=0;index<uppers.size();index++) {
			final Location upper = uppers.get(index);
			final Location lower = lowers.get(index);
			new BukkitRunnable() {
				private int high_y = upper.getBlockY();
				private int low_y = lower.getBlockY();
				public void run() {
					if(Main.gt == null) {
						openDoor(id);
						this.cancel();
					} else {
						if(low_y > high_y) {
							this.cancel();
							return;
						}
						
						Location loc1 = upper.clone();
						Location loc2 = lower.clone();
						loc1.setY(high_y); loc2.setY(high_y);
						Util.fillBlock(Material.IRON_BLOCK, loc1, loc2);
						
						loc1.getWorld().playSound(loc1, Sound.ENTITY_ZOMBIE_ATTACK_IRON_DOOR, 0.2F, 1.0F);
						
						high_y--;
					}
				}
			}.runTaskTimer(Main.getInstance(), 0L, DOOR_DELAY);
		}
		
		
	}
	
	public static void openDoor(int id) {
		List<Location> locs = LocManager.getLoc("Door_" + getRoomById(id));
		if(locs == null || locs.size() < 2) return;
		
		boolean opened = true;
		if(locs.get(0).getBlock().getType() == Material.IRON_BLOCK||locs.get(locs.size()-1).getBlock().getType() == Material.IRON_BLOCK){
				 opened = false;
		}
		if(opened) return;
		
		for(int i=0;i<locs.size();i+=2) {
			if(locs.size() > i+1) {
				
				Util.fillBlock(Material.AIR, locs.get(i), locs.get(i+1));
				locs.get(i+1).getWorld().playSound(locs.get(i+1), Sound.BLOCK_LAVA_EXTINGUISH, 1.0F, 1.0F);
			}
		
		}
		
		
	}
	
	public abstract void onRestart();
	
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

			PlayerData pd = PlayerData.getPlayerData(ap.getName());
			if(pd != null) {
				for(Mission m : pd.getMissions()) {
					if(m.getMissionName().equalsIgnoreCase(name)) {
						if(ap.getOpenInventory() != null && m.gui_title.get(i).contains(ap.getOpenInventory().getTitle())) {
							ap.closeInventory(Reason.PLUGIN);
						}
						m.cleared.add(i);
						break;
					}
				}
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
			
			if(Main.gt.getStatus() == Status.VOTING) {
				if(Sabos != null && (Sabos.getType() == SaboType.NUCL || Sabos.getType() == SaboType.OXYG)) {
					Sabotage.saboClear(0);
				}
				for(int i=1;i<8;i++) {
					openDoor(i);
				}
				return;
			}
			
			for (int id = 0; id < 8; id++) {
				if (Remain_Tick[id] > 0) {
					Remain_Tick[id]--;
					if (id == 0) {
						if (Remain_Tick[id] % 40 == 0 && (Sabos.getType() == SaboType.NUCL || Sabos.getType() == SaboType.OXYG))
							for(Player ap : Bukkit.getOnlinePlayers()) {
								ap.playSound(ap.getLocation(), Sound.BLOCK_NOTE_BLOCK_BIT, 1.0F, 0.5F);
								ap.playSound(ap.getLocation(), Sound.BLOCK_NOTE_BLOCK_DIDGERIDOO, 1.0F, 0.6F);
								ap.playSound(ap.getLocation(), Sound.BLOCK_NOTE_BLOCK_DIDGERIDOO, 1.0F, 0.9F);
								PlayerUtil.toggleRedEffect(ap, true);
							} // ġ���� �纸Ÿ�� �Ҹ� ȿ��
						else if(Remain_Tick[id] % 40 == 20 && (Sabos.getType() == SaboType.NUCL || Sabos.getType() == SaboType.OXYG))
							for(Player ap : Bukkit.getOnlinePlayers()) PlayerUtil.toggleRedEffect(ap, false);
						if (Remain_Tick[id] == 0) {
							switch(Sabos.getType()) {
							case OXYG: GameTimer.WIN_REASON = WinReason.IMPO_OXYG; break;
							case NUCL: GameTimer.WIN_REASON = WinReason.IMPO_NUCL; break;
							default: break;
							}
							
						}
					} else if (Remain_Tick[id] == 0) {
						saboClear(id);
					}
				} else if (Remain_Tick[id] < 0) { // Ŭ����
					Remain_Tick[id] = 0;
				}

				if (Remain_Tick[0] <= 0) {
					if (Sabo_Cool[id] > 0) {
						Sabo_Cool[id]--;
					}
				}

			}
		}

	}

}