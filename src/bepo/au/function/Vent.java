package bepo.au.function;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Location;

public class Vent {
	
	public final static int CHECK_Y_VALUE = 3;
	public final static int VENT_Y_VALUE = 5;
	
	public static HashMap<String, Vent> vents = new HashMap<String, Vent>();
	
	public static Vent getVent(String name) {
		return vents.get(name);
	}
	
	public static String check(Location loc) {
		if(CHECK_Y_VALUE < loc.getBlockY()) return null;
		
		Location c = loc.clone();
		c.setY(VENT_Y_VALUE);
		c.setX(c.getBlockX());
		c.setZ(c.getBlockZ());
		
		for(Vent v : vents.values()) {
			if(v.indexOf(c) > 0) return v.getName();
		}
		
		return null;
	}
	
	private List<Location> locs = new ArrayList<Location>();
	private String name;
	public Vent(String name) {
		this.name = name;
		vents.put(name, this);
	}
	
	public String getName() { return this.name; }
	public int indexOf(Location loc) { return locs.indexOf(loc); }
	public List<Location> getList() { return locs; }

}
