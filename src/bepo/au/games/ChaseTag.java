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
		super("술래잡기");
	}
	
	public String[] getDescriptions() {
		return new String[] {
				
				"§e술래잡기",
				"§f시야가 짧은 술래를 피해 일과를 모두 수행하세요",
				
				"§e§l술래잡기",
				"§f\"약올리기란 무엇인가, 제 1편\"",
				"§7술래는 크루원과 달리 시야가 짧지만, 빠릅니다.",
				"§7디스코드 등 음성채팅을 이용하면 더 재밌습니다!",
				
				"§b적정 인원 : 5~8명"
				
		};
	}
	
	public void config(int d) {
		// 0 오리지널 술래잡기 1 마몽어스 술래잡기
		
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
				p.sendTitle("§4§l술래", "§c모든 크루원을 죽이십시오", 10, 100, 10);
				p.sendMessage("§f=======================");
				p.sendMessage("§4당신은 술래입니다.");
				p.sendMessage("§c2번 슬롯에 검을 지급해드렸습니다.");
				p.sendMessage("§c최대한 빨리 크루원을 죽이십시오. (쿨타임 " + SETTING.KILL_COOLTIME_SEC.getAsInteger() + "초)");
				p.sendMessage("§c");
				p.sendMessage("§c임포스터 플레이어 : §f" + imposter);
				p.sendMessage("§f=======================");
				
				p.getInventory().setItem(1, ItemList.I_SWORD);
				p.setWalkSpeed((float) SETTING.IMPOSTER_MOVEMENT_SPEED.getAsDouble());
				if(SETTING.IMPOSTER_ALWAYS_BLIND.getAsBoolean()) {
					p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, Integer.MAX_VALUE, 0, true));
				}
				
				pd.nextSabo(p, false);
			} else {
				p.sendTitle("§f§l크루원", "§7술래를 피해 일과를 수행하십시오", 10, 100, 10);
				p.sendMessage("§f=======================");
				p.sendMessage("§f당신은 크루원입니다.");
				p.sendMessage("§7술래를 피해 배정받은 일과를 수행하고, 일과 진행도를 100%까지 채우십시오.");
				p.sendMessage("§7술래와 크루원의 수가 같아지기 전에 모든 일과가 끝나면 크루원의 승리입니다.");
				p.sendMessage("§7");
				p.sendMessage("§c술래 : " + imposter);
				p.sendMessage("§f=======================");
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
		
		String imp = "";
		
		for(String s : GameTimer.IMPOSTER) {
			imp += PlayerData.getPlayerData(s).getColor().getChatColor() + s + " ";
		}
		
		for (Player ap : Bukkit.getOnlinePlayers()) {
			if(GameTimer.IMPOSTER.contains(ap.getName())) {
				ap.sendTitle("§4술래", "§c술래 : " + imp, 20, 100, 20);
			}
			else {
				ap.sendTitle("§a크루원", "§c술래 : " + imp, 20, 100, 20);
			}
			if(GameTimer.PLAYERS.contains(ap.getName())) GameTimer.ALIVE_PLAYERS.add(ap);
		}
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
