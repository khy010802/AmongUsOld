package bepo.au;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import bepo.au.base.Mission;
import bepo.au.base.PlayerData;
import bepo.au.base.Sabotage;
import bepo.au.function.AdminMap;
import bepo.au.function.ItemList;
import bepo.au.function.MissionList;
import bepo.au.function.SightTimer;
import bepo.au.manager.ScoreboardManager;
import bepo.au.utils.ColorUtil;
import bepo.au.utils.PlayerUtil;
import bepo.au.utils.Util;
import io.github.thatkawaiisam.assemble.Assemble;
import io.github.thatkawaiisam.assemble.AssembleStyle;

public class GameTimer extends BukkitRunnable{
	
	public enum Status {
		READY,
		GAME_SETTING,
		WORKING,
		VOTING,
		END;
	}
	
	public final ColorUtil[] COLORLIST 			= { ColorUtil.CYAN, ColorUtil.BLUE, ColorUtil.GRAY, ColorUtil.GREEN, ColorUtil.ORANGE, ColorUtil.PURPLE, ColorUtil.RED, ColorUtil.WHITE, ColorUtil.YELLOW, ColorUtil.PINK, ColorUtil.BLACK, ColorUtil.LIGHT_GRAY };
	public List<ColorUtil> COLORS = new ArrayList<ColorUtil>();
	
	private int timer = 0;
	
	private Main main;
	
	private Status status = Status.READY;
	private boolean pause = false;

	public static List<String> OBSERVER = new ArrayList<String>();
	public static List<String> PLAYERS = new ArrayList<String>();
	public static List<String> IMPOSTER = new ArrayList<String>();
	
	public static List<Player> ALIVE_PLAYERS = new ArrayList<Player>();
	public static List<String> ALIVE_IMPOSTERS = new ArrayList<String>();
	
	public static int REQUIRED_MISSION = 0;
	public static int CLEARED_MISSION = 0;
	
	public static int EMERG_REMAIN_TICK = 0;
	
	private Assemble assemble;
	
	public GameTimer(Main main) {
		this.main = main;
		assemble = new Assemble(main, new ScoreboardManager());
		assemble.setAssembleStyle(AssembleStyle.KOHI);
		
	}
	
	public static final int getRemainImposter() {
		int i = 0;
		for(String name : IMPOSTER) {
			if(PlayerData.getPlayerData(name) != null && PlayerData.getPlayerData(name).isAlive()) i++;
		}
		return i;
	}
	
	public Status getStatus() { return this.status; }
	public void setStatus(Status s) { this.status = s; }
	
	
	public void start(Player p) {
		Bukkit.broadcastMessage(Main.PREFIX + "§e" + p.getName() + "§f님께서 게임을 시작하셨습니다!");
		status = Status.GAME_SETTING;
		this.runTaskTimer(main, 0L, 1L);
	}
	
	public void stop() {
		if(!this.isCancelled()) this.cancel();
		stop_reset();
		Mission.deactivateMission();
		SightTimer.stop();
		for(Player ap : Bukkit.getOnlinePlayers()) ap.removePotionEffect(PotionEffectType.BLINDNESS);
	}
	
	private void setting() {
		for(Player ap : Bukkit.getOnlinePlayers()) {
			if(!GameTimer.OBSERVER.contains(ap.getName().toLowerCase())) {
				PLAYERS.add(ap.getName());
				//ap.getInventory().clear();
				ap.setExp(0F);
				ap.setLevel(0);
			}
		}
		
		if(Main.COMMON_MISSION_AMOUNT > 0) {
			int[] a_common = Util.difrandom(0, MissionList.COMMON.size()-1, Main.COMMON_MISSION_AMOUNT);
			for(int index=0;index<a_common.length;index++) Commons[index] = MissionList.COMMON.get(a_common[index]).getClone();
		}
		
		EMERG_REMAIN_TICK = Main.EMER_BUTTON_COOL_SEC * 20;
		REQUIRED_MISSION = 0;
	}
	
	private void stop_reset() {
		Main.gt = null;
		Commons = new Mission[2];
		PLAYERS.clear();
		CLEARED_MISSION = 0;
		HandlerList.unregisterAll(Main.getEventManager());
		Mission.deactivateMission();
	}
	
	private void team_split() {
		String imposter = "";
		
		long seed = System.currentTimeMillis();
		Random rn = new Random(seed);
		
		Collections.shuffle(PLAYERS, rn);
		COLORS.clear();
		for(ColorUtil cu : COLORLIST) COLORS.add(cu);
		Collections.shuffle(COLORS, rn);
		
		for(int i=0;i<PLAYERS.size();i++) {
			if(Bukkit.getPlayer(PLAYERS.get(i)) == null) continue;
			
			String name = PLAYERS.get(i);
			PlayerData pd = new PlayerData(name, Bukkit.getPlayer(PLAYERS.get(i)).getUniqueId());
			if(i < Main.IMPOSTER_AMOUNT) {
				IMPOSTER.add(name);
				imposter = imposter + name + " ";
			}
			pd.setColor(COLORS.get(i));
		}
		
		give_item(imposter);
	}
	
