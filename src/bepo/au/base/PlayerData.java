package bepo.au.base;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import bepo.au.GameTimer;
import bepo.au.Main;
import bepo.au.GameTimer.WinReason;
import bepo.au.Main.SETTING;
import bepo.au.base.Sabotage.SaboType;
import bepo.au.function.CCTV;
import bepo.au.function.CCTV.E_cctv;
import bepo.au.function.ItemList;
import bepo.au.function.Vent;
import bepo.au.utils.ColorUtil;
import bepo.au.utils.PlayerUtil;
import bepo.au.utils.Util;

public class PlayerData {
	
	private static HashMap<String, PlayerData> PLAYERDATA = new HashMap<String, PlayerData>();
	
	public static PlayerData getPlayerData(String name) {
		return PLAYERDATA.get(name.toLowerCase());
	}
	
	public static List<PlayerData> getPlayerDataList(){
		return new ArrayList<PlayerData>(PLAYERDATA.values());
	}
	
	public static void resetPlayerDataList() {
		
		for(PlayerData pd : PLAYERDATA.values()) {
			pd.killDecoy();
		}
		
		PLAYERDATA.clear();
	}
	
	//임포 전용
	private int kill_remain_tick = 100;
	private int emerg_remain_time = 1;
	
	private String name;
	private UUID uuid;
	private ColorUtil color;
	
	private boolean survive = true;
	
	private List<Mission> missions = new ArrayList<Mission>();
	public int cleared_missions = 0;
	
	private List<String> scoreboard_line = new ArrayList<String>();
	
	private SaboType sabo_selected = SaboType.COMM;
	private int sabo_selected_door_id = 1;
	
	private Vent now_vent = null;
	private int now_vent_loc = 0;
	
	private int now_cctv_loc = -1;
	private ArmorStand cctv_decoy = null;
	private boolean cctv_move = true;
	
	private ItemStack head = Util.createItem(Material.PLAYER_HEAD, 1, " ",null);

	public PlayerData(String name, UUID uuid) {
		this.name = name;
		this.uuid = uuid;
		this.head = Util.createHead(name);
		PLAYERDATA.put(name.toLowerCase(), this);
		emerg_remain_time = SETTING.EMER_BUTTON_PER_PLAYER.getAsInteger();
		
	}
	
	public String getName() { return this.name; }
	public UUID getUUID() { return this.uuid; }
	public ColorUtil getColor() { return this.color; }
	public boolean isAlive() { return this.survive; }
	public ItemStack getHead(){return this.head.clone();}//클론 반환
	public ItemStack getHead(boolean clone){return (clone ? this.head.clone():this.head);}

	public SaboType getSelectedSabo() { return this.sabo_selected; }
	public int getSelectedSaboDoor() { return this.sabo_selected_door_id; }
	public Vent getVent() { return now_vent; }
	
	public int getRemainEmerg() { return this.emerg_remain_time; }
	public void subtractRemainEmerg() { this.emerg_remain_time--; }
	
	public int getKillCool() { return this.kill_remain_tick; }
	public void resetKillCool(boolean after_vote) { this.kill_remain_tick = after_vote ? 100 : SETTING.KILL_COOLTIME_SEC.getAsInteger() * 20; }
	public void subtractKillCool() { if(this.kill_remain_tick > 0) this.kill_remain_tick--; }
	

	public void updateItems(Player p) {
		if(survive) {
			updateItem(p, ItemList.I_SWORD.getType());
		}
		updateItem(p, ItemList.I_SABOTAGE_CRIT.getType());
		updateItem(p, ItemList.I_SABOTAGE_DOOR.getType());
	}
	
	public void updateItem(Player p, Material mat) {
		
		int slot = p.getInventory().first(mat);
		
		if(slot == -1) return;
		
		if(mat == ItemList.I_SWORD.getType())
			PlayerUtil.setItemDamage(p, slot, 1D - ((double) kill_remain_tick) / ((double) SETTING.KILL_COOLTIME_SEC.getAsInteger() * 20));
		else if(mat == ItemList.I_SABOTAGE_CRIT.getType()) 
		{
			if(Sabotage.isActivating(0)) 
				PlayerUtil.setItemDamage(p, slot, 1.0D);
			else 
				PlayerUtil.setItemDamage(p, slot, 1D - ((double) Sabotage.Sabo_Cool[0]) / ((double) SETTING.SABO_COOL_SEC.getAsInteger() * 20D));
		}
		else if(mat == ItemList.I_SABOTAGE_DOOR.getType()) 
		{
			PlayerUtil.setItemDamage(p, slot, 1D - ((double) Sabotage.Sabo_Cool[sabo_selected_door_id]) / ((double) SETTING.SABO_COOL_SEC.getAsInteger() * 20D));
		}
		
	}
	
