package bepo.au.missions;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;

import bepo.au.base.Mission;
import bepo.au.base.TimerBase;


public class H_Gas extends Mission {

	public H_Gas(MissionType mt2, String name, String korean, int clear, Location loc) {
		super(true, mt2, name, korean, clear, loc);
	}

	public void onAssigned(Player p) {
		assign(p);
		uploadInventory(p, 54, "Gas 0");
		uploadInventory(p, 54, "Gas 1");
	}

	public void onStart(Player p, int i) {
		if (i == 0)
			gas1(p);
		else if (cleared.contains((Integer) i))
			gas2(p);
	}

	public void onStop(Player p, int i) {
		if(Timer.GetTimerRunning()) Timer.StopTimer();
	}

	public void onClear(Player p, int i) {
		generalClear(p, i);
	}
	
	GasTimer Timer;

	public void gas1(Player p) {
		ItemStack RED_STAINED_GLASS_PANE = new ItemStack(Material.RED_STAINED_GLASS_PANE);
		for (int i = 1; i < 6; i++) {
			gui.get(0).setItem(getCoordinate(1, i), RED_STAINED_GLASS_PANE);
			gui.get(0).setItem(getCoordinate(5, i), RED_STAINED_GLASS_PANE);
		}
		gui.get(0).setItem(getCoordinate(2, 5), RED_STAINED_GLASS_PANE);
		gui.get(0).setItem(getCoordinate(3, 5), RED_STAINED_GLASS_PANE);
		gui.get(0).setItem(getCoordinate(4, 5), RED_STAINED_GLASS_PANE);
		gui.get(0).setItem(getCoordinate(2, 0), RED_STAINED_GLASS_PANE);
		gui.get(0).setItem(getCoordinate(4, 0), RED_STAINED_GLASS_PANE);
		gui.get(0).setItem(getCoordinate(7, 4), new ItemStack(Material.GRAY_CONCRETE));

		p.openInventory(gui.get(0));
	}

	public void gas2(Player p) {
		ItemStack WHITE_STAINED_GLASS_PANE = new ItemStack(Material.WHITE_STAINED_GLASS_PANE);
		for (int i = 1; i < 6; i++) {
			gui.get(1).setItem(getCoordinate(3, i), WHITE_STAINED_GLASS_PANE);
			gui.get(1).setItem(getCoordinate(5, i), WHITE_STAINED_GLASS_PANE);
		}
		gui.get(1).setItem(getCoordinate(4, 5), WHITE_STAINED_GLASS_PANE);
		gui.get(1).setItem(getCoordinate(7, 4), new ItemStack(Material.GRAY_CONCRETE));
		ItemStack potion = new ItemStack(Material.POTION);
		PotionMeta mateP = (PotionMeta) potion.getItemMeta();
		mateP.setColor(Color.YELLOW);
		potion.setItemMeta(mateP);
		gui.get(1).setItem(getCoordinate(5, 0), potion);
		p.openInventory(gui.get(1));
	}

	@EventHandler
	public void Click(InventoryClickEvent e) {
		
		if(!checkPlayer(e)) return;
		
		if (getCode(e.getView().getTitle()) == 0) {
			
			if (e.getCurrentItem().getType() == Material.GRAY_CONCRETE) {
				e.setCancelled(true);
				startTimer(0);
			} else {
				e.setCancelled(true);
			}
		} else if (getCode(e.getView().getTitle()) == 1) {
			if (e.getCurrentItem().getType() == Material.GRAY_CONCRETE) {
				e.setCancelled(true);
				startTimer(1);
			} else {
				e.setCancelled(true);
			}
		}

	}
	
	public void startTimer(int c) {
		Timer = new GasTimer();
		Timer.setCase(c);
		Timer.StartTimer(4, true);
	}

	public int getCoordinate(int x, int y) {
		return x + 9 * y;
	}

	public final class GasTimer extends TimerBase {

		private int Case = -1;
		
		public void setCase(int i) { this.Case = i; }
		
		@Override
		public void EventStartTimer() {
			// TODO Auto-generated method stub

		}

		@Override
		public void EventRunningTimer(int count) {
			
			if(getPlayer() == null) {
				StopTimer();
				return;
			}
			
			if (Case == 0) {
				if (!(count == 0)) {
					for (int i = 2; i < 5; i++) {
						gui.get(0).setItem(getCoordinate(i, count), new ItemStack(Material.YELLOW_STAINED_GLASS_PANE));
					}
				}
			}
			if (Case == 1) {
				if (!(count == 0)) {
					gui.get(1).setItem(getCoordinate(4, count), new ItemStack(Material.YELLOW_STAINED_GLASS_PANE));
				}
				if (count == 1) {
					gui.get(1).setItem(getCoordinate(5, 0), new ItemStack(Material.GLASS_BOTTLE));
				}
			}
		}

		@Override
		public void EventEndTimer() {
			// TODO Auto-generated method stub
			
			if(getPlayer() != null) {
				getPlayer().closeInventory();
				onClear(getPlayer(), Case);
			}
			
			
		}

	}
}
