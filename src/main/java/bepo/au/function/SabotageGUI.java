package bepo.au.function;


import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import bepo.au.GameTimer;
import bepo.au.Main;
import bepo.au.GameTimer.Status;
import bepo.au.base.PlayerData;
import bepo.au.base.Sabotage;
import bepo.au.base.Sabotage.SaboType;
import bepo.au.function.AdminMap.ROOMS;
import bepo.au.utils.Util;

public class SabotageGUI {
	
	private static SabotageGUITimer sgt;
	private static Inventory gui;
	public final static String guiName = "사보타지 GUI";
	
	
	public static void startTimer() {
		stopTimer();
		gui = AdminMap.initializeGUI(false);
		sgt = new SabotageGUITimer();
		sgt.runTaskTimer(Main.getInstance(), 0L, 1L);
	}
	
	public static void stopTimer() {
		if(sgt != null && !sgt.isCancelled()) sgt.cancel();
		sgt = null;
	}
	
	public static void onClick(InventoryClickEvent event) {
		if(!(event.getWhoClicked() instanceof Player)) return;
		
		Player p = (Player) event.getWhoClicked();
		ItemStack is = event.getCurrentItem();
		
		if(!GameTimer.IMPOSTER.contains(p.getName()) || is == null) return;
	
		if(event.getClickedInventory().getType() == InventoryType.PLAYER) {
			if(is.getType() == ItemList.I_SABOTAGE_GUI.getType()) {
				p.openInventory(gui);
				event.setCancelled(true);
			}
		} else if(event.getView().getTitle() != null && event.getView().getTitle().equals(guiName)){
			
			Material mat = is.getType();
			ROOMS room = ROOMS.getROOMBySignature(mat);
			boolean crit = event.isRightClick();
			
			PlayerData pd = PlayerData.getPlayerData(p.getName());
			
			if(room != null && Main.gt.getStatus() == Status.WORKING) {
				if(pd.getVent() != null) {
					p.sendMessage(Main.PREFIX + "��c��Ʈ ���ο��� �纸Ÿ���� �ߵ��� �� �����ϴ�.");
				} else {
					int id = room.getSaboDoorId();
					SaboType[] stlist = room.getSabos();
					
					if(stlist.length > 1) {
						pd.setSabo(p, crit ? stlist[1] : stlist[0], id, !crit);
					} else if(stlist[0] == SaboType.DOOR && !crit) {
						if(event.getRawSlot() == 37) id = 3;
						pd.setSabo(p, stlist[0], id, true);
					} else if(crit) {
						pd.setSabo(p, stlist.length > 1 ? stlist[1] : stlist[0], id, false);
					}
					
						
					Sabotage.saboActivate(p);
				}
				
			}
			
			event.setCancelled(true);
		}
	}
	
	public static class SabotageGUITimer extends BukkitRunnable {
		
		public void run() {
			for(int i=1;i<8;i++) {
				int slot = ROOMS.getROOMBySaboId(i).getSlot();
				ItemStack is = gui.getItem(slot);
				if(Sabotage.Sabo_Cool[i] > 0) {
					if(Sabotage.Remain_Tick[i] > 0 && !is.containsEnchantment(Enchantment.LURE)) Util.enchantItem(is);
					else if(Sabotage.Remain_Tick[i] == 0 && is.containsEnchantment(Enchantment.LURE)) Util.disenchantItem(is);
					is.setAmount(Sabotage.Sabo_Cool[i] / 20 + 1);
				}
			}
			
			for(SaboType st : SaboType.values()) {
				if(st == SaboType.DOOR) continue;
				
				ROOMS r = ROOMS.getROOMBySaboType(st);
				ItemStack is = gui.getItem(r.getSlot());
				if(Sabotage.Sabo_Cool[0] > 0) {
					if(Sabotage.Remain_Tick[0] > 0 && !is.containsEnchantment(Enchantment.LURE)) Util.enchantItem(is);
					else if(Sabotage.Remain_Tick[0] == 0 && is.containsEnchantment(Enchantment.LURE)) Util.disenchantItem(is);
					is.setAmount(Sabotage.Sabo_Cool[0] / 20 + 1);
				}
			}
		}
		
	}

}
