package bepo.au;

import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;

import bepo.au.base.Mission;
import bepo.au.function.MissionList;
import bepo.au.function.Vent;
import bepo.au.manager.CommandManager;
import bepo.au.manager.EventManager;
import bepo.au.manager.LocManager;
import bepo.au.manager.TabCompleteManager;
import bepo.au.utils.ColorUtil;
import bepo.au.utils.PlayerUtil;

public class Main extends JavaPlugin implements Listener{
	
	public static String PREFIX = "¡×a[AmongUs] ";
	
	public static GameTimer gt = null;
	private static Main main;
	public CommandManager cm;
	private static EventManager em;
	private static MissionList ml;
	private static LocManager lm;
	
	public static String WORLD_NAME = "world";
	public static World w;
	
	public static int EMER_BUTTON_PER_PLAYER = 1;
	public static int EMER_BUTTON_COOL_SEC = 1;
	public static int DISCUSS_SEC = 15;
	public static int VOTE_SEC = 10;
	public static int CREW_SIGHT_BLOCK = 16;
	public static int IMPOSTER_SIGHT_BLOCK = 24;
	public static int KILL_COOLTIME_SEC = 20;
	public static int SABO_COOL_SEC = 30;
	
	public static boolean NOTICE_IMPOSTER = true;
	public static boolean VISUAL_TASK = true;
	
	public static int COMMON_MISSION_AMOUNT = 2;
	public static int EASY_MISSION_AMOUNT = 8;
	public static int HARD_MISSION_AMOUNT = 7;
	
	public static float MOVEMENT_SPEED = 1.0F;
	
	public static int IMPOSTER_AMOUNT = 0;

	
	public static Main getInstance() {
		return main;
	}
	
	public static EventManager getEventManager() {
		return em;
	}
	
	public static LocManager getLocManager() {
		return lm;
	}
	
	public static MissionList getMissionList() {
		return ml;
	}
	
	public void onEnable() {
		main = this;
		w = Bukkit.getWorld(WORLD_NAME);
		
		for(Mission m : MissionList.EASY) {
			Mission.MISSIONS.add(m);
		}
		
		for(Mission m : MissionList.HARD) {
			Mission.MISSIONS.add(m);
		}
		
		for(Mission m : MissionList.COMMON) {
			Mission.MISSIONS.add(m);
		}
		
		for(Mission m : MissionList.SABOTAGE) {
			Mission.MISSIONS.add(m);
		}
		
		cm = new CommandManager();
		em = new EventManager();
		ml = new MissionList();
		lm = new LocManager();
		lm.loadLocs();
		
		Vent.uploadVent("NW");
		Vent.uploadVent("RL");
		Vent.uploadVent("RU");
		Vent.uploadVent("NS");
		Vent.uploadVent("EES");
		Vent.uploadVent("CAH");
		
		for(Mission m : Mission.MISSIONS) addLocation(m);
		
		
		
		getCommand("au").setExecutor(cm);
		getCommand("au").setTabCompleter(new TabCompleteManager());
		
		Mission.initMain(this);
		
		Bukkit.getConsoleSender().sendMessage("AmongUs È°¼ºÈ­. By Team JonJAr");
	}
	
	private void addLocation(Mission m) {
		
		String s = m.getMissionName();
		
		List<Location> list = LocManager.getLoc(s);
		Bukkit.getConsoleSender().sendMessage(m.getKoreanName() + " list size : " + list.size());
		if(list != null && list.size() > 0) {
			for(int i=0;i<list.size();i++) m.addLocation(list.get(i));
		}
	}
	
	public void onDisable() {
		lm.saveLocs();
		for(Player p : Bukkit.getOnlinePlayers()) {
			PlayerUtil.resetHidden(p);
			PlayerUtil.resetGlowingBlock(p);
			PlayerUtil.removeChair(p);
		}

		Scoreboard board = Bukkit.getScoreboardManager().getMainScoreboard();
		for(ColorUtil c : ColorUtil.values()) {
			String tname = "sh" + c.getChatColor().name();
			if(board.getTeam(tname) != null) board.getTeam(tname).unregister();
		}
		
		if(gt != null) {
			gt.stop();
		}
		
	}

}
