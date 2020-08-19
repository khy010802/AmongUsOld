package bepo.au.sabo;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Sabo_Oxygen implements Listener {
	
	int Oxygen_password = 0;
	int Oxygen_answer = 0;
	int Oxygen_count = 0;
	CustomRandom random = new CustomRandom();
	

	public void sabo_Oxygen(Player p) {
		Oxygen_answer = 0;
		Oxygen_count = 5;
		Oxygen_password = random.random(10000, 99999); //패스워드 지정 10000~99999
		Inventory inv = Bukkit.createInventory(p, 36, "Oxygen"); { //gui 생성 0 부터 9까지의 10개의 버튼
			int number = 1;
			for(int i = 3; i >= 0; i--) {
				for(int j = 0; j < 9; j++) {
					if(j >= 1 && j <= 3 && i < 3) {
						ItemStack item = new ItemStack(Material.WHITE_WOOL);
						ItemMeta meta = item.getItemMeta();
						meta.setDisplayName("§f" + number);
						item.setItemMeta(meta);
						inv.setItem(j+9*i, new ItemStack(item));
						number++;
					}
					else if(j == 3) {
						Util.Stack(inv, j+9*i, Material.BLACK_STAINED_GLASS_PANE, 1, " ");
					}
					else if(j <= 4) {
						Util.Stack(inv, j+9*i, Material.WHITE_STAINED_GLASS_PANE, 1, " ");
					}
				}
			}
			ItemStack item = new ItemStack(Material.RED_WOOL);
			ItemMeta meta = item.getItemMeta();
			meta.setDisplayName("§c" + "CANCEL");
			item.setItemMeta(meta);
			inv.setItem(28, new ItemStack(item));
			
			item = new ItemStack(Material.WHITE_WOOL);
			meta = item.getItemMeta();
			meta.setDisplayName("§f" + 0);
			item.setItemMeta(meta);
			inv.setItem(29, new ItemStack(item));
			
			item = new ItemStack(Material.PAPER);
			meta = item.getItemMeta();
			meta.setDisplayName("§f" + Oxygen_password);
			item.setItemMeta(meta);
			inv.setItem(16, new ItemStack(item));
		}
		p.openInventory(inv);
		
	}
	
	@EventHandler
	public void Click(InventoryClickEvent e) {
		Player p = (Player) e.getWhoClicked();
		if(e.getView().getTitle().equals("Oxygen") && e.getCurrentItem().getType() == Material.WHITE_WOOL) {  //흰 양털을 클릭하면 양털의 이름을 가져옴
			e.setCancelled(true);
			p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 10, 1);
			String numstr = e.getCurrentItem().getItemMeta().getDisplayName(); 								
			int num = Integer.parseInt(numstr.substring(2));
			switch (Oxygen_count) {				//양털의 이름을 num에 저장 저장할때마다 count가 1씩 줄어듬
			case 1:
				Oxygen_answer += num;
				Oxygen_count--;
				if(Oxygen_answer == Oxygen_password) {
					p.sendMessage("clear");
					p.closeInventory();
					return;
				}
				else {
					p.sendMessage("failed");
					p.closeInventory();
					return;
				}
			case 2:
				Oxygen_answer += num * 10;
				Oxygen_count--;
				break;
			case 3:
				Oxygen_answer += num * 100;
				Oxygen_count--;
				break;
			case 4:
				Oxygen_answer += num * 1000;
				Oxygen_count--;
				break;
			case 5:
				Oxygen_answer = num * 10000;
				Oxygen_count--;
				break;
			}
		}
		if(e.getView().getTitle().equals("Oxygen") && e.getCurrentItem().getType() == Material.RED_WOOL) {
			e.setCancelled(true);
			p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1);
			Oxygen_answer = 0;
			Oxygen_count = 5;
		}
		if(e.getView().getTitle().equals("Oxygen") && e.getCurrentItem().getType() == Material.PAPER) {
			e.setCancelled(true);
		}
		
	}

}
