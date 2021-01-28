package bepo.au.function;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.Inventory;

import bepo.au.Main;
import bepo.au.base.PlayerData;
import bepo.au.base.Sabotage;
import bepo.au.base.Sabotage.SaboType;
import bepo.au.function.AdminMap.ROOMS;
import bepo.au.utils.ColorUtil;
import bepo.au.utils.Util;
import net.minecraft.server.v1_16_R3.SlotShulkerBox;

@SuppressWarnings("unused")
public class AdminMap {
	public static Inventory gui;
	private static final int maxslot = 54;
	private static final int yAxis = 17;
	public static final String guiName = "������ ����";

	public enum ROOMS {
	CAFE(4, 6, "�Ĵ�",Material.ENCHANTING_TABLE, Material.RED_WOOL, SaboType.DOOR),
		UE(1, 1, "��� ����",Material.DISPENSER, Material.BLUE_WOOL, SaboType.DOOR),
		MEDBEY(12, 4, "�ǹ���",Material.POTION, Material.LIME_WOOL, SaboType.DOOR),
		O2(15, -1, "��� ���޽�",Material.KELP, Material.GREEN_WOOL, SaboType.OXYG),
		WEAPONS(7, -1, "�����",Material.FIREWORK_ROCKET, Material.BROWN_WOOL),
		REACTOR(18, -1, "���ڷ�",Material.CRYING_OBSIDIAN, Material.BLACK_WOOL, SaboType.NUCL),
		SECU(20, 2, "���Ƚ�",Material.ENDER_EYE, Material.MAGENTA_WOOL, SaboType.DOOR),
		ADMIN(23, -1, "������",Material.OAK_SIGN, Material.PURPLE_WOOL),
		NAVI(26, -1, "���ؽ�",Material.LODESTONE, Material.GRAY_WOOL),
		LE(37, 3, "�Ϻο���",Material.DISPENSER, Material.LIGHT_BLUE_WOOL, SaboType.DOOR),
		ELEC(30, 5, "�����",Material.COMPARATOR, Material.YELLOW_WOOL, SaboType.DOOR, SaboType.ELEC),
		SHIELD(43, -1, "��ȣ�� �����",Material.BEACON, Material.CYAN_WOOL, SaboType.DOOR),
		STORAGE(40, 7, "â��",Material.CRAFTING_TABLE, Material.ORANGE_WOOL, SaboType.DOOR),
		COMM(51, -1, "��Ž�",Material.SOUL_TORCH, Material.WHITE_WOOL, SaboType.COMM);

		private final int slot;
		private final int sabo_room_id;
		private final String roomname;
		private final Material mat;
		private final Material roomblock;
		private final SaboType[] st;
		private static HashMap<Integer, ROOMS> slots = new HashMap<Integer, ROOMS>();
		private HashSet<String> playerSET;

		ROOMS(int slot, int sabo_room_id, String roomname, Material mat, Material block, SaboType... st) {
			this.slot = slot;
			this.sabo_room_id = sabo_room_id;
			this.mat = mat;
			this.st = st;
			this.roomname = roomname;
			this.roomblock = block;
			this.playerSET = new HashSet<String>();
		}

		public static ROOMS getROOM(int slotnum) {
			return slots.get(slotnum);
		}

		public static ROOMS getROOMByBlock(Material roomblock) {
			for (ROOMS r : ROOMS.values()) {
				if (roomblock == r.getRoomblock())
					return r;
			}
			return null;
		}
		
		public static ROOMS getROOMBySignature(Material roomblock) {
			for (ROOMS r : ROOMS.values()) {
				if (roomblock == r.getMat())
					return r;
			}
			return null;
		}
		
		public static ROOMS getROOMBySaboType(SaboType st) {
			for (ROOMS r : ROOMS.values()) {
				if (r.getSabos().length > 0 && (st == r.getSabos()[0] || (r.getSabos().length > 1 && st == r.getSabos()[1])))
					return r;
			}
			return null;
		}
		
		public static ROOMS getROOMBySaboId(int id) {
			for (ROOMS r : ROOMS.values()) {
				if (id == r.getSaboDoorId())
					return r;
			}
			return null;
		}

		public int getSlot() {
			return slot;
		}
		
		public int getSaboDoorId() {
			return sabo_room_id;
		}

		public String getRoomname() {
			return roomname;
		}

		public Material getMat() {
			return mat;
		}
		
		public SaboType[] getSabos() {
			return st;
		}

		public Material getRoomblock() {
			return roomblock;
		}

		public void addPlayer(String player) {
			this.playerSET.add(player);
		}

		public void deletePlayer(String player) {
			this.playerSET.remove(player);
		}

		public HashSet<String> getPlayers() {
			return this.playerSET;
		}
		
		public void resetPlayer() {
			playerSET.clear();
		}

		public static void resetAllplayer(){
			for (ROOMS r : ROOMS.values()) {
				r.resetPlayer();
			}
		}

