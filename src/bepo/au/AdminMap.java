package bepo.au;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import bepo.au.utils.ColorUtil;
import bepo.au.utils.Util;
import net.minecraft.server.v1_16_R2.SlotShulkerBox;

@SuppressWarnings("unused")
public class AdminMap implements Listener {
	private HashMap<String, Inventory> guiMap;
	private final int maxslot = 54;
	public final String guiName = "������ ����";
	private enum ROOMS{
		CAFE(4,"�Ĵ�",Material.ENCHANTING_TABLE, Material.RED_WOOL),
		UE(1,"��� ����",Material.DISPENSER, Material.BLUE_WOOL),
		MEDBEY(12,"�ǹ���",Material.POTION, Material.LIME_WOOL),
		O2(15,"��� ���޽�",Material.KELP, Material.GREEN_WOOL),
		WEAPONS(7,"�����",Material.FIREWORK_ROCKET, Material.BROWN_WOOL),
		REACTOR(18,"���ڷ�",Material.CRYING_OBSIDIAN, Material.BLACK_WOOL),
		SECU(20,"���Ƚ�",Material.ENDER_EYE, Material.MAGENTA_WOOL),
		ADMIN(23,"������",Material.MAP, Material.PURPLE_WOOL),
		NAVI(26,"���ؽ�",Material.LODESTONE, Material.GRAY_WOOL),
		LE(37,"�Ϻο���",Material.DISPENSER, Material.LIGHT_BLUE_WOOL),
		ELEC(30,"�����",Material.COMPARATOR, Material.YELLOW_WOOL),
		SHIELD(43,"��ȣ�� �����",Material.BEACON, Material.CYAN_WOOL),
		STORAGE(40,"â��",Material.CHEST, Material.ORANGE_WOOL),
		COMM(51,"��Ž�",Material.SOUL_TORCH, Material.WHITE_WOOL);
		
		
		private final int slot;
		private final String roomname;
		private final Material mat;
		private final Material roomblock;
		private static HashMap<Integer,ROOMS> slots = new HashMap<Integer,ROOMS>();
		
		private ROOMS(int slot, String roomname, Material mat, Material block) {
			this.slot=slot;
			this.mat=mat;
			this.roomname=roomname;
			this.roomblock=block;
		}
		public static ROOMS getROOM(int slotnum) {
			return slots.get(slotnum);
		}
		
		public int getSlot() {
			return slot;
		}
		public String getRoomname() {
			return roomname;
		}

		public Material getMat() {
			return mat;
			
		}
		
		public Material getRoomblock() {
			return roomblock;
		}

		static {
			for (ROOMS r : ROOMS.values()) {
				slots.put(r.slot,r);
			}
		}
		
	}
	
	public void initializeGUI(Player p) {
		String name = p.getName();
		if (!guiMap.containsKey(name)) { guiMap.put(name, Bukkit.createInventory(p, maxslot)); }//Ű������ �����
		for (int slot = 0 ; slot<maxslot; slot++) {
			if (ROOMS.slots.containsKey(slot)){
				ROOMS room = ROOMS.getROOM(slot);
				Util.Stack(guiMap.get(name), slot, room.getMat(), 1, "��f"+room.getRoomname(),"��7�����ο� : 0");
			}
			else if((slot>0&&slot<8)||(slot>36&&slot<44)||slot%9==1||slot%9==4||slot%9==7){
					Util.Stack(guiMap.get(name), slot, Material.GREEN_STAINED_GLASS_PANE, 1, "��7���");
			}
			
		}
	}
	private void updateRoom(Player p, ROOMS room, int amount) {
		if (amount==0) {
			Util.Stack(guiMap.get(p.getName()), room.getSlot(), room.getMat(), amount, "��f"+room.getRoomname(), "��7�����ο� : "+amount);
		}
		Util.Stack(guiMap.get(p.getName()), room.getSlot(), room.getMat(), amount, "��f"+room.getRoomname(),"��7�����ο� : "+amount,true);
	}
	
	public void onClick(InventoryClickEvent e) {
		
		if(!e.getView().getTitle().contains(guiName)) return;
		e.setCancelled(true);
		
	}
}
