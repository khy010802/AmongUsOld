package bepo.au.missions;

import java.util.Arrays;

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

import bepo.au.base.Mission;
import bepo.au.base.TimerBase;
import bepo.au.base.Mission.MissionType;
import bepo.au.utils.Util;

public class H_Shooting extends Mission {

	public H_Shooting(MissionType mt2, String name, String korean, int clear, Location loc) {
		super(false, mt2, name, korean, clear, loc);
	}

	public void onAssigned(Player p) {
		assign(p);
		uploadInventory(p, 27, "Shooting");
	}

	public void onStart(Player p, int i) {
		shooting(p);
	}

	public void onStop(Player p, int i) {
		if(Timer != null && Timer.GetTimerRunning()) Timer.StopTimer();
		Timer = null;
	}

	public void onClear(Player p, int i) {
		generalClear(p, i);
	}
	
	int score;
	Inventory Inv;
	int Dot;
	
	ShootingTimer Timer;

	public void shooting(Player p) { // Á¶°Ç ¸¸Á·
		int dot = Util.random(0, 26);
		gui.get(0).clear();
		Util.Stack(gui.get(0), dot, Material.FIREWORK_STAR, 1, " "); // Å¬¸¯ÇÑ ¾çÅÐÀÌ »ç¶óÁü
		p.openInventory(gui.get(0));
	}

	@EventHandler
	public void Click(InventoryClickEvent e) {

		if(!(checkPlayer(e))) return;
		
		Inventory inv = e.getClickedInventory();
		Player p = (Player) e.getWhoClicked();

		if (e.getCurrentItem() != null && e.getCurrentItem().getType() == Material.FIREWORK_STAR) { 
			e.setCancelled(true);
			int dot = e.getSlot();
			score++;
			p.playSound(p.getLocation(), Sound.BLOCK_BEACON_DEACTIVATE, 1, 2);
			inv.setItem(dot, new ItemStack(Material.AIR)); // Å¬¸¯ÇÑ ¾çÅÐÀÌ »ç¶óÁü
			Dot = Util.random(0, 26);
			Timer = new ShootingTimer();
			Timer.StartTimer(1);// ·£´ýÇÑ Àå¼Ò¿¡ »¡°­¾çÅÐ »ý¼º
		}
	}
	
	public final class ShootingTimer extends TimerBase {

		@Override
		public void EventStartTimer() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void EventRunningTimer(int count) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void EventEndTimer() {
			// TODO Auto-generated method stub
			Util.Stack(gui.get(0), Dot, Material.FIREWORK_STAR, 1, " ");
		}
		
	}

}