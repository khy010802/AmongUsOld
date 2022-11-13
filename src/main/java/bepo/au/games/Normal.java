package bepo.au.games;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import bepo.au.GameTimer;
import bepo.au.GameTimer.GameType;
import bepo.au.GameTimer.Status;
import bepo.au.Main.SETTING;
import bepo.au.base.PlayerData;
import bepo.au.utils.Util;

public class Normal extends AGameTicker{
	
	public Normal() {
		super("일반");
	}
	
	public String[] getDescriptions() {
		return new String[] {
				
				"§e클래식 어몽어스",
				"§f어몽어스의 쾌감을 마인크래프트에서 느껴보세요!",
				
				"§e§l클래식 어몽어스",
				"§f\"어몽어스에 마인크래프트를 싸서 드셔보세요\"",
				"§7어몽어스의 시스템과 규칙을 최대한 구현해보았습니다.",
				"§71인칭 어몽어스를 즐겨보세요!",
				
				"§b적정 인원 : 6~10명"
				
		};
	}

	public void config(int d) {
		
		// 0 이지 , 1 노말 , 2 하드 , 3 하드코어
		
		SETTING.GAMEMODE.setSetting(GameType.NORMAL);
		
		SETTING.EMER_BUTTON_PER_PLAYER.setSetting(new int[] { 3, 2, 1, 1 }[d]);
		SETTING.EMER_BUTTON_COOL_SEC.setSetting(new int[] { 15, 15, 15, 15 }[d]);
		SETTING.VOTE_MAIN_SEC.setSetting(new int[] { 170, 140, 110, 80 }[d]);
		SETTING.VOTE_PREPARE_SEC.setSetting(new int[] { 10, 10, 10, 10 }[d]);
		SETTING.CREW_SIGHT_BLOCK.setSetting(new int[] { 30, 20, 15, 10 }[d]);
		SETTING.IMPOSTER_SIGHT_BLOCK.setSetting(new int[] { 40, 30, 25, 20 }[d]);
		SETTING.SABO_COOL_SEC.setSetting(new int[] { 35, 30, 25, 20 }[d]);
		SETTING.SABO_CRIT_DURA_SEC.setSetting(new int[] { 50, 45, 40, 35 }[d]);
		SETTING.NOTICE_IMPOSTER.setSetting(new boolean[] { true, true, false, false }[d]);
		SETTING.VISUAL_TASK.setSetting(new boolean[] { true, true, false, false }[d]);
		SETTING.COMMON_MISSION_AMOUNT.setSetting(new int[] { 1, 1, 2, 2 }[d]);
		SETTING.EASY_MISSION_AMOUNT.setSetting(new int[] { 1, 2, 3, 4 }[d]);
		SETTING.HARD_MISSION_AMOUNT.setSetting(new int[] { 1, 1, 2, 3}[d]);
		SETTING.MOVEMENT_SPEED.setSetting(0.2D);
		
		SETTING.IMPOSTER_ALWAYS_BLIND.setSetting(false);
		SETTING.ENABLE_CORPSE_REPORT.setSetting(true);
		SETTING.GENERATE_CORPSE.setSetting(true);
	}

	@Override
	public void tick(Status status, int timer) {
		if(status == Status.WORKING) {
			
			for(String name : GameTimer.ALIVE_IMPOSTERS) {
				if(Bukkit.getPlayer(name) != null) {
					PlayerData pd = PlayerData.getPlayerData(name);
					pd.updateItems(Bukkit.getPlayer(name));
					if(pd.getVent() == null) pd.subtractKillCool();
				}
			}
			
			
				
			
			if(GameTimer.EMERG_REMAIN_TICK > 0) {
				GameTimer.EMERG_REMAIN_TICK--;
				ArmorStand emerg_button_as = Util.getEmergArmorStand();
				emerg_button_as.setCustomName("회의 소집 가능까지 §c" + (GameTimer.EMERG_REMAIN_TICK/20+1) + "§f초");
				
				if(GameTimer.EMERG_REMAIN_TICK == 0) {
					emerg_button_as.getEquipment().setHelmet(new ItemStack(Material.AIR));
					emerg_button_as.teleport(emerg_button_as.getLocation().clone().add(0, -1.0D, 0));
					emerg_button_as.setCustomNameVisible(false);
				}
			} 
		}
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
		defaultGiveItem();
	}

	@Override
	public void random_mission() {
		defaultRandomMission();
	}

	@Override
	public void onGameStart(World world) {
		for (Player ap : Bukkit.getOnlinePlayers()) {
			if(GameTimer.IMPOSTER.contains(ap.getName())) {
				ap.sendTitle("§4임포스터", "§c임포스터가 " + GameTimer.ALIVE_IMPOSTERS.size()+"명 §f남았습니다", 20, 100, 20);
			}
			else {
				ap.sendTitle("§a크루원", "§c임포스터가 " + GameTimer.ALIVE_IMPOSTERS.size()+"명 §f남았습니다", 20, 100, 20);
			}
			if(GameTimer.PLAYERS.contains(ap.getName())) GameTimer.ALIVE_PLAYERS.add(ap);
		}
	}

}