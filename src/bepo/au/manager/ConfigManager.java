package bepo.au.manager;

import java.io.File;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import bepo.au.Main;


public class ConfigManager {
	
	private Main main;
	
	public ConfigManager(Main main) {
		this.main = main;
	}
	
	public void loadConfig() {
		
		File file = new File(main.getDataFolder(), "config.yml");
		
		if(!file.exists()) createConfig(file);
		FileConfiguration config = YamlConfiguration.loadConfiguration(file);
		
		Main.EMER_BUTTON_PER_PLAYER = config.getInt("긴급 회의");
		Main.EMER_BUTTON_COOL_SEC = config.getInt("긴급 회의 쿨타임(초)");
		Main.DISCUSS_SEC = config.getInt("회의 제한 시간(초)");
		Main.VOTE_SEC = config.getInt("투표 제한 시간(초)");
		Main.MOVEMENT_SPEED = (float) config.getDouble("이동 속도");
		Main.CREW_SIGHT_BLOCK = config.getInt("크루원 시야(블럭)");
		Main.IMPOSTER_SIGHT_BLOCK = config.getInt("임포스터 시야(블럭)");
		Main.KILL_COOLTIME_SEC = config.getInt("킬 쿨타임(초)");
		Main.COMMON_MISSION_AMOUNT = config.getInt("공통 임무");
		Main.EASY_MISSION_AMOUNT = config.getInt("간단한 임무");
		Main.HARD_MISSION_AMOUNT = config.getInt("복잡한 임무");

		Main.WORLD_NAME = config.getString("* 월드");
		
	}
	
	public void createConfig(File file) {
		FileConfiguration config = YamlConfiguration.loadConfiguration(file);
		
		config.options().header("* 이 붙어있는 설정값은 별다른 일이 없는 한 수정하지 말아주세요.");
		config.set("긴급 회의", 1);
		config.set("긴급 회의 쿨타임(초)", 15);
		config.set("회의 제한 시간(초)", 15);
		config.set("투표 제한 시간(초)", 120);
		config.set("이동 속도", 1.0D);
		config.set("크루원 시야(블럭)", 16);
		config.set("임포스터 시야(블럭)", 24);
		config.set("킬 쿨타임(초)", 45);
		config.set("공통 임무", 1);
		config.set("복잡한 임무", 1);
		config.set("간단한 임무", 2);
		
		config.set("* 월드", "world");
		
	}

}
