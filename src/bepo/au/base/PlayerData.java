package bepo.au.base;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import bepo.au.GameTimer;
import bepo.au.Main;
import bepo.au.base.Sabotage.SaboType;
import bepo.au.function.ItemList;
import bepo.au.function.Vent;
import bepo.au.utils.ColorUtil;
import bepo.au.utils.PlayerUtil;
import bepo.au.utils.Util;
import net.minecraft.server.v1_16_R2.PlayerList;

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
	
	private SaboType sabo_selected = SaboType.COMM;
	private int sabo_selected_door_id = 1;
	
	private Vent now_vent = null;
	private int now_vent_loc = 0;
	
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
	public SaboType getSelectedSabo() { return this.sabo_selected; }
	public int getSelectedSaboDoor() { return this.sabo_selected_door_id; }
	public Vent getVent() { return now_vent; }
	
	public int getRemainEmerg() { return this.emerg_remain_time; }
	public void subtractRemainEmerg() { this.emerg_remain_time--; }
	
	public int getKillCool() { return this.kill_remain_tick; }
	public void resetKillCool(boolean after_vote) { this.kill_remain_tick = after_vote ? 100 : Main.KILL_COOLTIME_SEC * 20; }
	public void subtractKillCool() { if(this.kill_remain_tick > 0) this.kill_remain_tick--; }
	
	public void setVent(Player p, Vent v, Location loc) {
		this.now_vent = v;
		now_vent_loc = v.indexOf(loc);
		
		for(int i=0;i<9;i++) {
			if(i < 4) p.getInventory().setItem(i, ItemList.I_VENT_PREV.clone());
			else if(i > 4) p.getInventory().setItem(i, ItemList.I_VENT_NEXT.clone());
			else p.getInventory().setItem(i, ItemList.I_VENT_CONFIRM);
		}
		p.getInventory().setHeldItemSlot(4);
	}
	
	public void nextVent(Player p, boolean next) {
		if(next) now_vent_loc++; else now_vent_loc--;
		if(now_vent_loc >= now_vent.getList().size()) {
			now_vent_loc = 0;
		} else if(now_vent_loc < 0) {
			now_vent_loc = now_vent.getList().size()-1;
		}
		
		Location loc = now_vent.getList().get(now_vent_loc).clone();
		Util.setDoor(loc, false);
		p.teleport(loc.add(0.5D, 0.5D, 0.5D));
	}
	
	// force는 회의때문에 강제 탈출
	public void confirmVent(Player p, boolean force) {
		// 벤트 출현
		if(now_vent == null) return;
		
		HashMap<Integer, ItemStack> hash = ItemList.getImposterSet();
		for(int i=0;i<9;i++) {
			if(hash.containsKey(i)) p.getInventory().setItem(i, hash.get(i));
			else if(i == 8) p.getInventory().setItem(i, ItemList.VOTE_PAPER.clone());
			else p.getInventory().setItem(i, new ItemStack(Material.AIR, 1));
		}
		
		p.getInventory().setHeldItemSlot(0);
		PlayerUtil.setInvisible(p, false);
		
		if(!force) {
			Location loc = now_vent.getList().get(now_vent_loc).clone();
			loc.setY(Vent.VENT_Y_VALUE);
			loc.setX(loc.getBlockX() + 0.5D);
			loc.setZ(loc.getBlockZ() + 0.5D);
			Util.setDoor(loc, true);
			
			loc.setY(Vent.CHECK_Y_VALUE+1);
			p.teleport(loc);
			PlayerUtil.goVelocity(p, loc.clone().add(0, 4, 0), 0.75D);
			
			new BukkitRunnable() {
				public void run() {
					loc.setY(Vent.VENT_Y_VALUE);
					Util.setDoor(loc, false);
				}
			}.runTaskLater(Main.getInstance(), 10L);
		}
		
		now_vent = null;
	}
	
	public void nextSabo(Player p, boolean door) {
		String a_string = "";
		if(door) {
			if(sabo_selected == SaboType.DOOR) sabo_selected_door_id++;
			else sabo_selected = SaboType.DOOR;
			if(sabo_selected_door_id > 7) sabo_selected_door_id = 1;
			a_string = "§7§l문 닫기 §f(" + Sabotage.getRoomById(sabo_selected_door_id, false) + ")";
		} else {
			switch(sabo_selected) {
			case COMM: sabo_selected = SaboType.ELEC; a_string = "§e§l전등 파괴"; break;
			case ELEC: sabo_selected = SaboType.NUCL; a_string = "§c§l원자로 용해";break;
			case NUCL: sabo_selected = SaboType.OXYG; a_string = "§b§l산소 고갈";break;
			case DOOR: case OXYG: sabo_selected = SaboType.COMM; a_string = "§a§l통신 제한"; break;
			}
		}
		PlayerUtil.sendActionBar(p, "§f§l선택한 사보타지 : " + a_string);
	}
	
	
	
	public List<Mission> getMissions() { return this.missions; }

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
		GameTimer.ALIVE_PLAYERS.remove(p);
		GameTimer.ALIVE_IMPOSTERS.remove(p.getName());
		p.closeInventory();
		p.removePotionEffect(PotionEffectType.BLINDNESS);
	}

}
