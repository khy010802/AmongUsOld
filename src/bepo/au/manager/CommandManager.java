package bepo.au.manager;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import bepo.au.GameTimer;
import bepo.au.Main;
import bepo.au.base.PlayerData;
import bepo.au.base.Sabotage;
import bepo.au.base.Sabotage.SaboType;
import bepo.au.function.MissionList;
import bepo.au.utils.ColorUtil;
import bepo.au.utils.PlayerUtil;
import bepo.au.utils.Util;

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
		} else if(args[0].equalsIgnoreCase("locate")) {
			locate(p, args);
		} else if(args[0].equalsIgnoreCase("reload")) {
			reload(p);
		}
		else {
			help(p);
		}
		
		return true;
	}
	
	private void reload(Player p) {
		Main.getLocManager().loadLocs();
	}
	
	private void locate(Player p, String[] args) {
		if(args.length <= 1) {
			p.sendMessage(Main.PREFIX + "��c�ùٸ� ��ɾ� : /au locate [�̸�] [block]");
			return;
		}
		boolean target = args.length == 3 && args[2].equalsIgnoreCase("block");
		String name = args[1];
		
		Location loc = target ? p.getTargetBlock(10).getLocation() : p.getLocation();
		
		Main.getLocManager().inputLocation(name, loc);
		p.sendMessage(Main.PREFIX + "��e" + name + " ��f�� " + (target ? "���� �ٶ󺸰� �ִ� ��" : "���� ��ġ") + " �߰� (�� " + LocManager.getLoc(name).size() + "��)");
	}

	
	
	private void debug(Player p, String[] args) {
		
		int i = Integer.parseInt(args[1]);
		SaboType st;
		switch(i) {
		case 1: st = SaboType.COMM;
			break;
		case 2: st = SaboType.ELEC; break;
		case 3: st = SaboType.NUCL; break;
		case 4: st = SaboType.OXYG; break;
		default: st = SaboType.DOOR; break;
		}
		int e = Sabotage.saboActivate(st, i > 4 ? Integer.parseInt(args[2]) : 0);
		
		if(e > 0) Bukkit.broadcastMessage("�ߵ��ȵƴ뿩 �������������� - " + e);
		else if(e == 0) Bukkit.broadcastMessage("...");
	}
	
	private void config(Player p) {
		
	}
	
	private void start(Player p) {
		if(Main.gt == null) {
			Main.gt = new GameTimer(Main.getInstance());
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
		
		if(player && GameTimer.OBSERVER.contains(name)) {
			GameTimer.OBSERVER.remove(name);
			p.sendMessage(Main.PREFIX + "��f" + args[1] + " ���� �����ڿ��� �����߽��ϴ�.");
		} else if(!player && !GameTimer.OBSERVER.contains(name)) {
			GameTimer.OBSERVER.add(name);
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
