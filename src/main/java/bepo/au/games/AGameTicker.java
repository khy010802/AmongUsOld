package bepo.au.games;

import bepo.au.GameTimer;
import bepo.au.GameTimer.Status;
import bepo.au.Main.SETTING;
import bepo.au.base.PlayerData;
import bepo.au.function.ItemList;
import bepo.au.function.MissionList;
import bepo.au.manager.LocManager;
import bepo.au.utils.ColorUtil;
import bepo.au.utils.PlayerUtil;
import bepo.au.utils.Util;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.Random;

public abstract class AGameTicker {
	
	protected String name;
	
	public AGameTicker(String name) {
		this.name = name;
	}
	
	public final void defaultGameSetting(World w) {
		for(Player ap : Bukkit.getOnlinePlayers()) {
			if(!GameTimer.OBSERVER.contains(ap.getName().toLowerCase())) {
				GameTimer.PLAYERS.add(ap.getName());
				//ap.getInventory().clear();
				ap.setHealth(20.0D);
				ap.setFoodLevel(20);
				ap.setWalkSpeed((float) SETTING.MOVEMENT_SPEED.getAsDouble());
				//Main.team.addEntry(ap.getName());
			}
		}
		
		Util.spawnEmergArmorStand(w);
		Util.toggleDescriptionArmorStand(w, false);
		
		if(LocManager.getLoc("ActivatingShield_POWER") != null) {
			LocManager.getLoc("ActivatingShield_POWER").forEach(l -> l.getBlock().setType(Material.AIR));
		}
		
		if(SETTING.COMMON_MISSION_AMOUNT.getAsInteger() > 0) {
			int[] a_common = Util.difrandom(0, MissionList.COMMON.size()-1, SETTING.COMMON_MISSION_AMOUNT.getAsInteger());
			for(int index=0;index<a_common.length;index++) GameTimer.Commons[index] = MissionList.COMMON.get(a_common[index]).getClone();
		}
		
		GameTimer.EMERG_REMAIN_TICK = SETTING.EMER_BUTTON_COOL_SEC.getAsInteger() * 20;
		GameTimer.REQUIRED_MISSION = 0;
	}
	
	public final void defaultTeamSplit() {
		
		long seed = System.currentTimeMillis();
		Random rn = new Random(seed);
		
		Collections.shuffle(GameTimer.PLAYERS, rn);
		GameTimer.COLORS.clear();
		for(ColorUtil cu : GameTimer.COLORLIST) GameTimer.COLORS.add(cu);
		Collections.shuffle(GameTimer.COLORS, rn);
		
		for(int i=0;i<GameTimer.PLAYERS.size();i++) {
			if(Bukkit.getPlayer(GameTimer.PLAYERS.get(i)) == null) continue;
			
			String name = GameTimer.PLAYERS.get(i);
			PlayerData pd = new PlayerData(name, Bukkit.getPlayer(GameTimer.PLAYERS.get(i)).getUniqueId());
			if(i < SETTING.IMPOSTER_AMOUNT.getAsInteger()) {
				GameTimer.IMPOSTER.add(name);
				GameTimer.ALIVE_IMPOSTERS.add(name);
			}
			pd.setColor(GameTimer.COLORS.get(i));
		}
		
		give_item();
	}
	
