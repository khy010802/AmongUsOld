package bepo.au;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import bepo.au.base.Mission;
import bepo.au.function.MissionList;
import bepo.au.manager.CommandManager;
import bepo.au.manager.ScoreboardManager;
import io.github.thatkawaiisam.assemble.Assemble;
import io.github.thatkawaiisam.assemble.AssembleStyle;

public class Main extends JavaPlugin implements Listener{
	
	public static String PREFIX = "¡×a[AmongUs] ";
	
	public static GameTimer gt = null;
	private static Main main;
	public CommandManager cm;
	
	public static String WORLD_NAME = "world";
	public static World w;
	
	public static int EMER_BUTTON_PER_PLAYER = 1;
	public static int EMER_BUTTON_COOL_SEC = 60;
	public static int DISCUSS_SEC = 15;
	public static int VOTE_SEC = 120;
	public static int CREW_SIGHT_BLOCK = 16;
	public static int IMPOSTER_SIGHT_BLOCK = 24;
	public static int KILL_COOLTIME_SEC = 20;
	
	public static int COMMON_MISSION_AMOUNT = 1;
	public static int EASY_MISSION_AMOUNT = 2;
	public static int HARD_MISSION_AMOUNT = 1;
	
	public static float MOVEMENT_SPEED = 1.0F;
	
	public static int MISSION_DIFFICULTY = 5;
	public static int IMPOSTER_AMOUNT = 2;

	
	public static Main getInstance() {
		return main;
	}
	
	public void onEnable() {
		main = this;
		w = Bukkit.getWorld(WORLD_NAME);
		
		for(Mission m : MissionList.EASY) {
			Bukkit.getConsoleSender().sendMessage(m.getMissionName());
			Mission.MISSIONS.add(m);
		}
		
		for(Mission m : MissionList.HARD) {
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
