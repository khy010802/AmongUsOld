package bepo.au.base;

import bepo.au.GameTimer;
import bepo.au.GameTimer.WinReason;
import bepo.au.Main;
import bepo.au.base.Sabotage.SaboType;
import bepo.au.function.MissionList;
import bepo.au.manager.BossBarManager;
import bepo.au.manager.BossBarManager.BossBarList;
import bepo.au.utils.ColorUtil;
import bepo.au.utils.PlayerUtil;
import bepo.au.utils.Util;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public abstract class Mission implements Listener, Cloneable {

	public enum MissionType {

		SABOTAGE, HARD, EASY, COMMON;

	}

	public static List<Mission> MISSIONS = new ArrayList<Mission>();

	public static List<Mission> ActivatedMission = new ArrayList<Mission>();

	protected static Main main;

	public static void initMain(Main m) {
		main = m;
	}

	public static Mission getMission(int id) {
		if (MISSIONS.size() > id)
			return MISSIONS.get(id);
		else
			return null;
	}

	public static Mission getMission(String name) {
		for (Mission m : MISSIONS) {
			if (name.equalsIgnoreCase(m.getMissionName()))
				return m;
		}
		return null;
	}

	public static void deactivateMission() {

		for (Player ap : Bukkit.getOnlinePlayers())
			ap.closeInventory();

		for (Mission m : ActivatedMission) {
			HandlerList.unregisterAll(m);
		}
	}

	protected String playername;

	protected String name;
	protected String korean;
	protected MissionType type;

	protected boolean order;

	protected List<Inventory> gui = new ArrayList<Inventory>();
	protected List<String> gui_title = new ArrayList<String>();
	protected List<Location> locs;
	protected List<Integer> cleared = new ArrayList<Integer>();

	protected int required_clear = 0;

	@Override
	public Object clone() throws CloneNotSupportedException {
		Mission m = (Mission) super.clone();
		m.gui = new ArrayList<Inventory>(gui);
		m.gui_title = new ArrayList<String>(gui_title);
		m.locs = new ArrayList<Location>(locs);
		m.cleared = new ArrayList<Integer>(cleared);
		return m;
	}


	public Mission(boolean order, MissionType mt, String name, String korean, int required_clear, Location... loc) {
		this.name = name;
		this.order = order;
		this.type = mt;
		this.korean = korean;
		this.required_clear = required_clear;
		this.locs = new ArrayList<Location>();
		/*
		if (locs != null) {
			for (int i = 0; i < loc.length; i++)
				if(loc != null && loc[i] != null) locs.add(loc[i]);
		}
		*/
		if (mt == MissionType.EASY)
			MissionList.EASY.add(this);
		else if (mt == MissionType.COMMON)
			MissionList.COMMON.add(this);
		else if(mt == MissionType.HARD)
			MissionList.HARD.add(this);
		else MissionList.SABOTAGE.add((Sabotage) this);
	}

	public Mission(MissionType mt, String name, String korean, int required_clear, Location... loc) {
		this.name = name;
		this.order = false;
		this.type = mt;
		this.korean = korean;
		this.required_clear = required_clear;
		this.locs = new ArrayList<Location>();

		if (mt == MissionType.EASY)
			MissionList.EASY.add(this);
		else if (mt == MissionType.COMMON)
			MissionList.COMMON.add(this);
		else if(mt == MissionType.HARD)
			MissionList.HARD.add(this);
		else MissionList.SABOTAGE.add((Sabotage) this);
	}



	public void addLocation(Location loc) {
		this.locs.add(loc);
	}

	public String getMissionName() {
		return this.name;
	}

	public String getKoreanName() {
		return this.korean;
	}

	public boolean getOrdered() {
		return this.order;
	}

	public List<Location> getLocations() {
		return this.locs;
	}

	public List<String> getTitles() {
		return gui_title;
	}

	public int getRequiredClear() {
		return this.required_clear;
	}

	public String getScoreboardMessage() {
		String s = getKoreanName() + (getRequiredClear() > 1 ? "(" + cleared.size() + "/" + getRequiredClear() + ")" : "");
		if (cleared.size() > 0) {
			if (cleared.size() < required_clear)
				s = "§e" + s;
			else
				s = "§a" + s;
		}
		return s;
	}

	public void shinePosition(boolean order) {
		if (getPlayer() == null || cleared.size() >= required_clear) return;

		for (int i = (order ? cleared.size() : 0); i < (order ? cleared.size()+1 : locs.size()); i++) {
			if (!cleared.contains((Integer) i)) {
				shinePosition(i, order);
			}
		}
	}

	public void shinePosition(int i, boolean order) {

		Location loc = locs.get(i).clone();
		if(this.getMissionName().equalsIgnoreCase("Gas")) {
			loc = loc.getBlock().getLocation().add(0.4D, 0, 0.6D);
		} else loc = loc.getBlock().getLocation().add(0.5D, 0, 0.5D);

		PlayerUtil.spawnGlowingBlock(getPlayer(), loc,
				this instanceof Sabotage ? ColorUtil.RED : (order && i > 0 ? ColorUtil.YELLOW : ColorUtil.WHITE));
	}

	public void shineReset() {
		if(getPlayer() != null) for(Location loc : locs) PlayerUtil.removeGlowingBlock(getPlayer(), loc);
	}

	public  Player getPlayer() {
		if(playername == null) return null;
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

	public void uploadInventory(InventoryHolder owner, int slot, String name) {
		Inventory inv = Bukkit.createInventory(owner, slot, name);
		gui.add(inv);
		gui_title.add(name);
	}

	public void reset() {
		cleared.clear();
		gui = null;
		gui_title = null;
	}

	public  void assign(Player p) {
		ActivatedMission.add(this);
		this.playername = p.getName();
		shinePosition(order);
		Bukkit.getPluginManager().registerEvents(this, Main.getInstance());
	}

	public void generalClear(Player p, int code) {
		Util.debugMessage("cleared " + code);
		PlayerUtil.removeGlowingBlock(p, locs.get(code));
		if(order) {
			if(locs.size() > code+1) shinePosition(code+1, true);
		}

		cleared.add(code);
		p.closeInventory();
		p.sendMessage(Main.PREFIX + "임무 완료!");

		PlayerData pd = PlayerData.getPlayerData(p.getName());

		if(cleared.size() == required_clear) {
			p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 1.2F);
			shineReset();
			if (pd != null && !GameTimer.IMPOSTER.contains(p.getName())) {
				GameTimer.CLEARED_MISSION++;
				pd.cleared_missions++;

				BossBarManager.updateBossBar(BossBarList.TASKS, Sabotage.Sabos != null && Sabotage.Sabos.getType() == SaboType.COMM);


				if (GameTimer.CLEARED_MISSION == GameTimer.REQUIRED_MISSION) {

					GameTimer.WIN_REASON = WinReason.CREW_MISSION;

				}
			}
		} else {
			p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 0.8F);
		}
	}

	public List<Integer> getCode(Location bloc) {
		List<Integer> list = new ArrayList<Integer>();
		bloc = bloc.getBlock().getLocation();
		for (int i = 0; i < locs.size(); i++) {
			if (bloc.equals(locs.get(i))) list.add(i);
		}
		return list;
	}

	public  int getCode(String title) {
		try {
			return Integer.parseInt(title.split(" ")[1]);
		} catch (Exception io) {
			for(int i=0;i<gui_title.size();i++) {
				if(gui_title.get(i).contains(title)) return i;
			}
			return -1;
		}

	}

	public  boolean isCleared(int code) {
		return cleared.contains(code);
	}

	public  boolean isCleared() {
		return cleared.size() >= required_clear;
	}

	public  boolean checkPlayer(Event event) {
		return checkPlayer(event, true);
	}

	public  boolean checkPlayer(Event event, boolean checkName) {
		if (event instanceof InventoryEvent) {
			InventoryEvent ie = (InventoryEvent) event;
			boolean bool = false;
			for (String title : gui_title)
				if (ie.getView().getTitle().contains(title))
					bool = true;
			if (!bool)
				bool = gui_title.contains(ie.getView().getTitle());
			boolean bool2 = false;
			Player p = null;
			if (event instanceof InventoryCloseEvent) {
				if (((InventoryCloseEvent) event).getPlayer() instanceof Player)
					p = (Player) ((InventoryCloseEvent) event).getPlayer();
			} else {
				if (event instanceof InventoryClickEvent) {
					if (((InventoryClickEvent) event).getWhoClicked() instanceof Player)
						p = (Player) ((InventoryClickEvent) event).getWhoClicked();
				}
				if (event instanceof InventoryDragEvent) {
					if (((InventoryDragEvent) event).getWhoClicked() instanceof Player)
						p = (Player) ((InventoryDragEvent) event).getWhoClicked();
				}
			}

			if (p != null) {
				if (playername != null && checkName) {
					bool2 = playername.equalsIgnoreCase(p.getName());
				} else
					bool2 = true;
			}

			if (ie.getInventory().getType() == InventoryType.PLAYER)
				bool2 = false;

			return bool && bool2;
		}
		return false;
	}

	public ItemStack createItem(Material mat, int amount, String name, List<String> lore) {
		ItemStack is = new ItemStack(mat, amount);
		ItemMeta ism = is.getItemMeta();
		ism.setDisplayName(name);
		ism.setLore(lore);
		is.setItemMeta(ism);
		return is;
	}

	public void giveItem(Player p, Material mat, int amount, String name, List<String> lore) {
		p.getInventory().addItem(createItem(mat, amount, name, lore));
		p.updateInventory();
	}

	public void Stack(Inventory inv, int slot, Material mat, int amount, String name, List<String> lore) {
		inv.setItem(slot, createItem(mat, amount, name, lore));
	}

	public abstract void onAssigned(Player p);

	public abstract void onStart(Player p, int i);

	public abstract void onClear(Player p, int i);

	public abstract void onStop(Player p, int i);
	
	/*
	public void onClear_VisualTask() {
		
	}
	*/

}