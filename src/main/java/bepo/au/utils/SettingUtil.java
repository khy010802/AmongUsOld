package bepo.au.utils;


import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;

import bepo.au.GameTimer.GameType;
import bepo.au.Main.SETTING;
import bepo.au.manager.LocManager;

public class SettingUtil {
	
	public enum ARMORSTANDS{
		
		GAME_NAME("gamename"),
		GAME_LORE("gamelore"),
		
		SUB_GAME_NAME("sub_gamename"),
		SUB_GAME_LORE1("sub_gamelore1"),
		SUB_GAME_LORE2("sub_gamelore2"),
		SUB_GAME_LORE3("sub_gamelore3"),
		SUB_GAME_LIMIT("sub_gamelimit");
		
		private String tag;
		private List<ArmorStand> as;
		
		ARMORSTANDS(String tag){
			this.tag = tag;
			this.as = new ArrayList<ArmorStand>();
		}
		
		public String getTag() {
			return tag;
		}
		
		public List<ArmorStand> getArmorStands(){
			return as;
		}
		
		public void addArmorStands(ArmorStand as) {
			this.as.add(as);
		}
		
	}
	
	public static Location lc;
	public static Location rb;
	public static ItemFrame[][] frames;
	
	public static void startSetting() {
		
		lc = LocManager.getLoc("Preview_LC").get(0);
		rb = LocManager.getLoc("Preview_RB").get(0);
		frames = getFrames(lc, rb);
		
		placeFrame(SETTING.GAMEMODE.getAsGameType().getPreviewMapId(), frames);
		updateDescription();
	}
	
	private static void updateDescription() {
		
		
		
		GameType gt = SETTING.GAMEMODE.getAsGameType();
		String[] des = gt.getGameTicker().getDescriptions();
		for(int i=0;i<des.length;i++) {
			final int index = i;
			ARMORSTANDS.values()[i].getArmorStands().forEach(a -> {
				a.setCustomName(des[index]);
			});
		}
		
	}
	
	public static void setSetting(World w, boolean next) {
		int i = SETTING.GAMEMODE.getAsGameType().ordinal();
		if(next) i++; else i--;
		
		if(i < 0) i = GameType.values().length-1; else if(i >= GameType.values().length) i = 0;
		GameType gt = GameType.values()[i];
		
		SETTING.GAMEMODE.setSetting(gt);
		gt.getGameTicker().config(1);
		placeFrame(gt.getPreviewMapId(), frames);
		updateDescription();
	}
	
	@SuppressWarnings("deprecation")
	private static void placeFrame(int start_map_id, ItemFrame[][] array) {//프레임을 설치
		for(int x=0;x<array.length;x++) {
			for(int y=0;y<array[x].length;y++) {
				ItemFrame frame = array[x][y];
				if(frame != null) {
					ItemStack is = new ItemStack(Material.FILLED_MAP);
					MapMeta mm = (MapMeta) is.getItemMeta();
					mm.setMapId(start_map_id);
					is.setItemMeta(mm);
					frame.setItem(null);
					frame.setItem(is);
				}
			
				start_map_id++;
			}
		}
	}
	
	public static void logo_Frames(){
		World w = lc.getWorld();
		Location lc = new Location(w, 160, 12, 25);
		Location rb = new Location(w, 147, 5, 25);
		
		ItemFrame[][] frames = getFrames(lc, rb);
		placeFrame(1, frames);
		
	}
	
	private static ItemFrame[][] getFrames(Location lc, Location rb) {//프레임을 얻어옴
		int lc_x = lc.getBlockX();
		int lc_z = lc.getBlockZ();
		int rb_x = rb.getBlockX();
		int rb_z = rb.getBlockZ();
		
		int x_diff = Math.abs(lc_x - rb_x);
		int z_diff = Math.abs(lc_z - rb_z);
		int y_diff = lc.getBlockY() - rb.getBlockY();
		
		boolean change_x = true;
		BlockFace bf;
		int diff;
		int start_temp;
		if(x_diff == 0) {
			diff = z_diff;
			if(lc_z > rb_z) bf = BlockFace.EAST;
			else bf = BlockFace.WEST;
			change_x = false;
			start_temp = Math.min(lc_z, rb_z);
		}
		else {
			if(lc_x > rb_x) bf = BlockFace.NORTH;
			else bf = BlockFace.SOUTH;
			diff = x_diff;
			start_temp = Math.min(lc_x, rb_x);
		}
		
		ItemFrame[][] arrays = new ItemFrame[diff+1][y_diff+1];

		for(int xz=0;xz<=diff;xz++) {
			for(int y=0;y<=y_diff;y++) {
				Location loc = new Location(lc.getWorld(), 0.5D + (change_x ? start_temp + xz : lc_x), rb.getBlockY() + y + 0.5D, 0.5D + (!change_x ? start_temp + xz : lc_z)); //
				//Bukkit.getConsoleSender().sendMessage("spawned " + loc.getBlockX() + " / " + loc.getBlockY() + " / " + loc.getBlockZ());
				ItemFrame frame = null;
				for(Entity e : loc.getNearbyEntities(0.5D, 0.5D, 0.5D)) if(e instanceof ItemFrame) { frame = (ItemFrame) e; break; }
				if(frame == null) {
					frame = (ItemFrame) lc.getWorld().spawnEntity(loc, EntityType.ITEM_FRAME);
					frame.setFacingDirection(bf);
					frame.addScoreboardTag("au_reset");//한번 지워보자
					frame.setFixed(true);
					frame.setVisible(false);
					frame.setSilent(true);
				}
				if(frame.getFacing() != bf){
					frame.remove();
					frame = null;
					for(Entity e : loc.getNearbyEntities(0.5D, 0.5D, 0.5D)) if(e instanceof ItemFrame){ frame = (ItemFrame) e; break; }
					if (frame==null) for(Entity e : loc.getNearbyEntities(1.0D, 1.05D, 1.0D)) if(e instanceof ItemFrame){ frame = (ItemFrame) e; break; }
				}
				arrays[diff-xz][y_diff-y] = frame;
			}
		}
		
		return arrays;
		
	}
	
}
