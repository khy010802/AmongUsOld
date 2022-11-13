package bepo.au.manager;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import bepo.au.GameTimer.GameType;
import bepo.au.Main;
import bepo.au.Main.SETTING;


public class ConfigManager {
	
	private Main main;
	
	public ConfigManager(Main main) {
		this.main = main;
	}
	
	public void loadConfig() {
		
		File file = new File(main.getDataFolder(), "config.yml");
		
		if(!file.exists()) createConfig(file);
		FileConfiguration config = YamlConfiguration.loadConfiguration(file);
		
		for(SETTING set : SETTING.values()) {
			if(!config.contains(set.getName())) continue;
			Object obj = config.get(set.getName());
			if(set == SETTING.GAMEMODE) obj = GameType.valueOf(config.getString(set.getName()));
			set.setSetting(obj);
		}
	}
	
	public void saveConfig() {
		File file = new File(main.getDataFolder(), "config.yml");
		FileConfiguration config = YamlConfiguration.loadConfiguration(file);
		
		for(SETTING set : SETTING.values()) {
			if(set == SETTING.GAMEMODE) config.set(set.getName(), set.getAsGameType().toString());
			else config.set(set.getName(), set.get());
		}
		
		try {
			config.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void createConfig(File file) {
		FileConfiguration config = YamlConfiguration.loadConfiguration(file);
		
		for(SETTING set : SETTING.values()) {
			if(set == SETTING.GAMEMODE) config.set(set.getName(), set.getAsGameType().toString());
			else config.set(set.getName(), set.get());
		}
		
		try {
			config.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

}
