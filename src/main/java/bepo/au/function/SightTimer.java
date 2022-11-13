package bepo.au.function;


import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Particle.DustOptions;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import bepo.au.GameTimer;
import bepo.au.Main;
import bepo.au.Main.SETTING;
import bepo.au.GameTimer.Status;
import bepo.au.base.PlayerData;
import bepo.au.function.CCTV.E_cctv;
import bepo.au.utils.ColorUtil;
import bepo.au.utils.PlayerUtil;
import bepo.au.utils.Util;

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
		
		for(Player ap : Bukkit.getOnlinePlayers()) {//cctv
			if(CCTV.watchingCCTVset.isEmpty()) break;
			if(!CCTV.watchingCCTVset.contains(ap)) {
				for(E_cctv cctv : E_cctv.values()) {
					Location loc = cctv.getloc();
					Vector vec = loc.getDirection();
					Location clone = loc.clone().add(0, 1, 0).add(vec.normalize().multiply(0.3D));
					DustOptions dust = new DustOptions(Color.RED, 1);
					ap.spawnParticle(Particle.REDSTONE, clone, 0, 0, 0, 0, dust);
				}
			}
		}
		
		for(Player ap : Bukkit.getOnlinePlayers()) {//
			
			Util.sendCorpse(ap);
			
			PlayerData pd = PlayerData.getPlayerData(ap.getName());
			double distance = GameTimer.IMPOSTER.contains(ap.getName()) ? SETTING.IMPOSTER_SIGHT_BLOCK.getAsInteger() : SETTING.CREW_SIGHT_BLOCK.getAsInteger();
			if(distance == 0) distance += 0.01D;
			else if(distance < 0) continue;
			
			if(pd != null && pd.isAlive()) {
				for(Player p2 : GameTimer.ALIVE_PLAYERS) {
					if(!p2.equals(ap)) {
						if(distanceCalculator(p2.getLocation(), ap.getLocation()) <= distance) {
							if(PlayerUtil.isHidden(ap, p2) && !PlayerUtil.isInvisible(p2)) {
								Util.debugMessage("" + ap.getName() + " see " + p2.getName());
								PlayerUtil.showPlayer(ap, p2);
							} else if(!PlayerUtil.isHidden(ap, p2) && PlayerUtil.isInvisible(p2)) {
								PlayerUtil.hidePlayer(ap, p2);
							}
						} else {
							if(!PlayerUtil.isHidden(ap, p2)) {
								Util.debugMessage("" + ap.getName() + " cannot see " + p2.getName());
								PlayerUtil.hidePlayer(ap, p2);
							}
						}
					}
				}
				
				drawCircle(ap, ap.getLocation(), distance, pd.getColor());
			} else {
				
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
		
		int PARTICLE_LIMIT = (int) radius * 5;
        double increment = (2 * Math.PI) / PARTICLE_LIMIT;
        for(int i = 0;i < PARTICLE_LIMIT; i++)
        {
            double angle = i * increment;
            double x = center.getX() + (radius * Math.cos(angle));
            double z = center.getZ() + (radius * Math.sin(angle));
            DustOptions dust = new DustOptions(color.getDyeColor().getColor(), 1);
            if(p.getWorld().getBlockAt((int)Math.floor(x), center.getBlockY(), (int)Math.floor(z)).isEmpty()) p.spawnParticle(Particle.REDSTONE, x, center.getBlockY() + 0.25D, z, 0, 0, 0, 0, dust);
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
