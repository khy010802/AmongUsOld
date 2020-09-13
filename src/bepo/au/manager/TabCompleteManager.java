package bepo.au.manager;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import bepo.au.Main;
import bepo.au.utils.Util;

public class TabCompleteManager implements TabCompleter{

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		Player player = (Player) sender;
		Block targ = player.getTargetBlock((Set<Material>) null, 5);
		if (command.getName().equals("au")) {
			switch (args.length) {
			case 2:
				if (args[0].equalsIgnoreCase("locate")) return Arrays.asList(Main.getLocManager().getList());
			case 3:
				if (args[0].equalsIgnoreCase("locate")) return Collections.singletonList("block");
			}
		}
		return null;
	}

}
