package bepo.au.manager;


import java.io.File;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;


public class LocManager {
	String[] locList= {
			"FixWiring",
			"DivertPower",
			"EmptyGarbage","EmptyGarbageStorage",
			"Card",
			"ShootingMiddle",
			"ActivatingShield",
			"ActivatingReactor",
			"OpenManifold",
			"GasStorage","GasEngineUpper","GasEngineLower",
			"AlignEngineUpper","AlignEngineLower",
			"EmptyChute",
			"ChartCourse",
			"StablizeSteering",
			"Scanning",
			"InspectSample",
			"DistributePower",
			"DataUpload", "DataDownload",
			"Sabo_FingerprintUpper","Sabo_FingerprintLower",
			"Sabo_FixLights",
			"Sabo_OxygenAdmin","Sabo_OxygenOxy",
			"Sabo_Communication",
			};
	
	public String locationCommand ="locate";
	private Map<String, ArrayList<Location>> LocationMap = new HashMap<String,ArrayList<Location>>();
	private FileConfiguration location;
	private File file = new File("plugins/AmongUs/locations.yml");
	
	public String[] getList() {return locList;};
	
	public void loadLocs() { //로케이션 불러오기
		loadLocations();
	}
	public void inputLoc(String locName, World w, int x, int y, int z) { //로케이션 맵에 넣기
		Location loc = new Location(w,x,y,z);
		inputALocation(locName, loc);
	}
	public void inputLocation(String locName, Location location) {
		inputALocation(locName, location);
	}
	public void saveALoc(String locName) { //
		saveALocation(locName);
	}
	public void saveLocs() {
		saveLocations();
	}
	public ArrayList<Location> getLoc(String locName) {
		return LocationMap.get(locName);
	}

	
	///////////////////
	private void loadLocations() {
		location = YamlConfiguration.loadConfiguration(file);
	    try {
	        if (!file.exists()) {
	        	for (String locName : locList) location.set(locName,"0,0,0");
	        	location.save(file);
	        }
	        location.load(file);
	        for (String locName : locList) {
		        for (String coor : location.getString(locName).split("/")) {
		        	if (coor==null) break;
		        	if(LocationMap.get(locName)==null) {
		    			LocationMap.put(locName, new ArrayList<Location>() );
		    		}
		        	LocationMap.get(locName).add(StringToLoc(coor));
		        }
	        }
	    } catch (Exception localException) {
			localException.printStackTrace();
		}
	}


	private void saveLocations() {
		location = YamlConfiguration.loadConfiguration(file);
		try {
			 for( String key : LocationMap.keySet() ){
				 String value = "";
				for(Location loc: LocationMap.get(key)) {
					value=value+LocationToCoor(loc)+"/";
				}
				location.set(key, value);
				}
			location.save(file);
		} catch (Exception localException) {
			localException.printStackTrace();
		}
	}
	private void inputALocation(String locName,Location loc) {
		if(LocationMap.get(locName)==null) {
			LocationMap.put(locName, new ArrayList<Location>() );
		}
		LocationMap.get(locName).add(loc);
	}
	private void saveALocation(String locName) {
		location = YamlConfiguration.loadConfiguration(file);
		try { 
			 String value = "";
			for(Location loc: LocationMap.get(locName)) {
				value=value+LocationToCoor(loc)+"/";
			}
			location.set(locName, value);
			location.save(file);
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
	
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		Player player = (Player) sender;
		Block targ = player.getTargetBlock((Set<Material>) null, 5);
		if (command.getName().equals("test")) {
			switch (args.length) {
			case 3:
				if (args[0].equalsIgnoreCase("locate") && (args[1].equalsIgnoreCase("i")||args[1].equalsIgnoreCase("r"))) return Arrays.asList(getList());
			case 4:
				if (args[0].equalsIgnoreCase("locate") && args[1].equalsIgnoreCase("i")) return Collections.singletonList(targ.getX() + "");
			case 5:
				if (args[0].equalsIgnoreCase("locate") && args[1].equalsIgnoreCase("i")) return Collections.singletonList(targ.getY() + "");
			case 6:
				if (args[0].equalsIgnoreCase("locate") && args[1].equalsIgnoreCase("i")) return Collections.singletonList(targ.getZ() + "");
			}
		}
		return null;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/*
	public void createNewLocfile() {
		String defaultFile;
		defaultFile="";
		for (String str:locList) {
			defaultFile=defaultFile+"\n"+str+" :";
		}
		File filename= new File("plugins/amongus/Locations.txt");	
		File folder_Location1 = new File("plugins/amongus");
		try {
			if(!filename.exists()) {
				folder_Location1.mkdir();
				filename.createNewFile();
			}
			BufferedWriter w = new BufferedWriter(new FileWriter(filename));
			w.append(defaultFile);
			w.flush();
			w.close();
		}
		catch (IOException localIoException) {
			
		}
	}
	public void saveLocfile() {
		String tempFile;
		tempFile="";
		for (String str:locList) {
			tempFile=tempFile+"\n"+str+" : ("+1+")";
		}
		File filename= new File("plugins/amongus/Locations.txt");	
		File folder_Location1 = new File("plugins/amongus");
		try {
			if(!filename.exists()) {
				folder_Location1.mkdir();
				filename.createNewFile();
			}
			BufferedWriter w = new BufferedWriter(new FileWriter(filename));
			w.append(tempFile);
			w.flush();
			w.close();
		}
		catch (IOException localIoException) {
			 System.out.println(localIoException);
		}
	}
	public void readLocfile() {
		
			File filename= new File("plugins/amongus/Locations.txt");	
			File folder_Location1 = new File("plugins/amongus");
			try {
				if(!filename.exists()) {
					folder_Location1.mkdir();
					filename.createNewFile();
				}
            BufferedReader bufReader = new BufferedReader(new FileReader(filename));
            String line = "";
            while((line = bufReader.readLine()) != null){
            	String coor = line.split(":")[1].replace('(', ' ').replace(')', ' ');
            	Location loc=StringToLoc(coor);
                LocationMap.put(line.split(":")[0], loc);
            }
            //.readLine()은 끝에 개행문자를 읽지 않는다.            
            bufReader.close();
        }catch (FileNotFoundException e) {
            Util.debugMessage("파일을 찾을 수 없습니다");
        }catch(IOException e){
            System.out.println(e);
        }
	}
	*/
	
}

