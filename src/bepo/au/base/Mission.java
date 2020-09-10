package bepo.au.base;


import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import bepo.au.GameTimer;
import bepo.au.Main;
import bepo.au.function.MissionList;
import bepo.au.manager.EventManager;

public abstract class Mission implements Listener{
	
	public enum MissionType {
		
		SABOTAGE,
		HARD,
		EASY,
		COMMON;
		
	}
	
	public static List<Mission> MISSIONS = new ArrayList<Mission>();

	private static final EventManager em = new EventManager();
	
	protected static Main main;
	
	public static void initMain(Main m) {
		main = m;
	}
	
	public static Mission getMission(int id) {
		if(MISSIONS.size() > id) return MISSIONS.get(id);
		else return null;
	}
	
	public static Mission getMission(String name) {
		for(Mission m : MISSIONS) {
			if(name.equalsIgnoreCase(m.getMissionName())) return m;
		}
		return null;
	}
	
	public static void activateMissions() {
		Bukkit.getPluginManager().registerEvents(em, main);
		for(Mission m : MISSIONS) {
			Bukkit.getPluginManager().registerEvents(m, main);
		}
	}
	
	public static void deactivateMission() {
		
		for(Player ap : Bukkit.getOnlinePlayers()) ap.closeInventory();
		
		HandlerList.unregisterAll(em);
		for(Mission m : MISSIONS) {
			m.reset();
			HandlerList.unregisterAll(m);
		}
	}
	
	
	
	protected String playername;
	
	protected String name;
	protected String korean;
	protected Location[] locs;
	protected MissionType type;
	
	protected List<Inventory> gui = new ArrayList<Inventory>();
	protected List<String> gui_title = new ArrayList<String>();
	
	protected int required_clear = 0;
	protected List<Integer> cleared = new ArrayList<Integer>();
	
	public Mission(MissionType mt, String name, String korean, int required_clear, Location... loc) {
		this.name = name;
		this.type = mt;
		this.korean = korean;
		this.required_clear = required_clear;
		this.locs = loc;
		if(mt == MissionType.EASY) MissionList.EASY.add(this);
		else if(mt == MissionType.COMMON) MissionList.COMMON.add(this);
		else MissionList.HARD.add(this);
	}

	public String getMissionName() { return this.name; }
	public String getKoreanName() { return this.korean; }
	public Location[] getLocations() { return this.locs; }
	public List<String> getTitles() { return gui_title; }
	public int getRequiredClear() { return this.required_clear; }
	
	public String getScoreboardMessage() {
		String s = getKoreanName() + (getRequiredClear() > 1 ? "(0/" + getRequiredClear() + ")" : "");
		if(cleared.size() > 0) {
			if(cleared.size() < required_clear) s = "��e" + s;
			else s = "��a" + s;
		}
		return s;
	}
	
	public final Player getPlayer() {
		return Bukkit.getPlayer(playername);
	}
	
	public Mission getClone() {
		try {
			return (Mission) this.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public final void uploadInventory(InventoryHolder owner, int slot, String name) {
		Inventory inv = Bukkit.createInventory(owner, slot, name);
		gui.add(inv);
		gui_title.add(name);
	}
	
	public final void reset() {
		cleared.clear();
		gui = null;
		gui_title = null;
	}
	
	public final void assign(Player p) {
		this.playername = p.getName();
	}
	
	
	public final void generalClear(Player p, int code) {
		cleared.add(code);
		p.closeInventory();
		p.sendMessage(Main.PREFIX + "�ӹ� �Ϸ�!");
		PlayerData pd = PlayerData.getPlayerData(p.getName());
		if(pd != null && cleared.size() == required_clear && !GameTimer.IMPOSTER.contains(p.getName())) {
			GameTimer.CLEARED_MISSION++;
			
			if(GameTimer.CLEARED_MISSION == GameTimer.REQUIRED_MISSION) {
				
				/*
				 * 
				 * ������ �¸�
				 * 
				 */
				
			}
			
		}
	}
	
	public final int getCode(Location bloc) {
		bloc = bloc.getBlock().getLocation();
		for(int i=0;i<locs.length;i++) {
			if(bloc.equals(locs[i])) return i;
		}
		return -1;
	}
	
	public final int getCode(String title) {
		try {
			return Integer.parseInt(title.split(" ")[1]);
		} catch(Exception io) {
			return -1;
		}
		
	}
	
	public final boolean isCleared(int code) {
		return cleared.contains(code);
	}
	
	public final boolean isCleared() {
		return cleared.size() >= required_clear;
	}
	
	
	public final boolean checkPlayer(Event event) {
		return checkPlayer(event, true);
	}
	
	public final boolean checkPlayer(Event event, boolean checkName) {
		if(event instanceof InventoryEvent) {
			InventoryEvent ie = (InventoryEvent) event;
			boolean bool = false;
			for(String title : gui_title) if(ie.getView().getTitle().contains(title)) bool = true;
			if(!bool) bool = gui_title.contains(ie.getView().getTitle());
			boolean bool2 = false;
			Player p = null;
			if(event instanceof InventoryCloseEvent) {
				if(((InventoryCloseEvent) event).getPlayer() instanceof Player) p = (Player) ((InventoryCloseEvent) event).getPlayer();
			}
			else {
				if(event instanceof InventoryClickEvent) {
					if(((InventoryClickEvent) event).getWhoClicked() instanceof Player) p = (Player) ((InventoryCloseEvent) event).getPlayer();
				}
				if(event instanceof InventoryDragEvent) {
					if(((InventoryDragEvent) event).getWhoClicked() instanceof Player) p = (Player) ((InventoryDragEvent) event).getWhoClicked();
				}
				
			}
			
			
			if(p != null) {
				if(playername != null && checkName) {
					bool2 = playername.equalsIgnoreCase(p.getName());
				} else bool2 = true;
			}
			
			if(ie.getInventory().getType() == InventoryType.PLAYER) bool2 = false;
			
			return bool && bool2;
		}
		return false;
	}
	
	public final ItemStack createItem(Material mat, int amount, String name, List<String> lore) {
		ItemStack is = new ItemStack(mat, amount);
		ItemMeta ism = is.getItemMeta();
		ism.setDisplayName(name);
		ism.setLore(lore);
		is.setItemMeta(ism);
		return is;
	}
	
	public final void giveItem(Player p, Material mat, int amount, String name, List<String> lore) {
		p.getInventory().addItem(createItem(mat, amount, name, lore));
		p.updateInventory();
	}
	
	public final void Stack(Inventory inv, int slot, Material mat, int amount, String name, List<String> lore) {
		inv.setItem(slot, createItem(mat, amount, name, lore));
	}
	
	public abstract void onAssigned(Player p);
	public abstract void onStart(Player p, int i);
	public abstract void onClear(Player p, int i);
	public abstract void onStop(Player p, int i);
	
	

}