package bepo.au.missions;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

public class H_Shooting implements Listener {

	int score = 0;
	CustomRandom random = new CustomRandom();

	public void shooting(Player p) { // 조건 만족
		int dot = random.random(0, 26);
		Inventory inv = Bukkit.createInventory(p, 27, "Shooting");
		Util.Stack(inv, dot, Material.FIREWORK_STAR, 1, " "); // 클릭한 양털이 사라짐
		p.openInventory(inv);
	}

	@EventHandler
	public void Click(InventoryClickEvent e) {

		Inventory inv = e.getClickedInventory();
		Player p = (Player) e.getWhoClicked();

		if (e.getView().getTitle().equals("Shooting") && e.getCurrentItem().getType() == Material.FIREWORK_STAR) { 
			e.setCancelled(true);
			int dot = e.getSlot();
			score++;
			p.playSound(p.getLocation(), Sound.BLOCK_BEACON_DEACTIVATE, 1, 2);
			Util.Stack(inv, dot, Material.AIR, 1, " "); // 클릭한 양털이 사라짐
			dot = random.random(0, 26);
			Util.Stack(inv, dot, Material.FIREWORK_STAR, 1, " "); // 랜덤한 장소에 빨강양털 생성
		}
	}

}