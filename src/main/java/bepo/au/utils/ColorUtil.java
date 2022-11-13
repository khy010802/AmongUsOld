package bepo.au.utils;

import org.bukkit.DyeColor;

import org.bukkit.ChatColor;


	public enum ColorUtil{
	  BLACK(DyeColor.BLACK, ChatColor.BLACK), 
	  BLUE(DyeColor.BLUE, ChatColor.BLUE), 
	  BROWN(DyeColor.BROWN, ChatColor.DARK_RED), 
	  CYAN(DyeColor.CYAN, ChatColor.DARK_AQUA), 
	  GRAY(DyeColor.GRAY, ChatColor.DARK_GRAY), 
	  GREEN(DyeColor.GREEN,ChatColor.DARK_GREEN), 
	  LIGHT_BLUE(DyeColor.LIGHT_BLUE, ChatColor.AQUA), 
	  LIGHT_GRAY(DyeColor.LIGHT_GRAY, ChatColor.GRAY), 
	  LIME(DyeColor.LIME, ChatColor.GREEN), 
	  MAGENTA(DyeColor.MAGENTA, ChatColor.LIGHT_PURPLE), 
	  ORANGE(DyeColor.ORANGE, ChatColor.GOLD), 
	  PINK(DyeColor.PINK, ChatColor.LIGHT_PURPLE), 
	  PURPLE(DyeColor.PURPLE, ChatColor.DARK_PURPLE), 
	  RED(DyeColor.RED, ChatColor.RED), 
	  WHITE(DyeColor.WHITE, ChatColor.WHITE), 
	  YELLOW(DyeColor.YELLOW, ChatColor.YELLOW);
		private final DyeColor dyecolor;
		private final ChatColor chatcolor;
		
		private ColorUtil(DyeColor dyecolor, ChatColor chatcolor) {
			this.dyecolor = dyecolor;
			this.chatcolor = chatcolor;
		}

		public DyeColor getDyeColor() {
			return this.dyecolor;
		}

		public ChatColor getChatColor() {
			return this.chatcolor;
		}	
		
	}


