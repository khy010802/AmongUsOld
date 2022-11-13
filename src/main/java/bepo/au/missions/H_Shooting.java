package bepo.au.missions;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.SmallFireball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import bepo.au.base.Mission;
import bepo.au.base.TimerBase;
import bepo.au.base.VisualTask;
import bepo.au.manager.LocManager;
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
		
		if(Timer != null && Timer.GetTimerRunning()) {
			Timer.EndTimer();
		}
		Timer = null;
	}
	

	@Override
	public String getScoreboardMessage() {
		
		String s = getKoreanName();
		s = s + "(" + (MaxScore-score) + "/" + MaxScore + ")";
		if (cleared.size() > 0) {
			s = "¡×a" + s;
		} else if(score < MaxScore && score > 0) {
			s = "¡×e" + s;
		}
		return s;
	}

	public void onClear(Player p, int i) {
		generalClear(p, i);
	}
	final int MaxScore = 20;
	public int score = 20;
	int Dot;
	
	ShootingTimer Timer;

	public void shooting(Player p) { // Á¶°Ç ¸¸Á·
		if(Timer==null)	Timer = new ShootingTimer(p);
		while(true) {
			Dot = Util.random(0, 26);
			if(Dot == 8) {
				Dot = Util.random(0, 26);
			}
			else {
				break;
			}
		}
		gui.get(0).clear();
		Util.Stack(gui.get(0), 8, Material.OAK_SIGN, score, " ");
		Util.Stack(gui.get(0), Dot, Material.FIREWORK_STAR, 1, " ");
		Timer.StartTimer(-1, false, 1);// ·£´ýÇÑ Àå¼Ò¿¡ »¡°­¾çÅÐ »ý¼º
		p.openInventory(gui.get(0));
	}

	@EventHandler
	public void Click(InventoryClickEvent e) {

		if(!(checkPlayer(e))) return;
		e.setCancelled(true);
		Inventory inv = e.getClickedInventory();
		Player p = (Player) e.getWhoClicked();

		if (e.getCurrentItem() != null && e.getCurrentItem().getType() == Material.FIREWORK_STAR) { 
			score--;
			Util.Stack(gui.get(0), 8, Material.OAK_SIGN, score, " ");
			p.playSound(p.getLocation(), Sound.BLOCK_BEACON_DEACTIVATE, 1, 2);
			inv.setItem(e.getSlot(), new ItemStack(Material.AIR)); // Å¬¸¯ÇÑ ¾çÅÐÀÌ »ç¶óÁü
			if(score <= 0) {
				onClear(p, 0);
			}
		}

	}
	
	public class ShootingTimer extends TimerBase {
		
		private VisualTask vt;
		private Player P;
		private int timer = 0;
		
		public ShootingTimer(Player p) {
			this.P = p;
		}

		@Override
		public void EventStartTimer() {
			vt = new VisualTask() {

				private List<Location> locs;
				private List<Location> dirs;
				private List<SmallFireball> sfb_list = new ArrayList<SmallFireball>();
				
				@Override
				public void onStart() {
					locs = LocManager.getLoc("Shooting_FIREBALL");
					dirs = LocManager.getLoc("Shooting_FIREBALL_DIR");
					if((locs == null || locs.size() == 0) || (dirs == null || dirs.size() == 0)) this.Finish(true);
				}

				@Override
				public void onTicking(int count) {
					if(count % 20 == 0) {
						int temp = (count / 20) % locs.size();
						Location loc = locs.get(temp);
						Location dir;
						if(dirs.size() > temp)
							dir = dirs.get(temp);
						else
							dir = dirs.get(0);
						
						SmallFireball sfb = (SmallFireball) loc.getWorld().spawnEntity(loc, EntityType.SMALL_FIREBALL);
						sfb.setDirection(dir.toVector().subtract(loc.toVector()).normalize());
						//sfb.getWorld().playSound(loc, Sound.ENTITY_BLAZE_SHOOT, 0.3F, 1.0F);
						sfb_list.add(sfb);
					}
				}

				@Override
				public void onFinished() {
					
				}

				@Override
				public void Reset() {
					
					if(!sfb_list.isEmpty()) {
						for(SmallFireball sfb : sfb_list) {
							if(sfb != null && sfb.isValid() && !sfb.isDead()) sfb.remove();
						}
					}
					
				}
				
			};
			vt.StartTimer(P, false);
		}
		
		@Override
		public void EventRunningTimer(int count) {
			
			if(P == null) {
				EndTimer();
				return;
			}
			
			if (P != null && !P.getOpenInventory().getTitle().contains("Shooting")) {
				EndTimer();
				return;
			}
			timer++;
			if(timer % 15 == 0) {
				while(true) {
					Dot = Util.random(0, 26);
					if(Dot == 8) {
						Dot = Util.random(0, 26);
					}
					else {
						break;
					}
				}
				Util.Stack(gui.get(0), Dot, Material.FIREWORK_STAR, 1, " ");
			}
			
			
		}

		@Override
		public void EventEndTimer() {
			vt.Finish(false);
		}
		
	}

}