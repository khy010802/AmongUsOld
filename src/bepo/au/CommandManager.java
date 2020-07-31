package bepo.au;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandManager implements CommandExecutor{

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		
		if(!(sender instanceof Player) || !sender.isOp()) {
			sender.sendMessage(Main.PREFIX + "§c본 명령어는 오피인 플레이어만 사용 가능합니다.");
			return true;
		}
		
		Player p = (Player) sender;
		
		if(args.length == 0) {
			help(p);
		} else if(args[0].equalsIgnoreCase("start")) {
			start(p);
		} else if(args[0].equalsIgnoreCase("stop")) {
			stop(p);
		} else if(args[0].equalsIgnoreCase("config")) {
			config(p);
		} else if(args[0].equalsIgnoreCase("player")) {
			toggle(p, args, true);
		} else if(args[0].equalsIgnoreCase("observer")) {
			toggle(p, args, false);
		} else if(args[0].equalsIgnoreCase("debug")) {
			debug(p, args);
		}
		else {
			help(p);
		}
		
		return true;
	}
	
	private void debug(Player p, String[] args) {
		if(args.length == 1) {
			Mission.activateMissions();
			return;
		}
		Mission m = Mission.getMission(args[1]);
		m.onAssigned(p);
		m.onStart(p, Integer.parseInt(args[2]));
	}
	
	private void config(Player p) {
		
	}
	
	private void start(Player p) {
		if(Main.gt == null) {
			Main.gt = new GameTimer(Main.main);
			Main.gt.start(p);
		} else {
			p.sendMessage(Main.PREFIX + "§c게임이 이미 시작되어있습니다.");
		}
	}
	
	private void stop(Player p) {
		if(Main.gt != null) {
			Main.gt.stop();
			Bukkit.broadcastMessage(Main.PREFIX + "§c§l" + p.getName() + "님께서 게임을 강제 종료하셨습니다.");
		} else {
			p.sendMessage(Main.PREFIX + "§c게임이 시작되어있지 않습니다.");
		}
	}
	
	private void toggle(Player p, String[] args, boolean player) {
		
		if(args.length <= 1) {
			p.sendMessage(Main.PREFIX + "§c올바른 명령어 : /au " + args[0] + " [이름]");
			return;
		}
		
		String name = args[1].toLowerCase();
		String s = player ? "참가자" : "관전자";
		
		if(player && Main.OBSERVER.contains(name)) {
			Main.OBSERVER.remove(name);
			p.sendMessage(Main.PREFIX + "§f" + args[1] + " 님을 관전자에서 제거했습니다.");
		} else if(!player && !Main.OBSERVER.contains(name)) {
			Main.OBSERVER.add(name);
			p.sendMessage(Main.PREFIX + "§f" + args[1] + " 님을 관전자에 추가했습니다.");
		} else {
			p.sendMessage(Main.PREFIX + "§c이미 " + args[1] + "님은 " + s + "에 등록되어있습니다.");
		}
	}
	
	private void help(Player p) {
		p.sendMessage(Main.PREFIX + "§f/au start §e게임을 시작합니다.");
		p.sendMessage(Main.PREFIX + "§f/au stop §e게임을 강제종료합니다.");
		p.sendMessage(Main.PREFIX + "§f/au config §e게임을 세팅합니다.");
		p.sendMessage(Main.PREFIX + "§f/au [player/observer] [이름] §e해당 플레이어를 관전자/참여자로 등록합니다.");
		 
	}

}
