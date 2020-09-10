package io.github.thatkawaiisam.assemble;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import io.github.thatkawaiisam.assemble.events.AssembleBoardCreateEvent;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

public class Assemble {

	private JavaPlugin plugin;
	private AssembleAdapter adapter;
	private Map<UUID, AssembleBoard> boards;
	private AssembleThread thread;
	private AssembleListener listeners;
	private long ticks = 2;
	private boolean hook = false;
	private AssembleStyle assembleStyle = AssembleStyle.MODERN;
	private boolean debugMode = true;
	
	public Map<UUID, AssembleBoard> getBoards(){
		return boards;
	}
	
	public JavaPlugin getPlugin() {
		return this.plugin;
	}
	
	public AssembleAdapter getAdapter() {
		return adapter;
	}
	
	public long getTicks() {
		return ticks;
	}
	
	public void setTicks(long t) {
		this.ticks = t;
	}
	
	public boolean isHook() {
		return hook;
	}
	
	public AssembleStyle getAssembleStyle() {
		return this.assembleStyle;
	}
	
	public void setAssembleStyle(AssembleStyle as) {
		this.assembleStyle = as;
	}
	
	public boolean getDebugMode() {
		return this.debugMode;
	}

	public Assemble(JavaPlugin plugin, AssembleAdapter adapter) {
		if (plugin == null) {
			throw new RuntimeException("Assemble can not be instantiated without a plugin instance!");
		}

		this.plugin = plugin;
		this.adapter = adapter;
		this.boards = new ConcurrentHashMap<>();

		this.setup();
	}

	public void setup() {
		//Register Events
		this.listeners = new AssembleListener(this);
		this.plugin.getServer().getPluginManager().registerEvents(listeners, this.plugin);

		//Ensure that the thread has stopped running
		if (this.thread != null) {
			if(!this.thread.isCancelled()) this.thread.cancel();
			this.thread = null;
		}

		//Register new boards for existing online players
		for (Player player : Bukkit.getOnlinePlayers()) {
			//Make sure it doesn't double up
			AssembleBoardCreateEvent createEvent = new AssembleBoardCreateEvent(player);

			Bukkit.getPluginManager().callEvent(createEvent);
			if (createEvent.isCancelled()) {
				return;
			}

			getBoards().putIfAbsent(player.getUniqueId(), new AssembleBoard(player, this));
		}

		//Start Thread
		
	}
	
	public void start(long tick) {
		this.ticks = tick;
		this.thread = new AssembleThread(this);
		this.thread.start();
	}

	public void cleanup() {
		if (this.thread != null) {
			if(!this.thread.isCancelled()) this.thread.cancel();
			this.thread = null;
		}

		if (listeners != null) {
			HandlerList.unregisterAll(listeners);
			listeners = null;
		}

		for (UUID uuid : getBoards().keySet()) {
			Player player = Bukkit.getPlayer(uuid);

			if (player == null || !player.isOnline()) {
				continue;
			}

			getBoards().remove(uuid);
			player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
		}
	}

}
