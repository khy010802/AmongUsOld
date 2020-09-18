package bepo.au.events;

import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerEscapeChairEvent extends Event implements Cancellable{
	
	private Player player;
	private ArmorStand chair;
	private boolean isCancelled;
	 
	public PlayerEscapeChairEvent(Player player, ArmorStand as) {
	    this.player = player;
	    this.chair = as;
	    this.isCancelled = false;
	}
	 
	public Player getPlayer() {
	    return this.player;
	}
	 
	public ArmorStand getChair() {
		return this.chair;
	}
	
	@Override
	public boolean isCancelled() {
		return isCancelled;
	}
	
	@Override
	public void setCancelled(boolean arg0) {
	    this.isCancelled = arg0;
	}
	
	private static final HandlerList handlers = new HandlerList();
	 
	@Override
	public HandlerList getHandlers() {
	    return handlers;
	}
	 
	public static HandlerList getHandlerList() {
	    return handlers;
	}
}
