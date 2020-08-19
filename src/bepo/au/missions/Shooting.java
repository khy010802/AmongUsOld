package bepo.au.missions;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class Shooting implements Listener {

	int score = 0;
	CustomRandom random = new CustomRandom();

	public void shooting(Player p) { // ���� ����
		int dot = random.random(0, 26);
		Inventory inv = Bukkit.createInventory(p, 27, "Shooting");
		inv.setItem(dot, new ItemStack(Material.RED_WOOL));
		p.openInventory(inv);
	}

	@EventHandler
	public void Click(InventoryClickEvent e) {

		Inventory inv = e.getClickedInventory();
		Player p = (Player) e.getWhoClicked();

		if (e.getView().getTitle().equals("Shooting") && e.getCurrentItem().getType() == Material.RED_WOOL) { // ���� ������
																												// Ŭ���ϸ�
			e.setCancelled(true);
			int dot = e.getSlot();

			score++;
			p.sendMessage("" + score);
			inv.setItem(dot, new ItemStack(Material.AIR)); // Ŭ���� ������ �����
			dot = random.random(0, 26);
			inv.setItem(dot, new ItemStack(Material.RED_WOOL)); // ������ ��ҿ� �������� ����
		}
	}

}