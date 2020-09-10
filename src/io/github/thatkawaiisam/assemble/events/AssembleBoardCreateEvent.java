package io.github.thatkawaiisam.assemble.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class AssembleBoardCreateEvent extends Event implements Cancellable {

    public static HandlerList handlerList = new HandlerList();

    private Player player;
    private boolean cancelled = false;

    public AssembleBoardCreateEvent(Player player) {
        this.player = player;
    }

    @Override
    public void setCancelled(boolean b) {
        this.cancelled = b;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

	@Override
	public boolean isCancelled() {
		return cancelled;
	}
	
	public Player getPlayer() {
		return this.player;
	}
}
