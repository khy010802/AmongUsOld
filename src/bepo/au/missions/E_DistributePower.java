package bepo.au.missions;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CompassMeta;
import org.bukkit.scheduler.BukkitRunnable;

import bepo.au.Main;
import bepo.au.base.Mission;
import bepo.au.base.TimerBase;
import bepo.au.utils.Util;


public class E_DistributePower extends Mission{
	
	public E_DistributePower(MissionType mt2, String name, String korean, int clear, Location loc) {
		super(mt2, name, korean, clear, loc);
	}
	
	public void onAssigned(Player p) {
		assign(p);
		uploadInventory(p, 45, "DistributePower");
	}
	
	public void onStart(Player p, int i) {
		distributePower(p);
	}
	
	public void onStop(Player p, int i) {
		p.getInventory().remove(Material.ELYTRA);
	}
	
	public void onClear(Player p, int i) {
		generalClear(p, i);
	}
	
	PowerTimer Timer;
	
	int Case;
	double Tmp;
	double angle;
	final int t = 24;
	double a;
	double b;
	
	public void distributePower(Player p) {
		Case = 0;
		
		
        for (int slot = 0; slot < 45; slot++) {
        	if(slot == 2) {
        		Util.Stack(gui.get(0), slot, Material.YELLOW_STAINED_GLASS_PANE, 1, " ");
        	}
        	else if(slot > 2 && slot < 6) {
        		;
        	}
        	else if(slot == 20) {
        		Util.Stack(gui.get(0), slot, Material.BLUE_STAINED_GLASS_PANE, 1, " ");
        	}
        	
        	else if(slot > 20 && slot < 24) {
        		;
        	}
        	else if(slot == 38) {
        		Util.Stack(gui.get(0), slot, Material.LIGHT_BLUE_STAINED_GLASS_PANE, 1, " ");
        	}
        	
        	else if(slot > 38 && slot < 42) {
        		;
        	}
        	
        	else if(slot == 7 || slot == 25 || slot == 43) {
        		Util.Stack(gui.get(0), slot, Material.REDSTONE_BLOCK, 1, " ");
            }
            else {
                Util.Stack(gui.get(0), slot, Material.GRAY_STAINED_GLASS_PANE, 1, " ");
            }

        }
        
		Tmp = (Util.random(-6, 6));
		a = Math.PI + (p.getLocation().getYaw() / 180 * Math.PI);
		b = a%(2*Math.PI);
		for(int i = 0 ; i < 3; i++) {
			Case = i;
			SetCompass(p, 0);
		}
		Case = 0;
		Timer = new PowerTimer();
		Timer.StartTimer(2*t, true, 2);
		p.openInventory(gui.get(0));
	}
	
	@EventHandler
	public void Click(InventoryClickEvent e) {
		
		if(!checkPlayer(e)) return;
		
		Player P = (Player) e.getWhoClicked();
		Inventory Inv = e.getInventory();
			if(true) {
				if(e.getCurrentItem().getType() == Material.REDSTONE_BLOCK) {
					e.setCancelled(true);
					if(angle < b+Math.PI/12 && angle > b-Math.PI/12) {
						if(e.getRawSlot()/9/2 == Case++) {
							Material M = Material.YELLOW_STAINED_GLASS_PANE;
							Timer.StopTimer();
							Timer.StartTimer(2*t, true, 2);
							if(Case == 1) {
								M = Material.YELLOW_STAINED_GLASS_PANE;
							}
							if(Case == 2) {
								M = Material.BLUE_STAINED_GLASS_PANE;
							}
							if(Case == 3) {
								M = Material.LIGHT_BLUE_STAINED_GLASS_PANE;
								Timer.StopTimer();
								P.closeInventory();
								onClear(P, 0);
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
	
	public final class PowerTimer extends TimerBase {

		@Override
		public void EventStartTimer() {
			// TODO Auto-generated method stub
		}

		@Override
		public void EventRunningTimer(int count) {
			
			Player P = getPlayer();
			
			if(P == null) {
				StopTimer();
				return;
			}

			if(!(P.getOpenInventory().getTitle().equals("DistributePower"))) {
				Timer.StopTimer();
				Case = 0;
			}
			
			SetCompass(P, count);
			
			
		}

		@Override
		public void EventEndTimer() {
			// TODO Auto-generated method stub
			
		}
		
	}
	public void SetCompass(Player P, int count) {
		ItemStack item = new ItemStack(Material.COMPASS);
		CompassMeta meta = (CompassMeta) item.getItemMeta();
		double size = 10;
		angle = (((Math.PI/t) * (count+Tmp)) + (a - Math.PI))%(2*Math.PI);
		double x = size * Math.cos(angle);
		double z = size * Math.sin(angle);
		Location loc = P.getLocation().add(x, 1, z);
		meta.setLodestone(loc);
		item.setItemMeta(meta);
		gui.get(0).setItem(2+18*Case, item);
		//P.sendMessage("" + angle + "/" + b);
		
	}
	
	
}

