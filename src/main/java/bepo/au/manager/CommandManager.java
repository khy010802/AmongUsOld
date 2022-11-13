package bepo.au.manager;

import bepo.au.GameTimer;
import bepo.au.Main;
import bepo.au.Main.SETTING;
import bepo.au.function.MiniMap;
import bepo.au.utils.PlayerUtil;
import bepo.au.utils.Util;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommandManager implements CommandExecutor {
	public static List<String> opCOMMANDS = Arrays.asList("start", "stop", "config", "player", "observer","settings", "list", "debug", "des", "reload", "locate");
	public static List<String> userCOMMANDS = Arrays.asList("settings", "list");
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		

		Player p = sender instanceof Player ? (Player) sender : null;

		if (p == null) {
			sender.sendMessage(Main.PREFIX + "§c이 명령어는 플레이어만 사용 가능합니다.");
			return true;
		}
		
		if(args.length == 0) {
			help(p);
			return true;
		}

		if (userCOMMANDS.contains(args[0])) {//유저명령어인가?
			if (args[0].equalsIgnoreCase("settings")) {
				settings(p);
			} else if (args[0].equalsIgnoreCase("list")) {
				list(p,args);
			}
		} else if (opCOMMANDS.contains(args[0])) {//op명령어인가?
			if (p.isOp()) {//op인가?
				if (args[0].equalsIgnoreCase("start")) {
					start(p);
				} else if (args[0].equalsIgnoreCase("stop")) {
					stop(p);
				} else if (args[0].equalsIgnoreCase("config")) {
					config(p,args);
				} else if (args[0].equalsIgnoreCase("player")) {
					toggle(p, args, true);
				} else if (args[0].equalsIgnoreCase("observer")) {
					toggle(p, args, false);
				} else if (args[0].equalsIgnoreCase("debug")) {
					debug(p, args);
				} else if (args[0].equalsIgnoreCase("locate")) {
					locate(p, args);
				} else if (args[0].equalsIgnoreCase("reload")) {
					reload(p);
				} else if (args[0].equalsIgnoreCase("des")) {
					des(p, args);
				}
			} else {//op가 아닌데 이걸써?
				p.sendMessage(Main.PREFIX + "§c이 명령어는 관리자만 사용 가능합니다.");
			}
		} else {//등록된 명령어가 아님
			help(p);
		}
		return true;
	}

	private Location description = null;
	private int temp = 0;

	private void des(Player p, String[] args) {
		if (args.length == 1) {
			description = p.getLocation().clone().add(0, -0.5D, 0);
		} else if (args[1].equalsIgnoreCase("reset")) {

			for (Entity e : p.getNearbyEntities(3.0D, 3.0D, 3.0D)) {
				if (e instanceof ArmorStand) {
					ArmorStand as = (ArmorStand) e;
					if (as.getScoreboardTags() != null && as.getScoreboardTags().contains("description"))
						e.remove();
				}
			}
			description.add(0, 0.3D * temp, 0);
			temp = 0;
		} else {

			String msg = "";
			List<String> tags = new ArrayList<String>();
			for (int i = 1; i < args.length; i++) {
				String s = args[i];
				
				if(s.startsWith("tag:")) {
					String custom = s.substring(4);
					tags.add(custom);
					
				} else {
					if (i < args.length - 1)
						msg += s + " ";
					else
						msg += s;
				}
				
				
			}

			ArmorStand as = spawnArmorStand(description, msg);
			if(!tags.isEmpty()) {
				
				for(String s : tags) {
					if(s.startsWith("as_")) {
						ArmorStand click = spawnArmorStand(description, null);
						click.setBasePlate(false);
						click.setSmall(true);
						click.addScoreboardTag("clickable");
						click.addScoreboardTag(s.replace("as_", ""));
						as.addPassenger(click);
					} else if(s.startsWith("small")){
						as.setSmall(true);
					} else {
						as.addScoreboardTag(s);
					}
				}
			}
			

			description.add(0, -0.3D, 0);
			temp++;
		}
	}
	
	private ArmorStand spawnArmorStand(Location loc, String name) {
		ArmorStand as = (ArmorStand) description.getWorld().spawnEntity(description, EntityType.ARMOR_STAND);
		as.addScoreboardTag("description");
		as.setInvisible(true);
		as.setInvulnerable(true);
		as.setGravity(false);
		if(name != null) {
			as.setCustomName(ChatColor.translateAlternateColorCodes('&', name));
			as.setCustomNameVisible(true);
		}
		return as;
	}

	private void reload(Player p) {
		Main.getInstance().config.loadConfig();
		Main.getLocManager().loadLocs();
	}


	private void locate(Player p, String[] args) {
		if (args.length <= 1) {
			p.sendMessage(Main.PREFIX + "§c올바른 명령어 : /au locate [이름] [block]");
			return;
		}
		boolean target = args.length == 3 && args[2].equalsIgnoreCase("block");
		String name = args[1];

		Location loc = target ? p.getTargetBlock(10).getLocation() : p.getLocation();

		Main.getLocManager().inputLocation(name, loc);
		p.sendMessage(Main.PREFIX + "§e" + name + " §f에 " + (target ? "현재 바라보고 있는 블럭" : "현재 위치") + " 추가 (총 "
				+ LocManager.getLoc(name).size() + "개)");
	}



	private void debug(Player p, String[] args) {

		if (args[1].equalsIgnoreCase("imposter")) {
			GameTimer.IMPOSTER.add(p.getName());
			GameTimer.ALIVE_IMPOSTERS.add(p.getName());
			PlayerUtil.getImposterSet(p, true);
		} else if (args[1].equalsIgnoreCase("corpse")) {
			Util.spawnCorpse(p.getLocation(), p);
		} else if(args[1].equalsIgnoreCase("map")) {
			p.getInventory().addItem(MiniMap.createMap(p));
		}

	}
	
	private void config(Player p, String[] args) {
		if (args.length < 3) {
			p.sendMessage(Main.PREFIX + "§c올바른 명령어 : /au config [설정] [값]");
			return;
		}else{
			Object obj = null;
			
			if (args[2].equalsIgnoreCase("true")||args[2].equalsIgnoreCase("참")){
				obj = true;
			}else if(args[2].equalsIgnoreCase("false")||args[2].equalsIgnoreCase("거짓")){
				obj = false;
			}else if (args[2].contains(".")){
				try {
					obj = Double.parseDouble(args[2]);//double임
					} catch(NumberFormatException e) {}
			}else{
				try {
					obj = Integer.parseInt(args[2]);//int임
				}catch(NumberFormatException e1) {
					try {
					obj = GameTimer.GameType.valueOf(args[2]);//gametype임
					} catch(IllegalArgumentException e){
						p.sendMessage(Main.PREFIX + "§c잘못된 설정 이름입니다.");
						return;
					}catch(Exception e) {
					p.sendMessage(Main.PREFIX + "§c/au settings를 참고해주세요. 올바른 명령어 : /au config [설정] [값]");
					return;
					}
				} //
			}	
			try{//입력 오류 확인.
				 
			SETTING to = SETTING.valueOf(args[1]);
			Object from = obj;
			
			if(!to.getType().isInstance(from)){
				p.sendMessage(Main.PREFIX + "§c잘못된 값입니다.");
				return;
			}
			to.setSetting(from); // 서순 알아서 정리하셈
			p.sendMessage(Main.PREFIX +"§e"+ args[1]+"§f 에 §e"+args[2] +"§f (을)를 넣었습니다.");
			}catch(Exception e){
				e.printStackTrace();
				p.sendMessage(Main.PREFIX + "§c/au settings를 참고해주세요. 올바른 명령어 : /au config [설정] [값]");
			}
		} //
	}

	public static void start(Player p) {
		if (Main.gt == null) {
			Main.gt = new GameTimer(p.getWorld(), Main.getInstance());
			Main.gt.start(p);
		} else {
			p.sendMessage(Main.PREFIX + "§c게임이 이미 시작되어있습니다.");
		}
	}

	private void stop(Player p) {
		if (Main.gt != null) {
			Main.gt.stop();
			Bukkit.broadcastMessage(Main.PREFIX + "§c§l" + p.getName() + "님께서 게임을 강제 종료하셨습니다.");
		} else {
			p.sendMessage(Main.PREFIX + "§c게임이 시작되어있지 않습니다.");
		}
	}

	private void toggle(Player p, String[] args, boolean player) {

		if (args.length <= 1) {
			p.sendMessage(Main.PREFIX + "§c올바른 명령어 : /au " + args[0] + " [이름]");
			return;
		}

		String name = args[1].toLowerCase();
		String s = player ? "참가자" : "관전자";

		if (player && GameTimer.OBSERVER.contains(name)) {
			GameTimer.OBSERVER.remove(name);
			p.sendMessage(Main.PREFIX + "§f" + args[1] + " 님을 관전자에서 제거했습니다.");
		} else if (!player && !GameTimer.OBSERVER.contains(name)) {
			GameTimer.OBSERVER.add(name);
			p.sendMessage(Main.PREFIX + "§f" + args[1] + " 님을 관전자에 추가했습니다.");
		} else {
			p.sendMessage(Main.PREFIX + "§c이미 " + args[1] + "님은 " + s + "에 등록되어있습니다.");
		}
	}
	private void list(Player p, String[] args) {
		if (args.length == 1|| (!args[1].equalsIgnoreCase("observer")&&!args[1].equalsIgnoreCase("ob"))) {
			p.sendMessage(Main.PREFIX + "§f=====================");
			p.sendMessage(Main.PREFIX + "§e게임 참가자 목록");
			for (int i = 0; i < GameTimer.PLAYERS.size(); i++) {
				p.sendMessage(Main.PREFIX + "§f" + (i + 1) + ". §a" + GameTimer.PLAYERS.get(i));
				}
			p.sendMessage(Main.PREFIX + "§f=====================");
		}else{
			p.sendMessage(Main.PREFIX + "§f=====================");
			p.sendMessage(Main.PREFIX + "§e관전자 목록");
			for (int i = 0; i < GameTimer.OBSERVER.size(); i++) {
				p.sendMessage(Main.PREFIX + "§f" + (i + 1) + ". §a" + GameTimer.OBSERVER.get(i));
				}
			p.sendMessage(Main.PREFIX + "§f=====================");
		}

	}
	
	private void settings(Player p) {
		p.sendMessage(Main.PREFIX + "§f=====================");
		p.sendMessage(Main.PREFIX + "§e현재 설정 목록");
		for (SETTING setting : SETTING.values()) {
			p.sendMessage(Main.PREFIX + "§e" + setting.getName() + "§f : " + setting.get());
		}
		p.sendMessage(Main.PREFIX + "§f=====================");
	}


	private void help(Player p) {
		
		if(p.isOp()) {
			p.sendMessage(Main.PREFIX + "§f/au start §e게임을 시작합니다.");
			p.sendMessage(Main.PREFIX + "§f/au stop §e게임을 강제종료합니다.");
			p.sendMessage(Main.PREFIX + "§f/au config §e게임을 세팅합니다.");
			p.sendMessage(Main.PREFIX + "§f/au reload §e설정과 장소를 다시 불러옵니다.");
			p.sendMessage(Main.PREFIX + "§f/au [player/observer] [이름] §e해당 플레이어를 관전자/참여자로 등록합니다.");
		}
		p.sendMessage(Main.PREFIX + "§f/au list (observer) §e현재 게임에 참여중인 플레이어 목록을 출력합니다. 플레이어 목록은 게임이 시작된 후 업데이트됩니다.");
		p.sendMessage(Main.PREFIX + "§f/au settings §e현재 게임 설정을 출력합니다.");
	}

}
