package bepo.au.base;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.entity.Player;

public class PlayerData {
	
	private static HashMap<String, PlayerData> PLAYERDATA = new HashMap<String, PlayerData>();
	
	public static PlayerData getPlayerData(String name) {
		return PLAYERDATA.get(name.toLowerCase());
	}
	
	public static List<PlayerData> getPlayerDataList(){
		return new ArrayList<PlayerData>(PLAYERDATA.values());
	}
	
	
	
	private String name;
	private Color color;
	private ChatColor chatcolor;
	
	private boolean survive = true;
	
	private List<Mission> missions = new ArrayList<Mission>();
	
	private List<String> scoreboard_line = new ArrayList<String>();
	
	public PlayerData(String name) {
		this.name = name;
		PLAYERDATA.put(name.toLowerCase(), this);
	}
	
	public String getName() { return this.name; }
	public Color getColor() { return this.color; }
	public ChatColor getChatColor() { return this.chatcolor; }
	public boolean isAlive() { return this.survive; }
	
	public List<Mission> getMissions() { return this.getMissions(); }

	public void setColor(Color c) { this.color = c; }
	public void setChatColor(ChatColor c) { this.chatcolor = c; }
	
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
