package bepo.au.function;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;


public class Vent {
	
	private HashMap<String, Location> ventLocations = new HashMap<String, Location>();
	private HashMap<String, ArrayList<String>> ventDestinations = new HashMap<String,ArrayList<String>>();
	
	private FileConfiguration vents;
	private File file = new File("plugins/AmongUs/vents.yml");
	
	public HashMap<String, ArrayList<String>> getVentDestinations() {
		return ventDestinations;
	}
	public HashMap<String, Location> getVentLocations() {
		return ventLocations;
	}
	public void addVentXYZ(String name,World w,int x,int y,int z) {
		 ventLocations.put(name, new Location(w,x,y,z));
	}
	public void addVentLocation(String name,Location location) {
		 ventLocations.put(name, location);
	}
	public Location getVentLocation(String name) {
		if (ventLocations.containsKey(name)) return ventLocations.get(name);
		else return null;
	}
	
	public void addDestination(String name, String destination) {
		if (!ventDestinations.containsKey(name)) ventDestinations.put(name, new ArrayList<String>());
		ventDestinations.get(name).add(destination);
	}
	
	public ArrayList<String> getVentDestination(String name){
		if (ventDestinations.containsKey(name)) return ventDestinations.get(name);
		else return null;
	}
	public void loadVents() {
		loadLocations();
	}
	public void saveVents() {
		saveLocations();
	}
	/////////////////////////////////////////////////////////////////
	private void loadLocations() {
		vents = YamlConfiguration.loadConfiguration(file);
	    try {
	        if (!file.exists()) {
	        	vents.set("default","0,0,0;");
	        	vents.save(file);
	        }
	        vents.load(file);
	    
	        for(String name : vents.getKeys(false)) {
	        	addVentLocation(name, StringToLoc(vents.getString(name).split(";")[0])); //장소 추가
	        	for (String destination : vents.getString(name).split(";")[1].split(",")){
	        	if (destination==null) break;
	        	addDestination(name, destination);
	        	}
	        }
	    } catch (Exception localException) {
			localException.printStackTrace();
	    }
		
	}
	
	private void saveLocations() {
		vents = YamlConfiguration.loadConfiguration(file);
		try {
			for(String name : ventLocations.keySet()) {
				String destinations = "";
				for (String temp : getVentDestination(name)) {
				destinations+= temp+",";
				}
				String value = LocationToCoor(getVentLocation(name))+";"+destinations;
				vents.set(name,value);
				
			}
			vents.save(file);
		} catch (Exception localException) {
			localException.printStackTrace();
		}
	}
	
	private String LocationToCoor(Location loc) {
		String coor = loc.getX()+","+loc.getY()+","+loc.getZ();
		return coor;
	}
	private Location StringToLoc(String str) {
		int x,y,z;
		String[] xyz=str.split(",");
		x=Integer.parseInt(xyz[0]);
		y=Integer.parseInt(xyz[1]);
		z=Integer.parseInt(xyz[2]);
		return new Location(Bukkit.getServer().getWorld("world"),x,y,z);
	}
}
