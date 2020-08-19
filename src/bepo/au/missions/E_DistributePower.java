package bepo.au.missions;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CompassMeta;
import org.bukkit.scheduler.BukkitRunnable;


public class E_DistributePower implements Listener{
	
	PowerTimer Timer = new PowerTimer();
	
	Player P;
	Inventory Inv;
	int Case;
	double Tmp;
	double angle;
	CustomRandom Random = new CustomRandom();
	final int t = 24;
	double a;
	double b;
	
	public void distributePower(Player p) {
		P = p;
		Inventory inv = Bukkit.createInventory(p, 45, "DistributePower");
		Inv = inv;
		Case = 0;
		
		
        for (int slot = 0; slot < 45; slot++) {
        	if(slot == 2) {
        		Util.Stack(inv, slot, Material.YELLOW_STAINED_GLASS_PANE, 1, " ");
        	}
        	else if(slot > 2 && slot < 6) {
        		;
        	}
        	else if(slot == 20) {
        		Util.Stack(inv, slot, Material.BLUE_STAINED_GLASS_PANE, 1, " ");
        	}
        	
        	else if(slot > 20 && slot < 24) {
        		;
        	}
        	else if(slot == 38) {
        		Util.Stack(inv, slot, Material.LIGHT_BLUE_STAINED_GLASS_PANE, 1, " ");
        	}
        	
        	else if(slot > 38 && slot < 42) {
        		;
        	}
        	
        	else if(slot == 7 || slot == 25 || slot == 43) {
        		Util.Stack(inv, slot, Material.REDSTONE_BLOCK, 1, " ");
            }
            else {
                Util.Stack(inv, slot, Material.GRAY_STAINED_GLASS_PANE, 1, " ");
            }

        }
        
		Tmp = (Random.random(-6, 6));
		a = Math.PI + (P.getLocation().getYaw() / 180 * Math.PI);
		b = a%(2*Math.PI);
		for(int i = 0 ; i < 3; i++) {
			Case = i;
			SetCompass(0);
		}
		Case = 0;
		Timer.StartTimer(2*t, true, 2);
		p.openInventory(inv);
	}
	
	@EventHandler
	public void Click(InventoryClickEvent e) {
		P = (Player) e.getWhoClicked();
		Inv = e.getInventory();
		if(e.getView().getTitle() == "DistributePower") {
			if(true) {
				if(e.getCurrentItem().getType() == Material.REDSTONE_BLOCK) {
					e.setCancelled(true);
					if(angle < b+Math.PI/12 && angle > b-Math.PI/12) {
						if(e.getRawSlot()/9/2 == Case++) {
							Material M = Material.YELLOW_STAINED_GLASS_PANE;
							Timer.StopTimer();
							Timer.StartTimer(2*t, true, 2);
							P.playSound(P.getLocation(), Sound.BLOCK_NOTE_BLOCK_SNARE, 10, 1);
							if(Case == 1) {
								M = Material.YELLOW_STAINED_GLASS_PANE;
							}
							if(Case == 2) {
								M = Material.BLUE_STAINED_GLASS_PANE;
							}
							if(Case == 3) {
								M = Material.LIGHT_BLUE_STAINED_GLASS_PANE;
								Timer.StopTimer();
								P.sendMessage("Clear");
								P.closeInventory();
							}
							for(int i = e.getRawSlot()-4; i < e.getRawSlot()-1; i++) {
								Util.Stack(Inv, i, M, 1, " ");
							}
							
						}
					}
					
					else {
						P.closeInventory();
						Timer.StopTimer();
						distributePower(P);
					}
				}
				else {
					e.setCancelled(true);
				}
			}
		}
		
	}
	
	public final class PowerTimer extends TimerBase {

		@Override
		public void EventStartTimer() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void EventRunningTimer(int count) {
			if(!(P.getOpenInventory().getTitle() == "DistributePower")) {
				Timer.StopTimer();
				P.sendMessage("Close");
				Case = 0;
			}
			
			SetCompass(count);
			
			
		}

		@Override
		public void EventEndTimer() {
			// TODO Auto-generated method stub
			new BukkitRunnable(){
	            public void run(){
	            	Timer.StartTimer(2*t, true, 2);
	            }
	            
	          }.runTaskLater(Main.main, 0L);
			
		}
		
	}
	public void SetCompass(int count) {
		ItemStack item = new ItemStack(Material.COMPASS);
		CompassMeta meta = (CompassMeta) item.getItemMeta();
		double size = 10;
		angle = (((Math.PI/t) * (count+Tmp)) + (a - Math.PI))%(2*Math.PI);
		double x = size * Math.cos(angle);
		double z = size * Math.sin(angle);
		Location loc = P.getLocation().add(x, 1, z);
		meta.setLodestone(loc);
		item.setItemMeta(meta);
		Inv.setItem(2+18*Case, item);
		
	}
	
	
}

