package bepo.au.missions;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import bepo.au.base.Mission;
import bepo.au.utils.Util;

public class H_Shooting extends Mission {

	int score = 0;
	
	public H_Shooting(MissionType mt2, String name, String korean, int clear, Location loc) {
		super(mt2, name, korean, clear, loc);
	}

	public void onAssigned(Player p) {
		assign(p);
		uploadInventory(p, 27, "Shooting");
	}

	public void onStart(Player p, int i) {
		shooting(p);
	}

	public void onStop(Player p, int i) {
		
	}

	public void onClear(Player p, int i) {
		generalClear(p, i);
	}

	public void shooting(Player p) { // Á¶°Ç ¸¸Á·
		int dot = Util.random(0, 26);
		gui.get(0).setItem(dot, new ItemStack(Material.RED_WOOL));
		p.openInventory(gui.get(0));
	}

	@EventHandler
	public void Click(InventoryClickEvent e) {

		if(!checkPlayer(e)) return;
		
		Inventory inv = e.getClickedInventory();
		Player p = (Player) e.getWhoClicked();

		if (e.getCurrentItem().getType() == Material.RED_WOOL) { // »¡°­ ¾çÅÐÀ»
																												// Å¬¸¯ÇÏ¸é
			e.setCancelled(true);
			int dot = e.getSlot();

			score++;
			
			if(score == 20) {
				onClear(p, 0);
				p.closeInventory();
				return;
			}
			
			inv.setItem(dot, new ItemStack(Material.AIR)); // Å¬¸¯ÇÑ ¾çÅÐÀÌ »ç¶óÁü
			dot  = Util.random(0, 26);
			inv.setItem(dot, new ItemStack(Material.RED_WOOL)); // ·£´ýÇÑ Àå¼Ò¿¡ »¡°­¾çÅÐ »ý¼º
		}
	}

}