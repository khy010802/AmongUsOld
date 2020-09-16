package bepo.au.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_16_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R2.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_16_R2.util.CraftMagicNumbers;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.Vector;

import bepo.au.Main;
import bepo.au.function.ItemList;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.v1_16_R2.EntityFallingBlock;
import net.minecraft.server.v1_16_R2.EntityMagmaCube;
import net.minecraft.server.v1_16_R2.EntityPlayer;
import net.minecraft.server.v1_16_R2.EntityShulker;
import net.minecraft.server.v1_16_R2.EntityTypes;
import net.minecraft.server.v1_16_R2.IBlockData;
import net.minecraft.server.v1_16_R2.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_16_R2.PacketPlayOutEntityMetadata;
import net.minecraft.server.v1_16_R2.PacketPlayOutEntityTeleport;
import net.minecraft.server.v1_16_R2.PacketPlayOutSpawnEntity;
import net.minecraft.server.v1_16_R2.PacketPlayOutSpawnEntityLiving;
import net.minecraft.server.v1_16_R2.WorldServer;

public class PlayerUtil {
	
	/*
	 public static class ShulkerInfo {
		
		private HashMap<Location, Integer> shulkerinfo;
		private String name;
		
		public ShulkerInfo(String name) {
			this.name = name;
			this.shulkerinfo = new HashMap<Location, Integer>();
		}
		
		public String getName() { return this.name; }
		
		public void reset() {
			if(getPlayer() == null) return;
			List<Location> lists = new ArrayList<Location>(shulkerinfo.keySet());
			for(Location loc : lists) removeShulker(loc);
		}
		
		public void removeShulker(Location loc) {
			
			if(getPlayer() == null) return;
			
			Location l = locationParser(loc);
			if(shulkerinfo.containsKey(l)) {
				PacketPlayOutEntityDestroy destroy = new PacketPlayOutEntityDestroy(shulkerinfo.get(l));
                ((CraftPlayer)getPlayer()).getHandle().playerConnection.sendPacket(destroy);
                
                shulkerinfo.remove(l);
			}
		}
		
		public void addShulker(Location loc, ColorUtil c) {
			
			if(getPlayer() == null) return;
			
			Location l = locationParser(loc);
			
			if(shulkerinfo.containsKey(l)) removeShulker(l);
			
			WorldServer ws = ((CraftWorld) loc.getWorld()).getHandle();
			EntityShulker es = new EntityShulker(EntityTypes.SHULKER, ws);
			
			es.setPosition(loc.getBlockX() + 0.5D, loc.getBlockY(), loc.getBlockZ() + 0.5D);
			es.setInvulnerable(true);
			es.setNoAI(true);
			es.setSilent(true);
			
			es.setFlag(6, true); // 밝기
			es.setFlag(5, true); // 투명화
			
			es.setInvisible(true);
			es.glowing = true;
			
			if(c != ColorUtil.WHITE) {
				Scoreboard board = Bukkit.getScoreboardManager().getMainScoreboard();
				String tname = "sh" + c.getChatColor().name();
				
				Team team;
				if(board.getTeam(tname) != null) team = board.getTeam(tname);
				else {
					team = board.registerNewTeam(tname);
					team.setColor(c.getChatColor());
				}
				
				team.addEntry(es.getUniqueIDString());
			}
			
			PacketPlayOutSpawnEntityLiving packet = new PacketPlayOutSpawnEntityLiving(es);
			((CraftPlayer) getPlayer()).getHandle().playerConnection.sendPacket(packet);
			
			PacketPlayOutEntityMetadata metaPacket = new PacketPlayOutEntityMetadata(es.getId(), es.getDataWatcher(), true);
			((CraftPlayer) getPlayer()).getHandle().playerConnection.sendPacket(metaPacket);
			
			shulkerinfo.put(l, es.getId());
		}
		
		private Location locationParser(Location loc) {
			Location l = loc.getBlock().getLocation().add(0.5D, 0, 0.5D);
			l.setYaw(0F);
			l.setPitch(0F);
			return l;
		}
		
		private Player getPlayer() {
			return Bukkit.getPlayer(name);
		}
		
	}
	 */
	
	
	/*
	 * 셜커 기능
	 */
	 public static class ShulkerInfo {
			
			private HashMap<Location, Integer> shulkerinfo;
			private String name;
			
			public ShulkerInfo(String name) {
				this.name = name;
				this.shulkerinfo = new HashMap<Location, Integer>();
			}
			
			public String getName() { return this.name; }
			