	private void give_item(String imposter) {
		
		for(String name : PLAYERS) {
			if(Bukkit.getPlayer(name) == null) return;
			Player p = Bukkit.getPlayer(name);
			PlayerData pd = PlayerData.getPlayerData(p.getName());
			
			//p.getInventory().clear();
			p.setPlayerListName(pd.getColor().getChatColor() + p.getName());
			
			ItemStack[] ac = PlayerUtil.getColoredArmorContent(pd.getColor());
			p.getInventory().setArmorContents(ac);
			
			if(IMPOSTER.contains(p.getName())) {
				p.sendTitle("§4§l임포스터", "§c모든 크루원을 죽이십시오", 10, 100, 10);
				p.sendMessage("§f=======================");
				p.sendMessage("§4당신은 임포스터입니다.");
				p.sendMessage("§c2번 슬롯에 검을 지급해드렸습니다. 적절한 시기에 크루원을 죽이십시오. (쿨타임 " + Main.KILL_COOLTIME_SEC + "초)");
				p.sendMessage("§c숫자 키 3번, 4번으로 사보타지를 선택할 수 있으며,");
				p.sendMessage("§c손 바꾸기(기본 설정 F) 키를 눌러 사보타지를 발동할 수 있습니다.");
				p.sendMessage("§c");
				p.sendMessage("§c임포스터 플레이어 : §f" + imposter);
				p.sendMessage("§f=======================");
				
				PlayerUtil.getImposterSet(p, true);
				
				pd.nextSabo(p, false);
			} else {
				p.sendTitle("§f§l크루원", "§7모든 임포스터를 추방하십시오", 10, 100, 10);
				p.sendMessage("§f=======================");
				p.sendMessage("§f당신은 크루원입니다.");
				p.sendMessage("§7배정받은 일과를 수행하고, 일과 진행도를 100%까지 채우십시오.");
				p.sendMessage("§7모든 임포스터가 사망하거나 일과 진행도가 100%에 도달하면 크루원의 승리입니다.");
				p.sendMessage("§7");
				p.sendMessage("§f일과는 우측 스코어보드에 표기되며, 진행도는 경험치 바에 표기됩니다.");
				p.sendMessage("§f=======================");
			}
			
			p.getInventory().setItem(8, ItemList.VOTE_PAPER.clone());
			
			
			random_mission(p);
		}
		
		
	}
	
	private Mission[] Commons = new Mission[2];
	
	private void random_mission(Player p) {
		PlayerData pd = PlayerData.getPlayerData(p.getName());
		
		if(Main.COMMON_MISSION_AMOUNT > 0) {
			for(int index=0;index<Commons.length;index++) if(Commons[index] != null) {
				pd.addMission(p, Commons[index].getClone());
			}
		}
		
		if(Main.EASY_MISSION_AMOUNT > 0) {
			int[] a_easy = Util.difrandom(0, MissionList.EASY.size()-1, Main.EASY_MISSION_AMOUNT);
			
			for(int index=0;index<a_easy.length;index++) {
				pd.addMission(p, MissionList.EASY.get(a_easy[index]).getClone());
			}
		}
		
		if(Main.HARD_MISSION_AMOUNT > 0) {
			int[] a_hard = Util.difrandom(0, MissionList.HARD.size()-1, Main.HARD_MISSION_AMOUNT);
			for(int index=0;index<a_hard.length;index++) pd.addMission(p, MissionList.HARD.get(a_hard[index]).getClone());
		}
		
		if(!IMPOSTER.contains(p.getName())) REQUIRED_MISSION += (Main.COMMON_MISSION_AMOUNT + Main.EASY_MISSION_AMOUNT + Main.HARD_MISSION_AMOUNT);
		
		assemble.start(5L);
	}
	
	
	public void run() {
		
		switch(timer) {
		case 0:
			setting();
			break;
		case 60:
			Bukkit.broadcastMessage(Main.PREFIX + "§f=====================");
			Bukkit.broadcastMessage(Main.PREFIX + "§e게임 참가자 목록");
			for(int i=0;i<PLAYERS.size();i++) {
				Bukkit.broadcastMessage(Main.PREFIX + "§f" + (i+1) + ". §a" + PLAYERS.get(i));
			}
			Bukkit.broadcastMessage(Main.PREFIX + "§f=====================");
			break;
		case 160:
			Bukkit.broadcastMessage(Main.PREFIX + "§f직업을 분배합니다...");
			break;
		case 200:
			team_split();
			break;
		case 300:
			Bukkit.broadcastMessage(Main.PREFIX + "§f직업 분배가 완료되었습니다. 5초 뒤 게임을 시작합니다.");
			break;
		case 320:
		case 340:
		case 360:
		case 380:
		case 400:
			Bukkit.broadcastMessage(Main.PREFIX + "§c" + ((420-timer)/20) + "초 전");
			break;
		case 420:
			Bukkit.broadcastMessage(Main.PREFIX + "§a게임 시작!");
			if(Main.CREW_SIGHT_BLOCK >= 0 && Main.IMPOSTER_SIGHT_BLOCK >= 0) SightTimer.start();
			for(Player ap : Bukkit.getOnlinePlayers()) ALIVE_PLAYERS.add(ap);
			Bukkit.getPluginManager().registerEvents(Main.getEventManager(), main);
			status = Status.WORKING;
			Sabotage.saboResetAll(true);
			AdminMap.initializeGUI();
			break;
		}
		
		if(status == Status.WORKING) {
			for(String name : ALIVE_IMPOSTERS) {
				PlayerData pd = PlayerData.getPlayerData(name);
				pd.subtractKillCool();
			}
			if(EMERG_REMAIN_TICK > 0) EMERG_REMAIN_TICK--;
		}
		
		if(!pause) timer++;
	}

}
