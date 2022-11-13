package bepo.au.manager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import bepo.au.GameTimer;
import bepo.au.Main;
import bepo.au.Main.SETTING;

public class TabCompleteManager implements TabCompleter{

	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		Player player = (Player) sender;
		if (command.getName().equals("au")) {
			switch (args.length) {
			case 1:
				return player.isOp() ? CommandManager.opCOMMANDS : CommandManager.userCOMMANDS ;
			case 2:
				if (args[0].equalsIgnoreCase("debug")) return getSortedArgs(args[1],Arrays.asList("imposter","map","check"));
				if (args[0].equalsIgnoreCase("locate")) return getSortedArgs(args[1],Arrays.asList(Main.getLocManager().getList()));
				if (args[0].equalsIgnoreCase("config")) return getSortedArgs(args[1],Main.SETTING.SETTING_LIST);
			case 3:
				if (args[0].equalsIgnoreCase("locate")) return Collections.singletonList("block");

				if (args[0].equalsIgnoreCase("list")) return Collections.singletonList("observer");

				if (args[0].equalsIgnoreCase("config")) {
				SETTING set;
				try{set = SETTING.valueOf(args[1]);}
				catch(IllegalArgumentException e){return Collections.singletonList("잘못된 클래스 이름입니다!");}
				if(set.getType().isAssignableFrom(GameTimer.GameType.class)) return getSortedArgs(args[2],GameTimer.GameType.TYPES);
				if(set.getType().isAssignableFrom(Boolean.class)) return getSortedArgs(args[2],Arrays.asList("true","false"));
				if(set.getType().isAssignableFrom(Integer.class)) return getSortedArgs(args[2],Arrays.asList("0","1","5","10","20"));
				if(set.getType().isAssignableFrom(Double.class)) return getSortedArgs(args[2],Arrays.asList("0.5","1.0","1.5","2.0"));
				else return Collections.singletonList("잘못된 클래스 이름입니다.");
				}
			}
		}
		return null;
	}
	
	private List<String> getSortedArgs(String head, List<String> args){
		List<String> result = new ArrayList<String>();

		for (String a : args){
			if (a.toLowerCase().startsWith(head.toLowerCase())){
				result.add(a);
			};
		}

		return result;
	}

}
