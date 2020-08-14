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
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import bepo.au.GameTimer;
import bepo.au.Main;
import bepo.au.MissionList;
import bepo.au.manager.EventManager;

public abstract class Mission implements Listener{
	
	public enum MissionType {
		
		VERIFY,
		HARD,
		EASY;
		
	}
	
	public static List<Mission> MISSIONS = new ArrayList<Mission>();
	
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
	
	private static final EventManager em = new EventManager();
	
	protected static Main main;
	
	public static void initMain(Main m) {
		main = m;
	}
	
	protected String name;
	protected String korean;
	protected Location[] loc;
	protected MissionType type;
	
	protected Inventory gui;
	protected String gui_title;
	
	protected int required_clear = 0;
	protected int clear = 0;
	
	public Mission(MissionType mt, String name, String korean, int required_clear, Location... loc) {
		this.name = name;
		this.type = mt;
		this.required_clear = required_clear;
		this.loc = loc;
		MissionList.CARDS.add(this);
	}

	public String getMissionName() { return this.name; }
	public String getKoreanName() { return this.korean; }
	public Location[] getLocations() { return this.loc; }
	public String getTitle() { return gui_title; }
	public int getRequiredClear() { return this.required_clear; }
	
	public final void uploadInventory(InventoryHolder owner, int slot, String name) {
		Inventory inv = Bukkit.createInventory(owner, slot, name);
		this.gui = inv;
		this.gui_title = name;
	}
	
	public final void reset() {
		clear = 0;
		gui = null;
		gui_title = null;
	}
	
	
	public final void generalClear(Player p) {
		clear++;
		p.closeInventory();
		p.sendMessage(Main.PREFIX + "��a�ӹ� �Ϸ�!");
		PlayerData pd = PlayerData.getPlayerData(p.getName());
		if(pd != null && !pd.isImposter()) {
			GameTimer.CLEARED_MISSION++;
			
			if(GameTimer.CLEARED_MISSION == GameTimer.REQUIRED_MISSION) {
				
				/*
				 * 
				 * ������ ���
				 * 
				 */
				
			}
			
		}
	}
	
	public final int getCode(Location bloc) {
		bloc = bloc.getBlock().getLocation();
		for(int i=0;i<loc.length;i++) {
			if(bloc.equals(loc[i])) return i;
		}
		return -1;
	}
	
	public final boolean isCleared() {
		return clear >= required_clear;
	}
	
	public final boolean checkPlayer(Event event) {
		if(event instanceof InventoryEvent) {
			boolean bool = ((InventoryEvent) event).getView().getTitle().equalsIgnoreCase(gui_title);
			boolean bool2 = false;
			if(event instanceof InventoryCloseEvent) bool2 = ((InventoryCloseEvent) event).getPlayer() instanceof Player;
			else if(event instanceof InventoryClickEvent) bool2 = ((InventoryClickEvent) event).getWhoClicked() instanceof Player;
			else if(event instanceof InventoryDragEvent) bool2 = ((InventoryDragEvent) event).getWhoClicked() instanceof Player;
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