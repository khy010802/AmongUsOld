package bepo.au.function;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Location;

import bepo.au.manager.LocManager;
import bepo.au.utils.Util;

public class Vent {
	
	public final static int CHECK_Y_VALUE = 3;
	public final static int VENT_Y_VALUE = 5;
	
	public static HashMap<String, Vent> vents = new HashMap<String, Vent>();
	
	public static void uploadVent(String name) {
		Vent v = new Vent(name);
		for(Location loc : LocManager.getLoc("Vent_" + name)) {
			//Bukkit.getConsoleSender().sendMessage("name : " + name);
			v.addLoc(loc);
		}
		vents.put(name, v);
	}
	
	public static Vent getVent(String name) {
		return vents.get(name);
	}
	
	public static String check(Location loc) {
		if(CHECK_Y_VALUE < loc.getBlockY()) {
			return null;
		}
		
		Location c = loc.clone();
		c.setY(VENT_Y_VALUE);
		c.setX(c.getBlockX());
		c.setZ(c.getBlockZ());
		
		for(Vent v : vents.values()) {
			if(v.indexOf(c) >= 0) return v.getName();
		}
		
		return null;
	}
	
	public static void closeAll() {
		for(Vent v : vents.values()) {
			for(Location loc : v.locs) {
				Location c = loc.clone();
				c.setY(VENT_Y_VALUE);
				Util.setDoor(c, false);
			}
		}
	}
	
	private List<Location> locs = new ArrayList<Location>();
	private String name;
	public Vent(String name) {
		this.name = name;
		vents.put(name, this);
	}
	
	public void addLoc(Location loc) {
		locs.add(loc);
	}
	
	public String getName() { return this.name; }
	public int indexOf(Location loc) {
		
		for(int i=0;i<locs.size();i++) {
			Location l = locs.get(i);
			if(loc.getBlockX() == l.getBlockX() && loc.getBlockZ() == l.getBlockZ()) {
				return i;
			}
		}
		
		return -1;
		
	}
	public List<Location> getList() { return locs; }

}
