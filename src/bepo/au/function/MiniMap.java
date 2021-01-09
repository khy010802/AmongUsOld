package bepo.au.function;

import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.imageio.ImageIO;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapCursor;
import org.bukkit.map.MapCursorCollection;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.bukkit.map.MapCursor.Type;
import org.bukkit.map.MapView.Scale;

import bepo.au.Main;


public class MiniMap extends MapRenderer{
	
	public enum MAPLIST {
		MINIMAP;
		
		private Image img;
		
		public void putMap(Image img) {
			this.img = img;
		}
		
		public Image getImg() {
			return img;
		}
	}
	
	public static void renderMaps() {
		try {
			MAPLIST.MINIMAP.putMap(ImageIO.read(new File(Main.getInstance().getDataFolder(), "images/map.png")));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static List<UUID> rendered = new ArrayList<UUID>();
	
	private MapCursor cursor;
	private Location loc;
	
	public MiniMap(Location loc) {
		this.loc = loc;
		cursor = new MapCursor((byte) 20, (byte)40, (byte)3, MapCursor.Type.BANNER_ORANGE, true);
	}
	
	@Override
	public void render(MapView mv, MapCanvas mc, Player p) {
		
		
		
		int x = loc.getBlockX();
		int z = loc.getBlockZ();
		
		int now_x = p.getLocation().getBlockX();
		int now_z = p.getLocation().getBlockZ();
		//float yaw = p.getLocation().getYaw() / 16F;
		
		//byte dir = (byte) yaw;
		
		int coord_x = x - now_x;
		int coord_z = z - now_z;
		
		if(coord_x < -128) coord_x = -128; else if(coord_x > 127) coord_x = 127;
		if(coord_z < -128) coord_z = -128; else if(coord_z > 127) coord_z = 127;
		
		MapCursorCollection mcc = mc.getCursors();
		mcc.removeCursor(cursor);
		cursor = new MapCursor((byte) coord_x, (byte) coord_z, (byte) 0, Type.SMALL_WHITE_CIRCLE, true);
		mcc.addCursor(cursor);
		
		if(rendered.contains(p.getUniqueId())) return;
		rendered.add(p.getUniqueId());
		mc.drawImage(0, 0, MAPLIST.MINIMAP.getImg());
	}
	
	public static ItemStack createMap(Player p) {
		
		MapView mv = Bukkit.createMap(p.getWorld());
		for(MapRenderer mr : mv.getRenderers()) mv.removeRenderer(mr);
		mv.setScale(Scale.FARTHEST);
		mv.addRenderer(new MiniMap(p.getLocation()));
		mv.setLocked(true);
		
		ItemStack is = new ItemStack(Material.FILLED_MAP);
		MapMeta mm = (MapMeta) is.getItemMeta();
		mm.setMapView(mv);
		is.setItemMeta(mm);
		
		return is;
		
		
	}

}
