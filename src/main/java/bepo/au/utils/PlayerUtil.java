package bepo.au.utils;

import bepo.au.Main;
import bepo.au.base.PlayerData;
import bepo.au.function.ItemList;
import bepo.au.manager.LocManager;
import net.minecraft.server.v1_16_R3.*;
import net.minecraft.server.v1_16_R3.PacketPlayOutPlayerInfo.EnumPlayerInfoAction;
import net.minecraft.server.v1_16_R3.PacketPlayOutWorldBorder.EnumWorldBorderAction;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.ArmorStand.LockType;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PlayerUtil {

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

		public String getName() {
			return this.name;
		}

		public void reset() {
			if (getPlayer() == null)
				return;
			List<Location> lists = new ArrayList<Location>(shulkerinfo.keySet());
			for (Location loc : lists)
				removeShulker(loc);
		}

		public void removeShulker(Location loc) {

			if (getPlayer() == null)
				return;

			Location l = locationParser(loc);
			if (shulkerinfo.containsKey(l.getBlock().getLocation())) {
				PacketPlayOutEntityDestroy destroy = new PacketPlayOutEntityDestroy(
						shulkerinfo.get(l.getBlock().getLocation()));
				((CraftPlayer) getPlayer()).getHandle().playerConnection.sendPacket(destroy);

				shulkerinfo.remove(l.getBlock().getLocation());
			}
		}

		public void addShulker(Location loc, ColorUtil c) {

			if (getPlayer() == null)
				return;

			Location l = locationParser(loc);

			if (shulkerinfo.containsKey(l.getBlock().getLocation()))
				removeShulker(l);

			WorldServer ws = ((CraftWorld) loc.getWorld()).getHandle();
			EntityMagmaCube es = new EntityMagmaCube(EntityTypes.MAGMA_CUBE, ws);
			// EntityShulker es = new EntityShulker(EntityTypes.SHULKER, ws);

			es.setPosition(loc.getX(), loc.getBlockY() + 0.25D, loc.getZ());
			es.setInvulnerable(true);
			es.setNoAI(true);
			es.setSize(1, true);
			es.setSilent(true);

			es.setFlag(6, true); // 밝기
			es.setFlag(5, true); // 투명화

			es.setInvisible(true);
			es.glowing = true;

			if (c != ColorUtil.WHITE) {
				Scoreboard board = Bukkit.getScoreboardManager().getMainScoreboard();
				if (getPlayer().getScoreboard() != null)
					board = getPlayer().getScoreboard();
				String tname = "sh" + c.getChatColor().name();

				Team team;
				if (board.getTeam(tname) != null)
					team = board.getTeam(tname);
				else {
					team = board.registerNewTeam(tname);
					team.setColor(c.getChatColor());
				}

				team.addEntry(es.getUniqueIDString());
			}

			PacketPlayOutSpawnEntityLiving packet = new PacketPlayOutSpawnEntityLiving(es);
			((CraftPlayer) getPlayer()).getHandle().playerConnection.sendPacket(packet);

			PacketPlayOutEntityMetadata metaPacket = new PacketPlayOutEntityMetadata(es.getId(), es.getDataWatcher(),
					true);
			((CraftPlayer) getPlayer()).getHandle().playerConnection.sendPacket(metaPacket);

			shulkerinfo.put(l.getBlock().getLocation(), es.getId());
		}

		private Location locationParser(Location loc) {
			Location l = loc.clone();
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
		if (Shulkers.containsKey(p.getName()))
			si = Shulkers.get(p.getName());
		else {
			si = new ShulkerInfo(p.getName());
			Shulkers.put(p.getName(), si);
		}

		si.addShulker(loc, color);
	}

	public static void removeGlowingBlock(Player p, Location loc) {
		if (!Shulkers.containsKey(p.getName()))
			return;
		ShulkerInfo si = Shulkers.get(p.getName());
		si.removeShulker(loc);
	}

	public static void resetGlowingBlock(Player p) {
		if (!Shulkers.containsKey(p.getName()))
			return;
		ShulkerInfo si = Shulkers.get(p.getName());
		si.reset();

	}

	/*
	 * 의자 기능
	 */
	private static HashMap<Player, ArmorStand> chair = new HashMap<Player, ArmorStand>();

	public static void sitChair(Player p, Location loc) {
		if (isSitting(p))
			return;
		ArmorStand item = dropSeat(loc.getBlock());
		item.addPassenger(p);
		p.setAllowFlight(true);
		chair.put(p, item);
	}

	public static boolean isSitting(Player p) {

		if (chair.containsKey(p) && chair.get(p) != null) {
			if (chair.get(p).isDead() && !chair.get(p).isValid()) {
				chair.remove(p);
				p.setAllowFlight(false);
				return false;
			}
			if (chair.get(p).getPassengers().contains(p))
				return true;
		}

		return false;
	}

	public static void removeChair(Player p) {
		if (isSitting(p)) {
			p.eject();
			p.setAllowFlight(false);
			chair.get(p).remove();
			chair.remove(p);//
		}
	}

	public static void resetChairs() {
		for (ArmorStand as : chair.values()) {
			as.remove();
		}
		chair.clear();
	}

	private static ArmorStand dropSeat(Block chair) {
		Location location = chair.getLocation().add(0.5, -1.0, 0.5);
		ArmorStand drop = (ArmorStand) location.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
		drop.setInvulnerable(true);
		drop.setVisible(false);
		drop.setGravity(false);
		drop.hasEquipmentLock(EquipmentSlot.HAND, LockType.REMOVING_OR_CHANGING);
		drop.teleport(location);
		return drop;
	}

	/*
	 * 플레이어 아이템 지급
	 */

	public static void getImposterSet(Player p, boolean first) {
		HashMap<Integer, ItemStack> islist = ItemList.getImposterSet();
		for (int i : islist.keySet())
			p.getInventory().setItem(i, islist.get(i));

		if (!first) {
			PlayerData pd = PlayerData.getPlayerData(p.getName());
			if (pd != null)
				pd.updateItems(p);
		}

		p.getInventory().setHeldItemSlot(0);
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
		showTabList(target);
		List<Player> list;
		if (hidden.containsKey(p))
			list = hidden.get(p);
		else {
			list = new ArrayList<Player>();
			hidden.put(p, list);
		}
		if (!list.contains(target))
			list.add(target);
	}

	public static void showPlayer(Player p, Player target) {

		if (!invisible.contains(target.getName())) {
			p.showPlayer(Main.getInstance(), target);
			if (hidden.containsKey(p)) {
				hidden.get(p).remove(target);
			}
		}

	}

	public static boolean isHidden(Player p, Player target) {
		return ((hidden.containsKey(p) && hidden.get(p).contains(target)));
	}
	
	public static boolean isInvisible(Player p) {
		return invisible.contains(p.getName());
	}

	public static void resetHidden(Player p) {
		for (Player ap : Bukkit.getOnlinePlayers())
			if (!p.equals(ap)) {
				p.showPlayer(Main.getInstance(), ap);

			}
		if (hidden.containsKey(p))
			hidden.clear();
		if (invisible.contains(p.getName()))
			invisible.remove(p.getName());
	}

	public static void setInvisible(Player p, boolean inv) {
		if (inv) {
			if (!invisible.contains(p.getName()))
				invisible.add(p.getName());
			for (Player ap : Bukkit.getOnlinePlayers()) {
				showTabList(p);
				ap.hidePlayer(Main.getInstance(), p);
			}
		} else {
			invisible.remove(p.getName());
		}
		// Bukkit.broadcastMessage("invisible toggle " + p.getName() + " " + inv);
	}

	public static void goVelocity(Player p1, Location lo, double value) {
		p1.setVelocity(
				p1.getVelocity().add(lo.toVector().subtract(p1.getLocation().toVector()).normalize().multiply(value)));
	}

	public static ItemStack[] getColoredArmorContent(ColorUtil c) {
		String[] parts = { "BOOTS", "LEGGINGS", "CHESTPLATE", "HELMET", };
		ItemStack[] ac = new ItemStack[4];

		for (int temp = 0; temp < 4; temp++) {
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
		if (is == null || is.getType() == Material.AIR || !is.hasItemMeta()
				|| !(is.getItemMeta() instanceof Damageable))
			return;

		setItemDamage(p, slot, (int) (is.getType().getMaxDurability() * (1D - ratio)));
	}

	public static void setItemDamage(Player p, int slot, int dam) {
		ItemStack is = p.getInventory().getItem(slot);
		if (is == null || is.getType() == Material.AIR || !is.hasItemMeta()
				|| !(is.getItemMeta() instanceof Damageable))
			return;

		Damageable ism = (Damageable) is.getItemMeta();
		ism.setDamage(dam);
		is.setItemMeta((ItemMeta) ism);
	}

	public static void showTabList(Player player) {
		PacketPlayOutPlayerInfo pack = new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.ADD_PLAYER,
				((CraftPlayer) player).getHandle());
		for (Player ap : Bukkit.getOnlinePlayers())
			((CraftPlayer) ap).getHandle().playerConnection.sendPacket(pack);
	}

	public static ArmorStand spawnDecoy(Location loc, PlayerData pd) {
		ArmorStand as = (ArmorStand) loc.getWorld().spawnEntity(loc, EntityType.ARMOR_STAND);
		as.setBasePlate(false);
		as.setArms(true);
		as.setSilent(true);
		as.setCustomName(pd.getName());
		as.setCustomNameVisible(false);

		EntityEquipment ee = as.getEquipment();
		ItemStack[] set = PlayerUtil.getColoredArmorContent(pd.getColor());
		ee.setArmorContents(set);
		ee.setHelmet(pd.getHead(true));

		return as;
	}

	private static ArrayList<Location> SEATS;
	private static List<PlayerData> DATALIST;

	public static void setSeats(boolean checkAlive) { // 자리로 TP
		DATALIST = PlayerData.getPlayerDataList();
		SEATS = LocManager.getLoc("SEATS");
		if (DATALIST.size() > SEATS.size()) {
			Util.debugMessage("플레이어 수보다 자리가 적습니다.");
		} 
		for (int idx = 0; idx < DATALIST.size(); idx++) {
			Player currentPlayer = Bukkit.getPlayer(DATALIST.get(idx).getName());
			if (currentPlayer != null && currentPlayer.getVehicle() == null && (!checkAlive || DATALIST.get(idx).isAlive())) {
				currentPlayer.teleport(SEATS.get(idx));// 플레이어를 각 자리로 이동
			}
		}
	}
	
	
	public static void toggleRedEffect(Player p, boolean bool) {
		CraftPlayer cp = (CraftPlayer) p;
		WorldBorder wb = cp.getHandle().world.getWorldBorder();
		if(bool) {
			wb.setSize(1D);
			wb.setCenter(p.getLocation().getX() + 10_000, p.getLocation().getZ() + 10_000);
			cp.getHandle().playerConnection.sendPacket(new PacketPlayOutWorldBorder(wb, EnumWorldBorderAction.INITIALIZE));
		} else {
			wb.setSize(30_000_000);
			wb.setCenter(p.getLocation().getX(), p.getLocation().getZ());
			cp.getHandle().playerConnection.sendPacket
			(new PacketPlayOutWorldBorder(wb, EnumWorldBorderAction.INITIALIZE));
		}
	}
}