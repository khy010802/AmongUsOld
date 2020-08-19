package bepo.au.missions;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import Mission.CustomRandom;
import Mission.Util;

public class E_ActivatingShield implements Listener {
	
	int Shield_score = 0;
	CustomRandom random = new CustomRandom();
	
	
	public void activatingShield(Player p) { // 미션 시작
		Shield_score = 0;
		Inventory inv = Bukkit.createInventory(p, 27, "Activating_Shield"); //gui 생성
		int a = random.random(0, 1);
		for(int i = 0; i < 3; i++) {
			for(int j = 0; j < 9; j++) {
				if(j >= 3 && j <= 5) {
					a = random.random(0, 1);
					if(a == 0) {
			    		Util.Stack(inv, j+9*i, Material.RED_STAINED_GLASS_PANE, 1, " ");
					}
					else {
						Util.Stack(inv, j+9*i, Material.WHITE_STAINED_GLASS_PANE, 1, " ");
						Shield_score++;
					}
				}
				else {
					Util.Stack(inv, j+9*i, Material.GRAY_STAINED_GLASS_PANE, 1, " ");
				}
			}
		}
		p.openInventory(inv);
	}
	
	@EventHandler
	public void Click(InventoryClickEvent e) {
		
		Inventory inv = e.getClickedInventory();
		int dot = e.getSlot();
		Player p = (Player) e.getWhoClicked();
		
		if(e.getView().getTitle().equals("Activating_Shield") && e.getCurrentItem().getType() == Material.RED_STAINED_GLASS_PANE) {  // 빨강 양털을 클릭하면 파란양털로 변경 이후 점수 증가 
			e.setCancelled(true);
			Util.Stack(inv, dot, Material.WHITE_STAINED_GLASS_PANE, 1, " ");
			p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 10, 1);
			Shield_score++;
			if(Shield_score >= 9) {      								//점수가 9점에 도달하면 gui가 닫히고 테스크 성공
				p.closeInventory();
				p.sendMessage("Clear");
				return;
			}
		}
		else if(e.getView().getTitle().equals("Activating_Shield") && e.getCurrentItem().getType() == Material.WHITE_STAINED_GLASS_PANE) {  //파란 양털을 클릭하면 빨강양털로 변경 이후 점수 감소
			e.setCancelled(true);
			Util.Stack(inv, dot, Material.RED_STAINED_GLASS_PANE, 1, " ");
			p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 10, 1);
			Shield_score--;
		}
		else if(e.getView().getTitle().equals("Activating_Shield") && e.getCurrentItem().getType() == Material.BLUE_STAINED_GLASS_PANE) {
			e.setCancelled(true);
		}
		
	}
	

}
