package bepo.au.manager;


import java.io.File;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;


public class LocManager {
	String[] locList= {
			"SEATS",
			"EmergencyButton",
			"AdminMap",
			"ImposterNotice",
			"VoteNotice",
			"VoteNoticeArmorStand",
			"VoteNoticeArmorStand_DIR",
			"ImposterNoticeArmorStand",
			"Desk", "Desk_ArmorStand",
			"FixWiring",
			"DivertPower",
			"EmptyGarbage",
			"Card",
			"Shooting",
			"ActivatingShield",
			"ActivatingReactor",
			"OpenManifold",
			"Gas",
			"AlignEngine",
			"EmptyChute",
			"ChartCourse",
			"StablizeSteering",
			"Scanning",
			"InspectSample",
			"DistributePower",
			"Data",
			"Fingerprint",
			"FixLights",
			"Oxygen",
			"Communication",
			
			"Door_UpperEngine",
			"Door_LowerEngine",
			"Door_Security",
			"Door_MedBay",
			"Door_Electrical",
			"Door_Storage",
			"Door_Cafeteria",
			
			"Vent_EES",
			"Vent_RL",
			"Vent_RU",
			"Vent_CAH",
			"Vent_NW",
			"Vent_NS"
			};
	
	public String locationCommand ="locate";
	private static Map<String, ArrayList<Location>> LocationMap = new HashMap<String,ArrayList<Location>>();
	private FileConfiguration location;
	private File file = new File("plugins/AmongUs/locations.yml");
	
	public String[] getList() {return locList;};
	
	public void loadLocs() { //�����̼� �ҷ�����
		loadLocations();
	}
	public void inputLoc(String locName, World w, int x, int y, int z) { //�����̼� �ʿ� �ֱ�
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
	public static ArrayList<Location> getLoc(String locName) {
		return LocationMap.get(locName);
	}

	
	///////////////////
	private void loadLocations() {
		LocationMap.clear();
		
		location = YamlConfiguration.loadConfiguration(file);
	    try {
	        if (!file.exists()) {
	        	location.save(file);
	        }
	        location.load(file);
	        for (String locName : locList) {
	        	if(!location.contains(locName)) continue;
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
					value=value+LocationToCoor(loc, getYawPitch(key))+"/";
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
	
	private boolean getYawPitch(String key) {
		 return key.equalsIgnoreCase("SEATS") || key.contains("Imposter") || key.contains("Desk") || key.contains("Vent") || key.contains("Vote");
	}
	private void saveALocation(String locName) {
		location = YamlConfiguration.loadConfiguration(file);
		try { 
			 String value = "";
			for(Location loc: LocationMap.get(locName)) {
				value=value+LocationToCoor(loc, getYawPitch(locName))+"/";
			}
			location.set(locName, value);
			location.save(file);
		} catch (Exception localException) {
			localException.printStackTrace();
		}
	}
	
	private String LocationToCoor(Location loc, boolean yawpitch) {
		String coor = (yawpitch ? loc.getX() : loc.getBlockX())+","+ (yawpitch ? loc.getY() : loc.getBlockY())+","+ (yawpitch ? loc.getZ() : loc.getBlockZ()) + (yawpitch ? "," + loc.getYaw() +"," + loc.getPitch() : "");
		return coor;
	}
	private Location StringToLoc(String str) {
		double x,y,z;
		float yaw = 0F, pitch = 0F;
		String[] xyz=str.split(",");
		x= Double.parseDouble(xyz[0]);
		y= Double.parseDouble(xyz[1]);
		z= Double.parseDouble(xyz[2]);
		if(xyz.length > 3) {
			yaw = Float.parseFloat(xyz[3]);
			pitch = Float.parseFloat(xyz[4]);
		}
		
		return new Location(Bukkit.getServer().getWorld("world"),x,y,z,yaw,pitch);
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
            //.readLine()�� ���� ���๮�ڸ� ���� �ʴ´�.            
            bufReader.close();
        }catch (FileNotFoundException e) {
            Util.debugMessage("������ ã�� �� �����ϴ�");
        }catch(IOException e){
            System.out.println(e);
        }
	}
	*/
	
}

