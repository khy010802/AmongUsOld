package bepo.au;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.bukkit.scoreboard.Team.Option;
import org.bukkit.scoreboard.Team.OptionStatus;

import bepo.au.base.Mission;
import bepo.au.manager.CommandManager;

public class Main extends JavaPlugin implements Listener{
	
	public static String PREFIX = "¡×a[AmongUs] ";
	
	public static GameTimer gt = null;
	public static Main main;
	public CommandManager cm;
	
	public static Scoreboard MAIN_SCOREBOARD;
	public static Team MAIN_TEAM;
	
	public static String WORLD_NAME = "world";
	public static World w;
	
	public static int SIGHT_BLOCK = 16;
	public static int KILL_COOLTIME_SEC = 20;
	public static int MISSION_DIFFICULTY = 5;
	public static int IMPOSTER_AMOUNT = 3;
	public static int EMER_BUTTON_COOL_SEC = 60;
	public static int SABOTAGE_DIFFICULTY = 5;
	public static int MISSION_AMOUNT = 5;
	
	public static List<String> OBSERVER = new ArrayList<String>();
	
	public void onEnable() {
		main = this;
		w = Bukkit.getWorld(WORLD_NAME);
		
		MAIN_SCOREBOARD = Bukkit.getScoreboardManager().getMainScoreboard();
		
		if(MAIN_SCOREBOARD.getTeam("amongus") == null) {
			Team t = MAIN_SCOREBOARD.registerNewTeam("amongus");
			t.setAllowFriendlyFire(true);
			t.setCanSeeFriendlyInvisibles(false);
			t.setOption(Option.NAME_TAG_VISIBILITY, OptionStatus.NEVER);
			t.setOption(Option.COLLISION_RULE, OptionStatus.NEVER);
		}
		MAIN_TEAM = MAIN_SCOREBOARD.getTeam("amongus");
		
		for(Mission m : MissionList.CARDS) {
			Bukkit.getConsoleSender().sendMessage(m.getMissionName());
			Mission.MISSIONS.add(m);
		}
		
		cm = new CommandManager();
		
		getCommand("au").setExecutor(cm);
		
		Mission.initMain(this);
		
		Bukkit.getConsoleSender().sendMessage("AmongUs È°¼ºÈ­. By Team JonJAr");
	}
	
	public void onDisable() {
		
	}

}