			public void reset() {
				if(getPlayer() == null) return;
				List<Location> lists = new ArrayList<Location>(shulkerinfo.keySet());
				for(Location loc : lists) removeShulker(loc);
			}
			
			public void removeShulker(Location loc) {
				
				if(getPlayer() == null) return;
				
				Location l = locationParser(loc);
				if(shulkerinfo.containsKey(l)) {
					PacketPlayOutEntityDestroy destroy = new PacketPlayOutEntityDestroy(shulkerinfo.get(l));
	                ((CraftPlayer)getPlayer()).getHandle().playerConnection.sendPacket(destroy);
	                
	                shulkerinfo.remove(l);
				}
			}
			
			public void addShulker(Location loc, ColorUtil c) {
				
				if(getPlayer() == null) return;
				
				Location l = locationParser(loc);
				
				if(shulkerinfo.containsKey(l)) removeShulker(l);
				
				WorldServer ws = ((CraftWorld) loc.getWorld()).getHandle();
				EntityMagmaCube es = new EntityMagmaCube(EntityTypes.MAGMA_CUBE, ws);
				//EntityShulker es = new EntityShulker(EntityTypes.SHULKER, ws);
				
				es.setPosition(loc.getBlockX() + 0.5D, loc.getBlockY() + 0.25D, loc.getBlockZ() + 0.5D);
				es.setInvulnerable(true);
				es.setNoAI(true);
				es.setSize(1, true);
				es.setSilent(true);
				
				es.setFlag(6, true); // 밝기
				es.setFlag(5, true); // 투명화
				
				es.setInvisible(true);
				es.glowing = true;
				
				if(c != ColorUtil.WHITE) {
					Scoreboard board = Bukkit.getScoreboardManager().getMainScoreboard();
					if(getPlayer().getScoreboard() != null) board = getPlayer().getScoreboard();
					String tname = "sh" + c.getChatColor().name();
					
					Team team;
					if(board.getTeam(tname) != null) team = board.getTeam(tname);
					else {
						team = board.registerNewTeam(tname);
						team.setColor(c.getChatColor());
					}
					
					team.addEntry(es.getUniqueIDString());
				}
				
				PacketPlayOutSpawnEntityLiving packet = new PacketPlayOutSpawnEntityLiving(es);
				((CraftPlayer) getPlayer()).getHandle().playerConnection.sendPacket(packet);
				
				PacketPlayOutEntityMetadata metaPacket = new PacketPlayOutEntityMetadata(es.getId(), es.getDataWatcher(), true);
				((CraftPlayer) getPlayer()).getHandle().playerConnection.sendPacket(metaPacket);
				
				shulkerinfo.put(l, es.getId());
			}
			
			private Location locationParser(Location loc) {
				Location l = loc.getBlock().getLocation().add(0.5D, 0.5D, 0.5D);
				l.setYaw(0F);
				l.setPitch(0F);
				return l;
			}
			
			private Player getPlayer() {
				return Bukkit.getPlayer(name);
			}
			
		}
	
	private static HashMap<String, ShulkerInfo> Shulkers = new HashMap<String, ShulkerInfo>();
	
	public static void spawnGlowingBlock(Player p, Location loc, ColorUtil color) {
		ShulkerInfo si;
		if(Shulkers.containsKey(p.getName())) si = Shulkers.get(p.getName());
		else {
			si = new ShulkerInfo(p.getName());
			Shulkers.put(p.getName(), si);
		}
		
		si.addShulker(loc, color);
	}
	
	public static void removeGlowingBlock(Player p, Location loc) {
		if(!Shulkers.containsKey(p.getName())) return;
		ShulkerInfo si = Shulkers.get(p.getName());
		si.removeShulker(loc);
	}
	
	public static void resetGlowingBlock(Player p) {
		if(!Shulkers.containsKey(p.getName())) return;
		ShulkerInfo si = Shulkers.get(p.getName());
		si.reset();
		
	}
	
	
	/*
	 * 의자 기능
	 */
	private static HashMap<Player, Item> chair = new HashMap<Player, Item>();
	
	public static void sitChair(Player p, Location loc) {
		if(isSitting(p)) removeChair(p);
		p.eject();
		Item item = dropSeat(loc.getBlock());
		item.addPassenger(p);
		chair.put(p, item);
	}
	
	public static boolean isSitting(Player p) {
		
		if(chair.containsKey(p) && chair.get(p) != null) {
			if(chair.get(p).isDead() && !chair.get(p).isValid()) {
				chair.remove(p);
				return false;
			}
			return true;
		}
		
		return false;
	}
	
