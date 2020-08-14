package bepo.au.missions;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import org.bukkit.inventory.meta.CompassMeta;


public class DistributePower implements Listener{
	
	PowerTimer Timer = new PowerTimer();
	
	Player P;
	Inventory Inv;
	boolean first = true;
	int Case;
	double Tmp;
	double angle;
	final int t = 24;
	double a;
	
	public void distributePower(Player p) {
		P = p;
		Inventory inv = Bukkit.createInventory(p, 45, "DistributePower");
		if(first) {
			Inv = inv;
			p.openInventory(inv);
			Case = 0;
			Tmp = (Util.random(0, 6));
			a = Math.PI + (P.getLocation().getYaw()/ 180 * Math.PI);
			a = Math.abs(a);
			for(int i = 0 ; i < 3; i++) {
				Case = i;
				SetCompass(0);
			}
			Case = 0;
			Inv.setItem(7, new ItemStack(Material.BLACK_CONCRETE));
			Timer.StartTimer(2*t, false, 2);
			first = false;
		}
	}
	
	@EventHandler
	public void Click(InventoryClickEvent e) {
		P = (Player) e.getWhoClicked();
		Inv = e.getInventory();
		if(e.getView().getTitle() == "DistributePower") {
			if(true) {
				if(e.getCurrentItem().getType() == Material.BLACK_CONCRETE) {
					e.setCancelled(true);
					if(angle < a + 0.3 && angle > a - 0.3) {
						Timer.StopTimer();
						Case++;
						Timer.StartTimer(2*t, false, 2);
						if(Case == 3) {
							first = true;
							P.closeInventory();
						}
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
			P.sendMessage("--------------------");
			
		}

		@Override
		public void EventRunningTimer(int count) {
			if(!(P.getOpenInventory().getTitle() == "DistributePower")) {
				Timer.StopTimer();
				first = true;
				P.sendMessage("Close");
			}
			
			SetCompass(count);
			
			
		}

		@Override
		public void EventEndTimer() {
			// TODO Auto-generated method stub
			Timer.StartTimer(2*t, false, 2);
			first = true;
			
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
		P.sendMessage("" + angle + "/" + a);
		
	}
	
	
}
