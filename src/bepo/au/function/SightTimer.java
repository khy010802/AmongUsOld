package bepo.au.function;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Particle.DustOptions;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import bepo.au.GameTimer;
import bepo.au.Main;
import bepo.au.GameTimer.Status;
import bepo.au.base.PlayerData;
import bepo.au.base.Sabotage;
import bepo.au.base.Sabotage.SaboType;
import bepo.au.utils.ColorUtil;
import bepo.au.utils.PlayerUtil;

public class SightTimer extends BukkitRunnable {
	
	public static SightTimer st;
	
	
	public static void start() {
		st = new SightTimer();
		st.runTaskTimer(Main.getInstance(), 0L, 1L);
	}
	
	public static void stop() {
		if(st == null) return;
		if(!st.isCancelled()) st.cancel();
		for(Player ap : Bukkit.getOnlinePlayers()) {
			PlayerUtil.resetHidden(ap);
		}
		st = null;
	}
	
	public void run() {
		
		if(Main.gt == null) {
			stop();
			return;
		} else if(Main.gt.getStatus() != Status.WORKING) return;
		
		for(Player ap : Bukkit.getOnlinePlayers()) {
			
			PlayerData pd = PlayerData.getPlayerData(ap.getName());
			List<Player> hideParticle = new ArrayList<Player>(Bukkit.getOnlinePlayers());
			double distance = GameTimer.IMPOSTER.contains(ap.getName()) ? Main.IMPOSTER_SIGHT_BLOCK : Main.CREW_SIGHT_BLOCK;
			if(distance == 0) distance += 0.01D;
			else if(distance < 0) continue;
			
			if(pd != null && pd.isAlive()) {
				for(Player p2 : GameTimer.ALIVE_PLAYERS) {
					if(!p2.equals(ap)) {
						if(distanceCalculator(p2.getLocation(), ap.getLocation()) <= distance) {
							if(PlayerUtil.isHidden(ap, p2)) PlayerUtil.showPlayer(ap, p2);
						} else {
							if(!PlayerUtil.isHidden(ap, p2)) PlayerUtil.hidePlayer(ap, p2);
						}
						hideParticle.remove(p2);
					}
				}
				
				for(Player p : hideParticle) {
					drawCircle(p, ap.getLocation(), distance, pd.getColor());
				}
			}
		}
	}
	
	private void drawCircle(Player p, Location center, double radius, ColorUtil color) {
		
		/*
		if(!((LivingEntity) p).isOnGround()) {
			if(!center.clone().subtract(0, 1, 0).getBlock().getType().isSolid()) center.subtract(0, 1, 0);
		}
		*/
		
		center.setY(5D);
		
		int PARTICLE_LIMIT = (int) radius * 10;
        double increment = (2 * Math.PI) / PARTICLE_LIMIT;
        for(int i = 0;i < PARTICLE_LIMIT; i++)
        {
            double angle = i * increment;
            double x = center.getX() + (radius * Math.cos(angle));
            double z = center.getZ() + (radius * Math.sin(angle));
            DustOptions dust = new DustOptions(color.getDyeColor().getColor(), 1);
            p.spawnParticle(Particle.REDSTONE, x, center.getBlockY() + 0.25D, z, 0, 0, 0, 0, dust);
        }
	}
	
	private double distanceCalculator(Location loc1, Location loc2) {
		if(loc1.getWorld().equals(loc2.getWorld())) {
			loc1.setY(loc2.getY());
			return loc1.distance(loc2);
		}
		return 999D;
	}

}
