package bepo.au.missions;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import bepo.au.base.Mission;
import bepo.au.utils.Util;


public class E_ActivatingShield extends Mission {
	
	public E_ActivatingShield(MissionType mt2, String name, String korean, int clear, Location loc) {
		super(mt2, name, korean, clear, loc);
	}
	
	public void onAssigned(Player p) {
		assign(p);
		uploadInventory(p, 27, "ActivatingShield");
	}
	
	public void onStart(Player p, int i) {
		activatingShield(p);
		uploadInventory(p, 36, "ActivatingShield");
	}
	
	public void onStop(Player p, int i) {
		p.getInventory().remove(Material.ELYTRA);
	}
	
	public void onClear(Player p, int i) {
		generalClear(p, i);
	}

	
	int Shield_score = 0;
	
	
	public void activatingShield(Player p) { // 미션 시작
		Shield_score = 0;
		Inventory inv = Bukkit.createInventory(p, 27, "ActivatingShield"); //gui 생성
		int a = Util.random(0, 1);
		for(int i = 0; i < 3; i++) {
			for(int j = 0; j < 9; j++) {
				if(j >= 3 && j <= 5) {
					a = Util.random(0, 1);
					if(a == 0) {
			    		Util.Stack(inv, j+9*i, Material.RED_WOOL, 1, " ");
					}
					else {
						Util.Stack(inv, j+9*i, Material.WHITE_WOOL, 1, " ");
						Shield_score++;
					}
				}
				else {
					Util.Stack(inv, j+9*i, Material.BLUE_STAINED_GLASS_PANE, 1, " ");
				}
			}
		}
		gui.set(0, inv);
		p.openInventory(inv);
	}
	
	@EventHandler
	public void Click(InventoryClickEvent e) {
		
		if(!checkPlayer(e)) return;
		
		Inventory inv = e.getClickedInventory();
		int dot = e.getSlot();
		Player p = (Player) e.getWhoClicked();
		
		if(e.getCurrentItem() != null && e.getCurrentItem().getType() == Material.RED_WOOL) {  // 빨강 양털을 클릭하면 파란양털로 변경 이후 점수 증가 
			e.setCancelled(true);
			Util.Stack(inv, dot, Material.WHITE_WOOL, 1, " ");
			p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 10, 1);
			Shield_score++;
			if(Shield_score >= 9) {      								//점수가 9점에 도달하면 gui가 닫히고 테스크 성공
				p.closeInventory();
				onClear(p, 0);
				return;
			}
		}
		else if(e.getCurrentItem().getType() == Material.WHITE_WOOL) {  //파란 양털을 클릭하면 빨강양털로 변경 이후 점수 감소
			e.setCancelled(true);
			Util.Stack(inv, dot, Material.RED_WOOL, 1, " ");
			p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 10, 1);
			Shield_score--;
		}
		else if(e.getCurrentItem().getType() == Material.BLUE_STAINED_GLASS_PANE) {
			e.setCancelled(true);
		}
		
	}
	

}
