package io.github.thatkawaiisam.assemble.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class AssembleBoardDestroyEvent extends Event implements Cancellable {

    public static HandlerList handlerList = new HandlerList();

    private Player player;
    private boolean cancelled = false;

    public AssembleBoardDestroyEvent(Player player) {
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
		// TODO Auto-generated method stub
		return cancelled;
	}
	
	public Player getPlayer() {
		return player;
	}
}
