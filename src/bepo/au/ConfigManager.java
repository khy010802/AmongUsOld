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
		
		Main.SIGHT_BLOCK = config.getInt("�þ�(��)");
		Main.KILL_COOLTIME_SEC = config.getInt("ų ��Ÿ��(��)");
		Main.MISSION_AMOUNT = config.getInt("��� �̼� ����");
		Main.MISSION_DIFFICULTY = config.getInt("�̼� ���̵�(1~10)");
		Main.SABOTAGE_DIFFICULTY = config.getInt("���θ� ���ذ��� ���̵�(1~10)");
		Main.IMPOSTER_AMOUNT = config.getInt("�������� ��");
		Main.EMER_BUTTON_COOL_SEC = config.getInt("���ȸ�� ��ư ��Ÿ��(��)");
		
		Main.WORLD_NAME = config.getString("* ����");
		
	}
	
	public void createConfig(File file) {
		FileConfiguration config = YamlConfiguration.loadConfiguration(file);
		
		config.options().header("* �� �پ��ִ� �������� ���ٸ� ���� ���� �� �������� �����ּ���.");
		config.set("�þ�(��)", 16);
		config.set("ų ��Ÿ��(��)", 20);
		config.set("��� �̼� ��", 5);
		config.set("�̼� ���̵�(1~10)", 5);
		config.set("���θ� ���ذ��� ���̵�(1~10)", 5);
		config.set("�������� ��", 3);
		config.set("���ȸ�� ��ư ��Ÿ��(��)", 100);
		
		config.set("* ����", "world");
		
	}

}
