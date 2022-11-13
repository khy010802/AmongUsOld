package bepo.au.base;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import bepo.au.GameTimer.Status;
import bepo.au.Main;

public abstract class VisualTask extends BukkitRunnable{
	
	public static List<VisualTask> Running_VisualTasks = new ArrayList<VisualTask>();
	
	public static void resetAllTasks() {
		List<VisualTask> clone = new ArrayList<VisualTask>(Running_VisualTasks);
		Bukkit.broadcastMessage("" + clone.size());
		for(VisualTask vt : clone) {
			Bukkit.broadcastMessage("vt " + vt.toString());
			vt.Finish(true);
		}
	}
	
	protected int count;
	protected boolean requirePlayer;
	protected Player p;
	protected String name;
	
	public void StartTimer(Player p, boolean need_player) {
		count = 0;
		Running_VisualTasks.add(this);
		this.p = p;
		this.name = p.getName();
		this.requirePlayer = need_player;
		this.runTaskTimer(Main.getInstance(), 0L, 1L);
		onStart();
	}
	
	public abstract void onStart();
	
	public abstract void onTicking(int count);
	
	public abstract void onFinished();
	
	public abstract void Reset();
	
	public void Finish(boolean reset) {
		if(!this.isCancelled()) {
			this.cancel();
			Running_VisualTasks.remove(this);
			onFinished();
		}		
		if(reset) Reset();
	}
	
	private boolean checkEnd() {
		if(Main.gt == null || Main.gt.getStatus() != Status.WORKING) return true;
		if(requirePlayer) {
			if(p == null || !p.isOnline() || PlayerData.getPlayerData(name) == null || !PlayerData.getPlayerData(name).isAlive()) return true;
		}
		return false;
	}
	
	public void run() {
		
		if(checkEnd()) {
			Finish(false);
			return;
		}
		
		onTicking(count);
		count++;
	}
	

}
