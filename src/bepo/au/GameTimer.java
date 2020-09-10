package bepo.au;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.scheduler.BukkitRunnable;

import bepo.au.base.Mission;
import bepo.au.base.PlayerData;
import bepo.au.function.MissionList;
import bepo.au.manager.ScoreboardManager;
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
	
	public Color[] COLORLIST 			= { Color.AQUA, Color.BLUE, Color.GRAY, Color.GREEN, Color.ORANGE, Color.PURPLE, Color.RED, Color.WHITE, Color.YELLOW, Color.SILVER, Color.NAVY, Color.TEAL };
	public ChatColor[] CHATCOLORLIST 	= { ChatColor.AQUA, ChatColor.BLUE, ChatColor.DARK_GRAY, ChatColor.GOLD, ChatColor.DARK_PURPLE, ChatColor.WHITE, ChatColor.YELLOW, ChatColor.GRAY, ChatColor.BLUE };
	public List<Color> COLORS = new ArrayList<Color>();
	public List<ChatColor> CHATCOLORS = new ArrayList<ChatColor>();
	
	private int timer = 0;
	
	private Main main;
	
	private Status status = Status.READY;
	private boolean pause = false;

	public static List<String> OBSERVER = new ArrayList<String>();
	public static List<String> PLAYERS = new ArrayList<String>();
	public static List<String> IMPOSTER = new ArrayList<String>();
	
	public static int REQUIRED_MISSION = 0;
	public static int CLEARED_MISSION = 0;
	
	private Assemble assemble;
	
	public GameTimer(Main main) {
		this.main = main;
		assemble = new Assemble(main, new ScoreboardManager());
		assemble.setAssembleStyle(AssembleStyle.VIPER);
		
	}
	
	public static final int getRemainImposter() {
		int i = 0;
		for(String name : IMPOSTER) {
			if(PlayerData.getPlayerData(name) != null && PlayerData.getPlayerData(name).isAlive()) i++;
		}
		return i;
	}
	
	public Status getStatus() { return this.status; }
	
	
	public void start(Player p) {
		reset();
		Bukkit.broadcastMessage(Main.PREFIX + "§e" + p.getName() + "§f님께서 게임을 시작하셨습니다!");
		status = Status.GAME_SETTING;
		this.runTaskTimer(main, 0L, 1L);
	}
	
	public void stop() {
		if(!this.isCancelled()) this.cancel();
		
		Mission.deactivateMission();
	}
	
	private void setting() {
		for(Player ap : Bukkit.getOnlinePlayers()) {
			if(!GameTimer.OBSERVER.contains(ap.getName().toLowerCase())) {
				PLAYERS.add(ap.getName());
				ap.getInventory().clear();
				ap.setExp(0F);
				ap.setLevel(0);
			}
		}

		REQUIRED_MISSION = (Main.COMMON_MISSION_AMOUNT + Main.EASY_MISSION_AMOUNT + Main.HARD_MISSION_AMOUNT) * (PLAYERS.size() - Main.IMPOSTER_AMOUNT);
	}
	
	private void reset() {
		PLAYERS.clear();
		CLEARED_MISSION = 0;
		Main.gt = null;
	}
	
	private void team_split() {
		String imposter = "";
		
		long seed = System.currentTimeMillis();
		Random rn = new Random(seed);
		
		Collections.shuffle(PLAYERS);
		Collections.shuffle(COLORS, rn);
		Collections.shuffle(CHATCOLORS, rn);
		
		COLORS = new ArrayList<Color>(Arrays.asList(COLORLIST));
		CHATCOLORS = new ArrayList<ChatColor>(Arrays.asList(CHATCOLORLIST));
		
		for(int i=0;i<PLAYERS.size();i++) {
			String name = PLAYERS.get(i);
			PlayerData pd = new PlayerData(name);
			if(i < Main.IMPOSTER_AMOUNT) {
				IMPOSTER.add(name);
				imposter = imposter + name + " ";
			}
			pd.setColor(COLORS.get(i));
			pd.setChatColor(CHATCOLORS.get(i));
		}
		
		give_item(imposter);
	}
	
	private void give_item(String imposter) {
		
		for(String name : PLAYERS) {
			if(Bukkit.getPlayer(name) == null) return;
			Player p = Bukkit.getPlayer(name);
			PlayerData pd = PlayerData.getPlayerData(p.getName());
			
			p.setPlayerListName(pd.getChatColor() + "§o" + p.getName());
			
			String[] parts = { "BOOTS", "LEGGINGS", "CHESTPLATE", "HELMET",  };
			ItemStack[] ac = new ItemStack[4];
			
			for(int temp=0;temp<4;temp++) {
				ItemStack is = new ItemStack(Material.getMaterial("LEATHER_" + parts[temp]));
				LeatherArmorMeta lam = (LeatherArmorMeta) is.getItemMeta();
				lam.setColor(pd.getColor());
				lam.setUnbreakable(true);
				is.setItemMeta(lam);
				ac[temp] = is;
			}
			p.getInventory().setArmorContents(ac);
			
			if(IMPOSTER.contains(p.getName())) {
				p.sendTitle("§4§l임포스터", "§c모든 크루원을 죽이십시오", 10, 100, 10);
				p.sendMessage("§f=======================");
				p.sendMessage("§4당신은 임포스터입니다.");
				p.sendMessage("§c인벤토리에 검을 지급해드렸습니다. 적절한 시기에 크루원을 죽이십시오. (쿨타임 " + Main.KILL_COOLTIME_SEC + "초)");
				p.sendMessage("§c일과 진행도가 100%에 도달하기 전 임포스터와 크루원의 수가 같아지거나,");
				p.sendMessage("§c산소/원자로 사보타지가 성공했을 경우 임포스터의 승리입니다.");
				p.sendMessage("§c");
				p.sendMessage("§c임포스터 플레이어 : §f" + imposter);
				p.sendMessage("§f=======================");
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
			random_mission(p);
		}
		
		
	}
	
	private void random_mission(Player p) {
		PlayerData pd = PlayerData.getPlayerData(p.getName());
		
		if(IMPOSTER.contains(p.getName())) {
			pd.addLine("");
		}

		
		if(Main.EASY_MISSION_AMOUNT > 0) {
			int[] a_easy = Util.difrandom(0, MissionList.EASY.size(), Main.EASY_MISSION_AMOUNT);
			for(int index=0;index<a_easy.length;index++) pd.addMission(p, MissionList.EASY.get(a_easy[index]).getClone());
		}
		
		if(Main.HARD_MISSION_AMOUNT > 0) {
			int[] a_hard = Util.difrandom(0, MissionList.HARD.size(), Main.HARD_MISSION_AMOUNT);
			for(int index=0;index<a_hard.length;index++) pd.addMission(p, MissionList.HARD.get(a_hard[index]).getClone());
		}
		
		if(Main.COMMON_MISSION_AMOUNT > 0) {
			int[] a_common = Util.difrandom(0, MissionList.COMMON.size(), Main.COMMON_MISSION_AMOUNT);
			for(int index=0;index<a_common.length;index++) pd.addMission(p, MissionList.COMMON.get(a_common[index]).getClone());
		}
		
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
		}
		
		if(!pause) timer++;
	}

}
