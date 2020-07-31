package bepo.au;

import java.io.File;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;


public class ConfigManager {
	
	private Main main;
	
	public ConfigManager(Main main) {
		this.main = main;
	}
	
	public void loadConfig() {
		
		File file = new File(main.getDataFolder(), "config.yml");
		
		if(!file.exists()) createConfig(file);
		FileConfiguration config = YamlConfiguration.loadConfiguration(file);
		
		Main.SIGHT_BLOCK = config.getInt("시야(블럭)");
		Main.KILL_COOLTIME_SEC = config.getInt("킬 쿨타임(초)");
		Main.MISSION_AMOUNT = config.getInt("배분 미션 개수");
		Main.MISSION_DIFFICULTY = config.getInt("미션 난이도(1~10)");
		Main.SABOTAGE_DIFFICULTY = config.getInt("살인마 방해공작 난이도(1~10)");
		Main.IMPOSTER_AMOUNT = config.getInt("임포스터 수");
		Main.EMER_BUTTON_COOL_SEC = config.getInt("긴급회의 버튼 쿨타임(초)");
		
		Main.WORLD_NAME = config.getString("* 월드");
		
	}
	
	public void createConfig(File file) {
		FileConfiguration config = YamlConfiguration.loadConfiguration(file);
		
		config.options().header("* 이 붙어있는 설정값은 별다른 일이 없는 한 수정하지 말아주세요.");
		config.set("시야(블럭)", 16);
		config.set("킬 쿨타임(초)", 20);
		config.set("배분 미션 수", 5);
		config.set("미션 난이도(1~10)", 5);
		config.set("살인마 방해공작 난이도(1~10)", 5);
		config.set("임포스터 수", 3);
		config.set("긴급회의 버튼 쿨타임(초)", 100);
		
		config.set("* 월드", "world");
		
	}

}
