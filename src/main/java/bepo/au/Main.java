package bepo.au;

import bepo.au.GameTimer.GameType;
import bepo.au.base.Mission;
import bepo.au.function.MissionList;
import bepo.au.function.SoundRemover;
import bepo.au.function.Vent;
import bepo.au.games.ChaseTag;
import bepo.au.games.Normal;
import bepo.au.manager.*;
import bepo.au.utils.ColorUtil;
import bepo.au.utils.PlayerUtil;
import bepo.au.utils.SettingUtil;
import bepo.au.utils.SettingUtil.ARMORSTANDS;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Scoreboard;

import java.util.ArrayList;
import java.util.List;

public class Main extends JavaPlugin implements Listener{
	
	public enum SETTING {
		
		GAMEMODE("���� ����", GameTimer.GameType.NORMAL, GameTimer.GameType.class),
		EMER_BUTTON_PER_PLAYER("�÷��̾� �� ��� ȸ�� ����", 1, Integer.class),
		EMER_BUTTON_COOL_SEC("��� ȸ�� ��Ÿ��(��)", 15, Integer.class),
		VOTE_MAIN_SEC("��ǥ ���� �ð�(��)", 150, Integer.class),
		VOTE_PREPARE_SEC("ȸ�� ���� �ð�(��)",10, Integer.class),
		CREW_SIGHT_BLOCK("ũ��� �þ�(��)", 20, Integer.class),
		IMPOSTER_SIGHT_BLOCK("�������� �þ�(��)",30, Integer.class),
		KILL_COOLTIME_SEC("ų ��Ÿ��(��)",45, Integer.class),
		SABO_COOL_SEC("���ذ��� ��Ÿ��",30, Integer.class),
		SABO_CRIT_DURA_SEC("ġ���� ���ذ��� ���ӽð�",40, Integer.class),
		NOTICE_IMPOSTER("�߹� �� �������� ���� ����",true, Boolean.class),
		VISUAL_TASK("�ð� �̼� ���̱�", true, Boolean.class),
		BLOCK_PLAYER_SOUND("�÷��̾� �Ҹ� ����", true, Boolean.class),
		COMMON_MISSION_AMOUNT("���� �ӹ�", 1, Integer.class),
		EASY_MISSION_AMOUNT("������ �ӹ�", 2, Integer.class),
		HARD_MISSION_AMOUNT("����� �ӹ�", 1, Integer.class),
		IMPOSTER_AMOUNT("�������� ��", 1, Integer.class),
		MOVEMENT_SPEED("�̵��ӵ�", 0.2D, Double.class),
		
		GENERATE_CORPSE("��� �� ��ü ����", true, Boolean.class),
		ENABLE_CORPSE_REPORT("��ü ����Ʈ Ȱ��ȭ", true, Boolean.class),
		
		IMPOSTER_ALWAYS_BLIND("(�������) �������� �Ǹ� �ο�", true, Boolean.class),
		IMPOSTER_MOVEMENT_SPEED("(�������) �������� �̵��ӵ�", 0.2D, Double.class);
		
		private Object obj;
		private String name;
		private Class<?> type;
		public static ArrayList<String> SETTING_LIST = new ArrayList<String>();
		
		SETTING(String name, Object obj, Class<?> type) {
			this.obj = obj.toString();
			this.type = type;
			this.name = name;
		}
		
		public String getName() {
			return this.name;
		}
		
		public Object get() {
			return obj;
		}
		
		public Class<?> getType(){
			return this.type;
		}
		/*
		public void set(String s) {
			
		}
		*/
		public boolean getAsBoolean() {
			return (boolean) obj;
		}
		
		public double getAsDouble() {
			return (double) obj;
		}
		
		public int getAsInteger() {
			return (int) obj;
		}
		
		public GameTimer.GameType getAsGameType(){
			return (GameType) obj;
		}
		
		public void setSetting(Object obj) {
			this.obj = obj;
			checkLimit();
		}
		
		private void checkLimit() {
			if(this == IMPOSTER_MOVEMENT_SPEED) {
		
				
				if(obj instanceof Double) {
					Double d = (Double) obj;
					if(d > 1.0D) obj = 1.0D; else if(d < -1.0D) obj = -1.0D;
				}
			}
		}
		
		static{
			for (SETTING setting : SETTING.values()) {
					SETTING_LIST.add(setting.toString());
				}
		}
	
	}
	
	public static String PREFIX = "��a[AmongUs] ";
	
	public static GameTimer gt = null;
	private static Main main;
	public CommandManager cm;
	public ConfigManager config;
	private static GameEventManager gem;
	private static MissionList ml;
	private static LocManager lm;
	private static EventManager em;
	
	public static boolean isProtocolHooked = false;

	
	public static World w;
	//public static Team team;

	
	public static Main getInstance() {
		return main;
	}
	
	public static GameEventManager getEventManager() {
		return gem;
	}
	
	public static LocManager getLocManager() {
		return lm;
	}
	
