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
		
		Main.EMER_BUTTON_PER_PLAYER = config.getInt("��� ȸ��");
		Main.EMER_BUTTON_COOL_SEC = config.getInt("��� ȸ�� ��Ÿ��(��)");
		Main.DISCUSS_SEC = config.getInt("ȸ�� ���� �ð�(��)");
		Main.VOTE_SEC = config.getInt("��ǥ ���� �ð�(��)");
		Main.MOVEMENT_SPEED = (float) config.getDouble("�̵� �ӵ�");
		Main.CREW_SIGHT_BLOCK = config.getInt("ũ��� �þ�(��)");
		Main.IMPOSTER_SIGHT_BLOCK = config.getInt("�������� �þ�(��)");
		Main.KILL_COOLTIME_SEC = config.getInt("ų ��Ÿ��(��)");
		Main.COMMON_MISSION_AMOUNT = config.getInt("���� �ӹ�");
		Main.EASY_MISSION_AMOUNT = config.getInt("������ �ӹ�");
		Main.HARD_MISSION_AMOUNT = config.getInt("������ �ӹ�");

		Main.WORLD_NAME = config.getString("* ����");
		
	}
	
	public void createConfig(File file) {
		FileConfiguration config = YamlConfiguration.loadConfiguration(file);
		
		config.options().header("* �� �پ��ִ� �������� ���ٸ� ���� ���� �� �������� �����ּ���.");
		config.set("��� ȸ��", 1);
		config.set("��� ȸ�� ��Ÿ��(��)", 15);
		config.set("ȸ�� ���� �ð�(��)", 15);
		config.set("��ǥ ���� �ð�(��)", 120);
		config.set("�̵� �ӵ�", 1.0D);
		config.set("ũ��� �þ�(��)", 16);
		config.set("�������� �þ�(��)", 24);
		config.set("ų ��Ÿ��(��)", 45);
		config.set("���� �ӹ�", 1);
		config.set("������ �ӹ�", 1);
		config.set("������ �ӹ�", 2);
		
		config.set("* ����", "world");
		
	}

}