	public final void defaultGiveItem() {
		
		String imposter = "";
		
		for(String name : GameTimer.IMPOSTER) {
			imposter = imposter + name + " ";
		}
		
		for(String name : GameTimer.PLAYERS) {
			if(Bukkit.getPlayer(name) == null) return;
			Player p = Bukkit.getPlayer(name);
			PlayerData pd = PlayerData.getPlayerData(p.getName());
			
			//p.getInventory().clear();
			p.setPlayerListName(pd.getColor().getChatColor() + p.getName());
			
			ItemStack[] ac = PlayerUtil.getColoredArmorContent(pd.getColor());
			p.getInventory().setArmorContents(ac);
			
			p.getInventory().setItem(8, ItemList.MINIMAP.clone());
			
			if(GameTimer.IMPOSTER.contains(p.getName())) {
				p.sendTitle("��4��l��������", "��c��� ũ����� ���̽ʽÿ�", 10, 100, 10);
				p.sendMessage("��f=======================");
				p.sendMessage("��4����� ���������Դϴ�.");
				p.sendMessage("��c2�� ���Կ� ���� �����ص�Ƚ��ϴ�. ������ �ñ⿡ ũ����� ���̽ʽÿ�. (��Ÿ�� " + SETTING.KILL_COOLTIME_SEC.getAsInteger() + "��)");
				p.sendMessage("��c���� Ű 4��, 6������ �纸Ÿ���� ������ �� ������,");
				p.sendMessage("��c�� �ٲٱ�(�⺻ ���� F) Ű�� ���� �纸Ÿ���� �ߵ��� �� �ֽ��ϴ�.");
				p.sendMessage("��c");
				p.sendMessage("��c�������� �÷��̾� : ��f" + imposter);
				p.sendMessage("��f=======================");
				
				PlayerUtil.getImposterSet(p, true);
				
				pd.nextSabo(p, false);
			} else {
				p.sendTitle("��f��lũ���", "��7��� �������͸� �߹��Ͻʽÿ�", 10, 100, 10);
				p.sendMessage("��f=======================");
				p.sendMessage("��f����� ũ����Դϴ�.");
				p.sendMessage("��7�������� �ϰ��� �����ϰ�, �ϰ� ���൵�� 100%���� ä��ʽÿ�.");
				p.sendMessage("��7��� �������Ͱ� ����ϰų� �ϰ� ���൵�� 100%�� �����ϸ� ũ����� �¸��Դϴ�.");
				p.sendMessage("��7");
				p.sendMessage("��f�ϰ��� ���� ���ھ�忡 ǥ��Ǹ�, ���൵�� ����ġ �ٿ� ǥ��˴ϴ�.");
				p.sendMessage("��f=======================");
			}
			
		}
		
		random_mission();
	}

	public final void defaultRandomMission() {
		
		for(String name : GameTimer.PLAYERS) {
			PlayerData pd = PlayerData.getPlayerData(name);
			Player p = Bukkit.getPlayer(name);
			
			if(SETTING.COMMON_MISSION_AMOUNT.getAsInteger() > 0) {
				for(int index=0;index<GameTimer.Commons.length;index++) if(GameTimer.Commons[index] != null) {
					pd.addMission(p, GameTimer.Commons[index].getClone());
				}
			}
			
			if(SETTING.EASY_MISSION_AMOUNT.getAsInteger() > 0) {
				int[] a_easy = Util.difrandom(0, MissionList.EASY.size()-1, SETTING.EASY_MISSION_AMOUNT.getAsInteger());
				
				for(int index=0;index<a_easy.length;index++) {
					pd.addMission(p, MissionList.EASY.get(a_easy[index]).getClone());
				}
			}
			
			if(SETTING.HARD_MISSION_AMOUNT.getAsInteger() > 0) {
				int[] a_hard = Util.difrandom(0, MissionList.HARD.size()-1, SETTING.HARD_MISSION_AMOUNT.getAsInteger());
				for(int index=0;index<a_hard.length;index++) pd.addMission(p, MissionList.HARD.get(a_hard[index]).getClone());
			}
			
			if(!GameTimer.IMPOSTER.contains(p.getName())) GameTimer.REQUIRED_MISSION += (SETTING.COMMON_MISSION_AMOUNT.getAsInteger() + SETTING.EASY_MISSION_AMOUNT.getAsInteger() + SETTING.HARD_MISSION_AMOUNT.getAsInteger());
			
			GameTimer.assemble.start(10L);
		}
		
		
	}
	
	public abstract String[] getDescriptions();
	
	public abstract void config(int param);
	public abstract void setting(World w);
	public abstract void team_split();
	public abstract void give_item();
	public abstract void random_mission();
	
	public abstract void onGameStart(World w);
	
	public abstract void tick(Status status, int count);
	
}
