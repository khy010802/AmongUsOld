package bepo.au.base;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.entity.Player;

import bepo.au.Main;
import bepo.au.utils.ColorUtil;

public class PlayerData {
	
	private static HashMap<String, PlayerData> PLAYERDATA = new HashMap<String, PlayerData>();
	
	public static PlayerData getPlayerData(String name) {
		return PLAYERDATA.get(name.toLowerCase());
	}
	
	public static List<PlayerData> getPlayerDataList(){
		return new ArrayList<PlayerData>(PLAYERDATA.values());
	}
	
	//임포 전용
	private int kill_remain_tick = 100;
	private int emerg_remain_time = 1;
	
	private String name;
	private UUID uuid;
	private ColorUtil color;
	
	private boolean survive = true;
	
	private List<Mission> missions = new ArrayList<Mission>();
	
	private List<String> scoreboard_line = new ArrayList<String>();
	
	public PlayerData(String name, UUID uuid) {
		this.name = name;
		this.uuid = uuid;
		PLAYERDATA.put(name.toLowerCase(), this);
		emerg_remain_time = Main.EMER_BUTTON_PER_PLAYER;
	}
	
	public String getName() { return this.name; }
	public UUID getUUID() { return this.uuid; }
	public ColorUtil getColor() { return this.color; }
	public boolean isAlive() { return this.survive; }
	
	public int getRemainEmerg() { return this.emerg_remain_time; }
	public void subtractRemainEmerg() { this.emerg_remain_time--; }
	
	public int getKillCool() { return this.kill_remain_tick; }
	public void resetKillCool(boolean after_vote) { this.kill_remain_tick = after_vote ? 100 : Main.KILL_COOLTIME_SEC * 20; }
	public void subtractKillCool() { if(this.kill_remain_tick > 0) this.kill_remain_tick--; }
	
	public List<Mission> getMissions() { return this.getMissions(); }

	public void setColor(ColorUtil c) { this.color = c; }
	
	public void addLine(String line) {
		scoreboard_line.add(line);
	}
	
	public void setLine(int i, String line) {
		if(scoreboard_line.size() > i) {
			if(!scoreboard_line.get(i-1).equals(line)) scoreboard_line.set(i-1, line);
		}
	}
	
	public void addMission(Player p, Mission m) {
		
		if(p != null) m.onAssigned(p);
		else if(Bukkit.getPlayer(name) != null) m.onAssigned(Bukkit.getPlayer(name));
		
		missions.add(m);
		
	}
	
	public void kill() {
		survive = false;
		
		Player p = Bukkit.getPlayer(name);
		p.closeInventory();
	}

}