	public void moveCCTV(Player p, boolean next) {

		if (!CCTV.watchingCCTVset.contains(p)) CCTV.watchingCCTVset.add(p);//보고있는 플레이어 추가

		if(!cctv_move) return;
		
		if(!isWatchingCCTV() && isAlive()) {
			ArmorStand as = PlayerUtil.spawnDecoy(p.getLocation(), this);
			cctv_decoy = as;
			
			p.getInventory().setItem(13, ItemList.CCTV_EXIT);
		}
		
		cctv_move = false;
		new BukkitRunnable() {
			public void run() {
				cctv_move = true;
			}
		}.runTaskLater(Main.getInstance(), 3L);
		
		if(next)
			now_cctv_loc++;
		else
			now_cctv_loc--;
		
		int length = E_cctv.values().length;
		if(now_cctv_loc < 0) now_cctv_loc = length - 1;
		if(now_cctv_loc == length) now_cctv_loc = 0;
		
		E_cctv cctv = E_cctv.values()[now_cctv_loc];
		
		p.setGameMode(GameMode.SPECTATOR);
		p.setSpectatorTarget(cctv.getEntity());
		
		String ac = "";
		for(E_cctv c : E_cctv.values()) {
			if(c.getName() == cctv.getName())
				ac += "§a";
			else
				ac += "§7";
			ac += c.getName() + " ";
			
		}
		p.sendTitle("", " " + ac, 0, 20000, 0);
		p.sendActionBar("§eShift§f를 눌러 위치를 변경하고, §e인벤토리의 철 문 아이템§f을 눌러 중단합니다.");
		
	}
	
	public void exitCCTV(Player p) {
		if (CCTV.watchingCCTVset.contains(p)) CCTV.watchingCCTVset.remove(p);//보고있는 목록에서 제거

		now_cctv_loc = -1;
		if(isAlive()) 
			p.setGameMode(GameMode.SURVIVAL);
		else
			p.setSpectatorTarget(null);
		if(cctv_decoy != null) p.teleport(cctv_decoy.getLocation());
		p.resetTitle();
		p.sendActionBar("");
		p.getInventory().setItem(13, new ItemStack(Material.AIR));
		
		killDecoy();
	}
	
	public void killDecoy() {
		
		if(cctv_decoy == null) return; 
		
		cctv_decoy.remove();
		cctv_decoy = null;
		cctv_move = true;
	}
	
	public boolean isWatchingCCTV() {
		return now_cctv_loc >= 0;
	}
	
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
			else if(i == 8) p.getInventory().setItem(i, ItemList.MINIMAP.clone());
			else p.getInventory().setItem(i, new ItemStack(Material.AIR, 1));
		}
		
		p.getInventory().setHeldItemSlot(0);
		PlayerUtil.setInvisible(p, false);
		
		if(!force) {
			Location loc = now_vent.getList().get(now_vent_loc).clone();
			loc.setY(Vent.VENT_Y_VALUE);
			loc.setX(loc.getBlockX() + 0.5D);
			loc.setZ(loc.getBlockZ() + 0.5D);
			loc.setYaw(p.getLocation().getYaw());
			loc.setPitch(p.getLocation().getPitch());
			Util.setDoor(loc, true);
			
			loc.setY(Vent.CHECK_Y_VALUE+1.5D);
			p.teleport(loc);
			new BukkitRunnable() {
				public void run() {
					PlayerUtil.goVelocity(p, loc.clone().add(0, 4, 0), 0.6D);
				}
			}.runTaskLater(Main.getInstance(), 1L);
			
			
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
			case ELEC: sabo_selected = SaboType.NUCL; a_string = "§c§l원자로 용해"; break;
			case NUCL: sabo_selected = SaboType.OXYG; a_string = "§b§l산소 고갈"; break;
			case DOOR: case OXYG: sabo_selected = SaboType.COMM; a_string = "§a§l통신 제한"; break;
			}
		}
		PlayerUtil.sendActionBar(p, "§f§l선택한 사보타지 : " + a_string);
		updateItem(p, door ? ItemList.I_SABOTAGE_DOOR.getType() : ItemList.I_SABOTAGE_CRIT.getType());
	}
	
	public void setSabo(Player p, SaboType st, int id, boolean door) {
		if(door) {
			sabo_selected = SaboType.DOOR;
			sabo_selected_door_id = id;
		} else {
			sabo_selected = st;
		}
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
	
	public void kill(boolean voted_kill) {
		
		survive = false;
		
		Player p = Bukkit.getPlayer(name);
		GameTimer.ALIVE_PLAYERS.remove(p);
		GameTimer.ALIVE_IMPOSTERS.remove(name);
		
		if(!voted_kill) {
			if(SETTING.GENERATE_CORPSE.getAsBoolean()) {
				Util.spawnCorpse(p.getLocation().getBlock().getLocation(), p);
			}
			p.closeInventory();
			p.removePotionEffect(PotionEffectType.BLINDNESS);
		}
		
		p.setGameMode(GameMode.SPECTATOR);
		p.setCollidable(false);
		
		PlayerUtil.resetHidden(p);
		
		//Main.team.removeEntry(name);

		
		
		if(Main.gt != null) {
			
			Util.debugMessage(GameTimer.ALIVE_IMPOSTERS.size() + " / " + GameTimer.ALIVE_PLAYERS.size());
			
			if(GameTimer.ALIVE_IMPOSTERS.size() * 2 >= GameTimer.ALIVE_PLAYERS.size()) {
				GameTimer.WIN_REASON = WinReason.IMPO_KILLALL;
			} else if(GameTimer.ALIVE_IMPOSTERS.size() == 0) {
				GameTimer.WIN_REASON = WinReason.CREW_KILLALL;
			}
		}
		
		
	}

}
