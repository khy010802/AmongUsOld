package bepo.au.base;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.entity.Player;

import bepo.au.Main;
import fr.minuskube.netherboard.Netherboard;
import fr.minuskube.netherboard.bukkit.BPlayerBoard;

public class PlayerData {
	
	private static HashMap<String, PlayerData> PLAYERDATA = new HashMap<String, PlayerData>();
	
	public static PlayerData getPlayerData(String name) {
		return PLAYERDATA.get(name.toLowerCase());
	}
	
	private String name;
	private Color color;
	private ChatColor chatcolor;
	
	private boolean isImposter = false;
	
	private boolean survive = true;
	
	private int workID = 0;
	
	private List<Mission> missions = new ArrayList<Mission>();
	
	private BPlayerBoard board;
	private int score = 15;
	
	public PlayerData(String name) {
		this.name = name;
		PLAYERDATA.put(name.toLowerCase(), this);
	}
	
	public String getName() { return this.name; }
	public boolean isImposter() { return this.isImposter; }
	public Color getColor() { return this.color; }
	public ChatColor getChatColor() { return this.chatcolor; }
	public int getWorkID() { return this.workID; }
	public boolean isAlive() { return this.survive; }
	public BPlayerBoard getBoard() { return this.board; }
	
	public List<Mission> getMissions() { return this.getMissions(); }
	
	public void setImposter() { this.isImposter = true; }
	public void setColor(Color c) { this.color = c; }
	public void setChatColor(ChatColor c) { this.chatcolor = c; }
	public void setWorkID(int id) { this.workID = id; }
	
	public void registerBoard() {
		board = Netherboard.instance().createBoard(Bukkit.getPlayer(name), Main.MAIN_SCOREBOARD, "AMONG US");
	}
	
	public void modifyLine(int line, String string) {
		board.set(string, 11-line);
	}
	
	public void addLine(String line) {
		board.set(line, score);
		score--;
	}
	
	public void addMission(Mission m) { 
		addLine(m.getKoreanName() + (m.getRequiredClear() > 1 ? "(0/" + m.getRequiredClear() + ")" : ""));
		missions.add(m);
	}
	
	public void kill() {
		survive = false;
		
		Player p = Bukkit.getPlayer(name);
		p.closeInventory();
	}

}
