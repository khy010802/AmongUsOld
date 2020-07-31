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
			sender.sendMessage(Main.PREFIX + "��c�� ��ɾ�� ������ �÷��̾ ��� �����մϴ�.");
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
			p.sendMessage(Main.PREFIX + "��c������ �̹� ���۵Ǿ��ֽ��ϴ�.");
		}
	}
	
	private void stop(Player p) {
		if(Main.gt != null) {
			Main.gt.stop();
			Bukkit.broadcastMessage(Main.PREFIX + "��c��l" + p.getName() + "�Բ��� ������ ���� �����ϼ̽��ϴ�.");
		} else {
			p.sendMessage(Main.PREFIX + "��c������ ���۵Ǿ����� �ʽ��ϴ�.");
		}
	}
	
	private void toggle(Player p, String[] args, boolean player) {
		
		if(args.length <= 1) {
			p.sendMessage(Main.PREFIX + "��c�ùٸ� ��ɾ� : /au " + args[0] + " [�̸�]");
			return;
		}
		
		String name = args[1].toLowerCase();
		String s = player ? "������" : "������";
		
		if(player && Main.OBSERVER.contains(name)) {
			Main.OBSERVER.remove(name);
			p.sendMessage(Main.PREFIX + "��f" + args[1] + " ���� �����ڿ��� �����߽��ϴ�.");
		} else if(!player && !Main.OBSERVER.contains(name)) {
			Main.OBSERVER.add(name);
			p.sendMessage(Main.PREFIX + "��f" + args[1] + " ���� �����ڿ� �߰��߽��ϴ�.");
		} else {
			p.sendMessage(Main.PREFIX + "��c�̹� " + args[1] + "���� " + s + "�� ��ϵǾ��ֽ��ϴ�.");
		}
	}
	
	private void help(Player p) {
		p.sendMessage(Main.PREFIX + "��f/au start ��e������ �����մϴ�.");
		p.sendMessage(Main.PREFIX + "��f/au stop ��e������ ���������մϴ�.");
		p.sendMessage(Main.PREFIX + "��f/au config ��e������ �����մϴ�.");
		p.sendMessage(Main.PREFIX + "��f/au [player/observer] [�̸�] ��e�ش� �÷��̾ ������/�����ڷ� ����մϴ�.");
		 
	}

}
