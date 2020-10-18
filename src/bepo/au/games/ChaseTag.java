package bepo.au.games;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import bepo.au.GameTimer;
import bepo.au.GameTimer.GameType;
import bepo.au.GameTimer.Status;
import bepo.au.Main.SETTING;
import bepo.au.base.PlayerData;
import bepo.au.function.ItemList;
import bepo.au.utils.PlayerUtil;

public class ChaseTag extends AGameTicker{

	public ChaseTag() {
		super("�������");
	}
	
	public String[] getDescriptions() {
		return new String[] {
				
				"��e�������",
				"��f�þ߰� ª�� ������ ���� �ϰ��� ��� �����ϼ���",
				
				"��e��l�������",
				"��f\"��ø���� �����ΰ�, �� 1��\"",
				"��7������ ũ����� �޸� �þ߰� ª����, �����ϴ�.",
				"��7���ڵ� �� ����ä���� �̿��ϸ� �� ��ս��ϴ�!",
				
				"��b���� �ο� : 5~8��"
				
		};
	}
	
	public void config(int d) {
		// 0 �������� ������� 1 ����� �������
		
		SETTING.GAMEMODE.setSetting(GameType.CHASETAG);
		SETTING.EMER_BUTTON_PER_PLAYER.setSetting(0);
		SETTING.MOVEMENT_SPEED.setSetting(new double[] { 0.2D, 0.2D }[d]);
		SETTING.COMMON_MISSION_AMOUNT.setSetting(new int[] { 1, 1 }[d]);
		SETTING.EASY_MISSION_AMOUNT.setSetting(new int[] { 2, 2}[d]);
		SETTING.HARD_MISSION_AMOUNT.setSetting(new int[] { 0, 1}[d]);
		SETTING.VISUAL_TASK.setSetting(false);
		SETTING.CREW_SIGHT_BLOCK.setSetting(new int[] { 40, 20 }[d]);
		SETTING.IMPOSTER_SIGHT_BLOCK.setSetting(new int[] { 20, 20 }[d]);
		SETTING.SABO_COOL_SEC.setSetting(new int[] { 999, 999 }[d]);
		SETTING.SABO_CRIT_DURA_SEC.setSetting(new int[] { 999, 999 }[d]);
		SETTING.KILL_COOLTIME_SEC.setSetting(new int[] { 20, 10 }[d]);
		SETTING.IMPOSTER_MOVEMENT_SPEED.setSetting(new double[] { 0.5D, 0.5D }[d]);
		
		SETTING.ENABLE_CORPSE_REPORT.setSetting(false);
		SETTING.GENERATE_CORPSE.setSetting(true);
		SETTING.IMPOSTER_ALWAYS_BLIND.setSetting(true);
	}

	@Override
	public void setting(World w) {
		defaultGameSetting(w);
	}

	@Override
	public void team_split() {
		defaultTeamSplit();
	}

	@Override
	public void give_item() {
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
				p.sendTitle("��4��l����", "��c��� ũ����� ���̽ʽÿ�", 10, 100, 10);
				p.sendMessage("��f=======================");
				p.sendMessage("��4����� �����Դϴ�.");
				p.sendMessage("��c2�� ���Կ� ���� �����ص�Ƚ��ϴ�.");
				p.sendMessage("��c�ִ��� ���� ũ����� ���̽ʽÿ�. (��Ÿ�� " + SETTING.KILL_COOLTIME_SEC.getAsInteger() + "��)");
				p.sendMessage("��c");
				p.sendMessage("��c�������� �÷��̾� : ��f" + imposter);
				p.sendMessage("��f=======================");
				
				p.getInventory().setItem(1, ItemList.I_SWORD);
				p.setWalkSpeed((float) SETTING.IMPOSTER_MOVEMENT_SPEED.getAsDouble());
				if(SETTING.IMPOSTER_ALWAYS_BLIND.getAsBoolean()) {
					p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, Integer.MAX_VALUE, 0, true));
				}
				
				pd.nextSabo(p, false);
			} else {
				p.sendTitle("��f��lũ���", "��7������ ���� �ϰ��� �����Ͻʽÿ�", 10, 100, 10);
				p.sendMessage("��f=======================");
				p.sendMessage("��f����� ũ����Դϴ�.");
				p.sendMessage("��7������ ���� �������� �ϰ��� �����ϰ�, �ϰ� ���൵�� 100%���� ä��ʽÿ�.");
				p.sendMessage("��7������ ũ����� ���� �������� ���� ��� �ϰ��� ������ ũ����� �¸��Դϴ�.");
				p.sendMessage("��7");
				p.sendMessage("��c���� : " + imposter);
				p.sendMessage("��f=======================");
			}
		}
		
		random_mission();
	}

	@Override
	public void random_mission() {
		defaultRandomMission();
	}

	@Override
	public void onGameStart(World w) {
		
	}

	@Override
	public void tick(Status status, int count) {
		if(status == Status.WORKING) {
			for(String name : GameTimer.ALIVE_IMPOSTERS) {
				if(Bukkit.getPlayer(name) != null) {
					PlayerData pd = PlayerData.getPlayerData(name);
					pd.updateItems(Bukkit.getPlayer(name));
					if(pd.getVent() == null) pd.subtractKillCool();
				}
			}
		}
	}

}