		static {
			for (ROOMS r : ROOMS.values()) {
				slots.put(r.slot, r);
			}
		}

	}

	public static Inventory initializeGUI(boolean admin) {
		Inventory gui = Bukkit.createInventory(null, maxslot, admin ? guiName : SabotageGUI.guiName);
		for (int slot = 0; slot < maxslot; slot++) {
			if (ROOMS.slots.containsKey(slot)) {
				ROOMS room = ROOMS.getROOM(slot);

				if(admin) {
					Util.Stack(gui, slot, room.getMat(), 1, "��f" + room.getRoomname(), "��7�����ο� : 0");
				} else {
					SaboType[] stlist = room.getSabos();
					if(stlist == null || stlist.length == 0) {
						Util.Stack(gui, slot, Material.BARRIER, 1, "��f" + room.getRoomname(), "��c�ߵ��� �� �ִ� �纸Ÿ���� �����ϴ�.");
					} else {
						
						if(stlist.length > 1) {
							Util.Stack(gui, slot, room.getMat(), 1, "��f" + room.getRoomname(), Arrays.asList("��e��Ŭ�� - ��f�� �ݱ�", "��e��Ŭ�� - ��c���� ����"));
						} else if(stlist[0] == SaboType.DOOR){
							Util.Stack(gui, slot, room.getMat(), 1, "��f" + room.getRoomname(), "��e��Ŭ�� - ��f�� �ݱ�");
						} else {
							Util.Stack(gui, slot, room.getMat(), 1, "��f" + room.getRoomname(), "��e��Ŭ�� - ��c" + (stlist[0] == SaboType.OXYG ? "��� ��" : (stlist[0] == SaboType.COMM ? "��� ����" : "���ڷ� ����")));
						}
						
					}
					
				}
				
				
			} else if (((slot > 0 && slot < 8) || (slot > 36 && slot < 44) || slot % 9 == 1 || slot % 9 == 4
					|| slot % 9 == 7)&&slot<45) {
				Util.Stack(gui, slot, Material.GREEN_STAINED_GLASS_PANE, 1, "��7���");
			}
		}
		if(!admin) {
			gui.setItem(ROOMS.WEAPONS.getSlot(), Util.createItem(Material.BARRIER, 1, ROOMS.WEAPONS.getRoomname(), Arrays.asList("��7�ߵ��� �� �ִ� �纸Ÿ���� �����ϴ�.")));
			gui.setItem(ROOMS.NAVI.getSlot(), Util.createItem(Material.BARRIER, 1, ROOMS.NAVI.getRoomname(), Arrays.asList("��7�ߵ��� �� �ִ� �纸Ÿ���� �����ϴ�.")));
			gui.setItem(ROOMS.SHIELD.getSlot(), Util.createItem(Material.BARRIER, 1, ROOMS.SHIELD.getRoomname(), Arrays.asList("��7�ߵ��� �� �ִ� �纸Ÿ���� �����ϴ�.")));
			gui.setItem(ROOMS.ADMIN.getSlot(), Util.createItem(Material.BARRIER, 1, ROOMS.ADMIN.getRoomname(), Arrays.asList("��7�ߵ��� �� �ִ� �纸Ÿ���� �����ϴ�.")));
		}
		
		return gui;
	}

	public static void openGUI(Player p) {
		if(Sabotage.isActivating(0) && Sabotage.Sabos.getType() == SaboType.COMM) {
			p.sendMessage(Main.PREFIX + "��c��� �纸Ÿ�� �ߵ� �߿� �� �� �����ϴ�.");
			return;
		}
		p.openInventory(gui);
	}

	private static void updateRoom(ROOMS room, int amount) {
		if (amount == 0) {
			Util.Stack(gui, room.getSlot(), room.getMat(), 1, "��f" + room.getRoomname(), "��7�����ο� : " + amount);
		}
		else Util.Stack(gui, room.getSlot(), room.getMat(), amount, "��f" + room.getRoomname(), "��7�����ο� : " + amount, true);
	}

	private static void updateRoom(Material roomBlock, int amount) {
		updateRoom(ROOMS.getROOMByBlock(roomBlock), amount);
	}

	private static void updateRoom(ROOMS room) {
		updateRoom(room, room.getPlayers().size());
	}

	public static void onClick(InventoryClickEvent e) {

		if (!e.getView().getTitle().contains(guiName))
			return;
		e.setCancelled(true);
	}
	
	

	public static void onMove(PlayerMoveEvent e) {
		Player p = e.getPlayer();
		Location l = e.getFrom().clone();
		l.setY(yAxis);
		Material mat = l.getBlock().getType();
		for (ROOMS r : ROOMS.values()) {
			if (!r.getPlayers().contains(p.getName())&&r.getRoomblock() == mat) {
				r.addPlayer(p.getName());
				updateRoom(r);
			} else if (r.getPlayers().contains(p.getName()) && r.getRoomblock() != mat) {
				r.deletePlayer(p.getName());
				updateRoom(r);
			}
		}
	}
}
