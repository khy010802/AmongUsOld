package bepo.au.manager;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

import bepo.au.GameTimer;

public class BossBarManager {
	
	public enum BossBarList {
		
		TASKS;
		
		private BossBar bb;
		
		public void setBossBar(BossBar bb) {
			this.bb = bb;
		}
		
		public BossBar getBossBar() {
			return bb;
		}
		
	}
	
	public static void updateBossBar(BossBarList bbl, boolean comm) {
		
		BossBar bb = bbl.getBossBar();
		
		if(bb == null) return;
		
		
		
		switch(bbl) {
		case TASKS:
			
			if(comm) {
				bb.setTitle("§c통신 기기 고장");
				bb.setProgress(1.0D);
				bb.setColor(BarColor.RED);
			} else {
				bb.setColor(BarColor.GREEN);
				bb.setProgress((double) GameTimer.CLEARED_MISSION / (GameTimer.REQUIRED_MISSION > 0 ? GameTimer.REQUIRED_MISSION : 1));
				bb.setTitle("§f일과 진행도 §a" + GameTimer.CLEARED_MISSION + "/" + (GameTimer.REQUIRED_MISSION > 0 ? GameTimer.REQUIRED_MISSION : 1));
			}
			break;
		}
		
	}
	
	public static void registerBossBar(BossBarList bbl) {
		
		BossBar bb;
		
		switch(bbl) {
		case TASKS:
			bb = Bukkit.createBossBar("", BarColor.GREEN, BarStyle.SEGMENTED_10);
			break;
		default:
			return;
		}
		
		bb.setVisible(true);
		bbl.setBossBar(bb);
		
		for(Player ap : Bukkit.getOnlinePlayers()) {
			bb.addPlayer(ap);
		}
		
		updateBossBar(bbl, false);
		
	}
	
	public static void resetAllBossBar() {
		for(BossBarList bbl : BossBarList.values()) if(bbl.getBossBar() != null) bbl.getBossBar().removeAll();
	}
	
	public static void sendBossBar(BossBarList bbl, Player p) {
		BossBar bb = bbl.getBossBar();
		
		if(bb == null) return;
		
		if(!bb.getPlayers().contains(p)){
			bb.addPlayer(p);
		}
	}

}