	public static MissionList getMissionList() {
		return ml;
	}
	
	public void onEnable() {
		main = this;
		
		String bv =Bukkit.getBukkitVersion();
        String v = Bukkit.getVersion();
        
        
        Bukkit.getConsoleSender().sendMessage(PREFIX + "��f���� Ȯ�� ��...");
        if(!bv.contains("1.16.5")) {
        	Bukkit.getConsoleSender().sendMessage(PREFIX + "��c����. �ش� �÷������� 1.16.5���� ����Ǿ����ϴ�. ��ɿ� �̻��� ���� �� �ֽ��ϴ�.");
        }
        if(!v.contains("Paper")) {
        	Bukkit.getConsoleSender().sendMessage(PREFIX + "��4����. Paper ��Ŷ�� �ƴմϴ�. �÷������� �۵����� �ʽ��ϴ�.");
        	Bukkit.getPluginManager().disablePlugin(this);
        	return;
        }
        Bukkit.getConsoleSender().sendMessage(PREFIX + "��f���� Ȯ�� �Ϸ�! �÷����� Ȱ��ȭ�� �����մϴ�.");

		Mission.MISSIONS.addAll(MissionList.EASY);

		Mission.MISSIONS.addAll(MissionList.HARD);

		Mission.MISSIONS.addAll(MissionList.COMMON);

		Mission.MISSIONS.addAll(MissionList.SABOTAGE);
		
		config = new ConfigManager(this);
		config.loadConfig();

		cm = new CommandManager();
		gem = new GameEventManager();
		em = new EventManager();
		ml = new MissionList();
		lm = new LocManager();
		lm.loadLocs();
		
		Vent.uploadVent("NW");
		Vent.uploadVent("RL");
		Vent.uploadVent("RU");
		Vent.uploadVent("NS");
		Vent.uploadVent("EES");
		Vent.uploadVent("CAH");
		
		//MiniMap.renderMaps();
		
		for(Mission m : Mission.MISSIONS) addLocation(m);
		
		/*
		Scoreboard board = Bukkit.getScoreboardManager().getMainScoreboard();
		Team t = board.getTeam("au_players");
		if(t == null) {
			t = board.registerNewTeam("au_players");
			t.setOption(Option.NAME_TAG_VISIBILITY, OptionStatus.FOR_OTHER_TEAMS);
			t.setOption(Option.COLLISION_RULE, OptionStatus.NEVER);
		}
		Main.team = t;
		*/
		getCommand("au").setExecutor(cm);
		getCommand("au").setTabCompleter(new TabCompleteManager());
		
		Bukkit.getPluginManager().registerEvents(em, this);
		
		GameType.NORMAL.setGameTicker(new Normal());
		GameType.CHASETAG.setGameTicker(new ChaseTag());
		
		if(Bukkit.getPluginManager().getPlugin("ProtocolLib") != null) {
			isProtocolHooked = true;
			SoundRemover.addListener();
		}
		
		for(World w : Bukkit.getWorlds()) {
			for(Entity e : w.getEntities()) {
				if(e.getScoreboardTags().contains("au_reset")) {
					e.remove();
				} else {
					if(e instanceof ArmorStand) {
						for(ARMORSTANDS ass : ARMORSTANDS.values()) {
							if(e.getScoreboardTags().contains(ass.getTag())) {
								Bukkit.getConsoleSender().sendMessage("added " + ass.getTag());
								ass.addArmorStands((ArmorStand) e);
							}
						}
					}
				}
			}
		}
		
		Mission.initMain(this);
		
		new BukkitRunnable() {
			public void run() {
				SettingUtil.startSetting();
				//SettingUtil.logo_Frames();
			}
		}.runTaskLater(this, 20L);
		
		Bukkit.getConsoleSender().sendMessage(PREFIX + "��fAmongUs Ȱ��ȭ. By Team JonJAr");
	}
	
	private void addLocation(Mission m) {
		
		String s = m.getMissionName();
		
		List<Location> list = LocManager.getLoc(s);
		//Bukkit.getConsoleSender().sendMessage(m.getKoreanName() + " list size : " + list.size());
		if(list != null && list.size() > 0) {
			for(int i=0;i<list.size();i++) m.addLocation(list.get(i));
		}
	}
	
	public void onDisable() {
		lm.saveLocs();
		config.saveConfig();
		for(Player p : Bukkit.getOnlinePlayers()) {
			PlayerUtil.resetHidden(p);
			PlayerUtil.resetGlowingBlock(p);
			PlayerUtil.removeChair(p);
		}
		Scoreboard board = Bukkit.getScoreboardManager().getMainScoreboard();
		for(ColorUtil c : ColorUtil.values()) {
			String tname = "sh" + c.getChatColor().name();
			if(board.getTeam(tname) != null) board.getTeam(tname).unregister();
		}
		
		if(gt != null) {
			gt.stop();
		} else {
			GameTimer.module_reset();
		}
		
		
	}

}