	public static void removeChair(Player p) {
		if(isSitting(p)) {
			p.eject();
			chair.get(p).remove();
			chair.remove(p);
		}
	}
	
	private static Item dropSeat(Block chair) {
		Location location = chair.getLocation().add(0.5, 0.2, 0.5);
		Item drop = location.getWorld().dropItemNaturally(location, new ItemStack(Material.PUMPKIN_STEM));
		drop.setPickupDelay(Integer.MAX_VALUE);
		drop.teleport(location);
		drop.setVelocity(new Vector(0, 0, 0));
		return drop;
	}
	
	/*
	 * 플레이어 아이템 지급
	 */
	
	public static void getImposterSet(Player p, boolean first) {
		HashMap<Integer, ItemStack> islist = ItemList.getImposterSet();
		for(int i : islist.keySet()) p.getInventory().setItem(i, islist.get(i));
		
		if(!first) {
			// 내구도 갱신
		}
	}
	
	/*
	 * 액션바
	 */
	
	public static void sendActionBar(Player p, String string) {
		p.sendActionBar(string);
	}
	
	/*
	 * 플레이어 숨기기
	 */
	private static HashMap<Player, List<Player>> hidden = new HashMap<Player, List<Player>>();
	private static List<String> invisible = new ArrayList<String>();
	
	public static void hidePlayer(Player p, Player target) {
		
		p.hidePlayer(Main.getInstance(), target);
		
		List<Player> list;
		if(hidden.containsKey(p)) list = hidden.get(p);
		else {
			list = new ArrayList<Player>();
			hidden.put(p, list);
		}
		if(!list.contains(target)) list.add(target);
	}
	
	public static void showPlayer(Player p, Player target) {
		
		if(!invisible.contains(target.getName())) {
			p.showPlayer(Main.getInstance(), target);
			if(hidden.containsKey(p)) {
				hidden.get(p).remove(target);
			}
		}
		
		
	}
	
	public static boolean isHidden(Player p, Player target) {
		return ((hidden.containsKey(p) && hidden.get(p).contains(target)) || invisible.contains(target.getName()));
	}
	
	public static void resetHidden(Player p) {
		for(Player ap : Bukkit.getOnlinePlayers()) if(!p.equals(ap)) p.showPlayer(Main.getInstance(), ap);
		if(hidden.containsKey(p)) hidden.clear();
		if(invisible.contains(p.getName())) invisible.remove(p.getName());
	}
	
	public static void setInvisible(Player p, boolean inv) {
		if(inv) {
			if(!invisible.contains(p.getName())) invisible.add(p.getName());
			for(Player ap : Bukkit.getOnlinePlayers()) ap.hidePlayer(Main.getInstance(), p);
		} else {
			invisible.remove(p.getName());
		}
		Bukkit.broadcastMessage("invisible toggle " + p.getName() + " " + inv);
	}
	
	
	public static void goVelocity(Player p1, Location lo, double value) {
		p1.setVelocity(p1.getVelocity().add(lo.toVector().subtract(p1.getLocation().toVector()).normalize().multiply(value)));
	}
	
	
	public static ItemStack[] getColoredArmorContent(ColorUtil c) {
		String[] parts = { "BOOTS", "LEGGINGS", "CHESTPLATE", "HELMET",  };
		ItemStack[] ac = new ItemStack[4];
		
		for(int temp=0;temp<4;temp++) {
			ItemStack is = new ItemStack(Material.getMaterial("LEATHER_" + parts[temp]));
			LeatherArmorMeta lam = (LeatherArmorMeta) is.getItemMeta();
			lam.setColor(c.getDyeColor().getColor());
			lam.setUnbreakable(true);
			is.setItemMeta(lam);
			ac[temp] = is;
		}
		return ac;
	}
	
	public static void setItemDamage(Player p, int slot, double ratio) {
		ItemStack is = p.getInventory().getItem(slot);
		if(is == null || is.getType() == Material.AIR || !is.hasItemMeta() || !(is.getItemMeta() instanceof Damageable)) return;
		
		setItemDamage(p, slot, (int) (is.getType().getMaxDurability() * (1D-ratio)));
	}
	
	public static void setItemDamage(Player p, int slot, int dam) {
		ItemStack is = p.getInventory().getItem(slot);
		if(is == null || is.getType() == Material.AIR || !is.hasItemMeta() || !(is.getItemMeta() instanceof Damageable)) return;
		
		Damageable ism = (Damageable) is.getItemMeta();
		ism.setDamage(dam);
		is.setItemMeta((ItemMeta) ism);
	}

}
