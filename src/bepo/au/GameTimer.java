package bepo.au;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.scheduler.BukkitRunnable;

import bepo.au.base.Mission;
import bepo.au.base.PlayerData;

public class GameTimer extends BukkitRunnable{
	
	public enum Status {
		READY,
		GAME_SETTING,
		WORKING,
		VOTING,
		END;
	}
	
	public Color[] COLORLIST 			= { Color.AQUA, Color.BLUE, Color.GRAY, Color.GREEN, Color.ORANGE, Color.PURPLE, Color.RED, Color.WHITE, Color.YELLOW, Color.SILVER, Color.NAVY, Color.TEAL };
	public ChatColor[] CHATCOLORLIST 	= { ChatColor.AQUA, ChatColor.BLUE, ChatColor.DARK_GRAY, ChatColor.GOLD, ChatColor.DARK_PURPLE, ChatColor.WHITE, ChatColor.YELLOW, ChatColor.GRAY, ChatColor.BLUE };
	public List<Color> COLORS = new ArrayList<Color>();
	public List<ChatColor> CHATCOLORS = new ArrayList<ChatColor>();
	
	private int timer = 0;
	
	private Main main;
	
	private Status status = Status.READY;
	private boolean pause = false;
	
	public static List<String> PLAYERS = new ArrayList<String>();
	public static int IMPOSETER_LEFT = 0;
	
	public static int REQUIRED_MISSION = 0;
	public static int CLEARED_MISSION = 0;
	
	public GameTimer(Main main) {
		this.main = main;
	}
	
	public Status getStatus() { return this.status; }
	
	public void start(Player p) {
		reset();
		Bukkit.broadcastMessage(Main.PREFIX + "��e" + p.getName() + "��f�Բ��� ������ �����ϼ̽��ϴ�!");
		status = Status.GAME_SETTING;
		this.runTaskTimer(main, 0L, 1L);
	}
	
	public void stop() {
		if(!this.isCancelled()) this.cancel();
		
		Mission.deactivateMission();
	}
	
	private void setting() {
		for(Player ap : Bukkit.getOnlinePlayers()) {
			if(!Main.OBSERVER.contains(ap.getName().toLowerCase())) {
				PLAYERS.add(ap.getName());
				ap.getInventory().clear();
				ap.setExp(0F);
				ap.setLevel(0);
			}
		}

		REQUIRED_MISSION = (Main.COMMON_MISSION_AMOUNT + Main.EASY_MISSION_AMOUNT + Main.HARD_MISSION_AMOUNT) * (PLAYERS.size() - Main.IMPOSTER_AMOUNT);
	}
	
	private void reset() {
		PLAYERS.clear();
		CLEARED_MISSION = 0;
		Main.gt = null;
	}
	
	private void team_split() {
		String imposter = "";
		
		long seed = System.currentTimeMillis();
		Random rn = new Random(seed);
		
		Collections.shuffle(PLAYERS);
		Collections.shuffle(COLORS, rn);
		Collections.shuffle(CHATCOLORS, rn);
		
		COLORS = new ArrayList<Color>(Arrays.asList(COLORLIST));
		CHATCOLORS = new ArrayList<ChatColor>(Arrays.asList(CHATCOLORLIST));
		
		for(int i=0;i<PLAYERS.size();i++) {
			String name = PLAYERS.get(i);
			PlayerData pd = new PlayerData(name);
			if(i < Main.IMPOSTER_AMOUNT) {
				pd.setImposter();
				imposter = imposter + name + " ";
			}
			pd.setColor(COLORS.get(i));
			pd.setChatColor(CHATCOLORS.get(i));
		}
		GameTimer.IMPOSETER_LEFT = Main.IMPOSTER_AMOUNT;
		give_item(imposter);
	}
	
