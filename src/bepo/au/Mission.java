package bepo.au;


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
	
	protected String playername;
	
	protected String name;
	protected String korean;
	protected Location[] loc;
	protected MissionType type;
	
	protected List<Inventory> gui = new ArrayList<Inventory>();
	protected List<String> gui_title = new ArrayList<String>();
	
	protected int required_clear = 0;
	protected List<Integer> cleared = new ArrayList<Integer>();
	
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
	public List<String> getTitles() { return gui_title; }
	public int getRequiredClear() { return this.required_clear; }
	
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
		p.sendMessage(Main.PREFIX + "§a임무 완료!");
		PlayerData pd = PlayerData.getPlayerData(p.getName());
		if(pd != null && !pd.isImposter()) {
			GameTimer.CLEARED_MISSION++;
			
			if(GameTimer.CLEARED_MISSION == GameTimer.REQUIRED_MISSION) {
				
				/*
				 * 
				 * 생존자 우승
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
	
	public final boolean isCleared(int code) {
		return cleared.contains(code);
	}
	
	public final boolean isCleared() {
		return cleared.size() >= required_clear;
	}
	
	public final boolean checkPlayer(Event event) {
		if(event instanceof InventoryEvent) {
			boolean bool = gui_title.contains(((InventoryEvent) event).getView().getTitle());
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