	private void give_item(String imposter) {
		
		for(String name : PLAYERS) {
			if(Bukkit.getPlayer(name) == null) return;
			Player p = Bukkit.getPlayer(name);
			PlayerData pd = PlayerData.getPlayerData(p.getName());
			
			p.setPlayerListName(pd.getChatColor() + "��o" + p.getName());
			
			String[] parts = { "BOOTS", "LEGGINGS", "CHESTPLATE", "HELMET",  };
			ItemStack[] ac = new ItemStack[4];
			
			for(int temp=0;temp<4;temp++) {
				ItemStack is = new ItemStack(Material.getMaterial("LEATHER_" + parts[temp]));
				LeatherArmorMeta lam = (LeatherArmorMeta) is.getItemMeta();
				lam.setColor(pd.getColor());
				lam.setUnbreakable(true);
				is.setItemMeta(lam);
				ac[temp] = is;
			}
			p.getInventory().setArmorContents(ac);
			
			if(pd.isImposter()) {
				p.sendTitle("��4��l��������", "��c��� ũ����� ���̽ʽÿ�", 10, 100, 10);
				p.sendMessage("��f=======================");
				p.sendMessage("��4����� ���������Դϴ�.");
				p.sendMessage("��c�κ��丮�� ���� �����ص�Ƚ��ϴ�. ������ �ñ⿡ ũ����� ���̽ʽÿ�. (��Ÿ�� " + Main.KILL_COOLTIME_SEC + "��)");
				p.sendMessage("��c�ϰ� ���൵�� 100%�� �����ϱ� �� �������Ϳ� ũ����� ���� �������ų�,");
				p.sendMessage("��c���/���ڷ� �纸Ÿ���� �������� ��� ���������� �¸��Դϴ�.");
				p.sendMessage("��c");
				p.sendMessage("��c�������� �÷��̾� : ��f" + imposter);
				p.sendMessage("��f=======================");
			} else {
				p.sendTitle("��f��lũ���", "��7��� �������͸� �߹��Ͻʽÿ�", 10, 100, 10);
				p.sendMessage("��f=======================");
				p.sendMessage("��f����� ũ����Դϴ�.");
				p.sendMessage("��7�������� �ϰ��� �����ϰ�, �ϰ� ���൵�� 100%���� ä��ʽÿ�.");
				p.sendMessage("��7��� �������Ͱ� ����ϰų� �ϰ� ���൵�� 100%�� �����ϸ� ũ����� �¸��Դϴ�.");
				p.sendMessage("��7");
				p.sendMessage("��f�ϰ��� ���� ���ھ�忡 ǥ��Ǹ�, ���൵�� ����ġ �ٿ� ǥ��˴ϴ�.");
				p.sendMessage("��f=======================");
			}
			random_mission(p);
		}
		
		
	}
	
	private void random_mission(Player p) {
		PlayerData pd = PlayerData.getPlayerData(p.getName());
		
		List<Integer> missions = new ArrayList<Integer>();
		for(int id=1;id<MissionList.EASY.size();id++) {
			missions.add(id);
		}
		
		Collections.shuffle(missions);
		
		pd.registerBoard();
		if(pd.isImposter()) {
			pd.addLine("��c�ϴ� �̼��� ����� �̼��Դϴ�.");
			pd.addLine("��a");
		}
		
		for(int i=0;i<Main.EASY_MISSION_AMOUNT;i++) {
			pd.addMission(p, MissionList.EASY.get(missions.get(i)).getClone());
		}
	}
	
	
	public void run() {
		
		switch(timer) {
		case 0:
			setting();
			break;
		case 60:
			Bukkit.broadcastMessage(Main.PREFIX + "��f=====================");
			Bukkit.broadcastMessage(Main.PREFIX + "��e���� ������ ���");
			for(int i=0;i<PLAYERS.size();i++) {
				Bukkit.broadcastMessage(Main.PREFIX + "��f" + (i+1) + ". ��a" + PLAYERS.get(i));
			}
			Bukkit.broadcastMessage(Main.PREFIX + "��f=====================");
			break;
		case 160:
			Bukkit.broadcastMessage(Main.PREFIX + "��f������ �й��մϴ�...");
			break;
		case 200:
			team_split();
			break;
		}
		
		if(!pause) timer++;
	}

}
