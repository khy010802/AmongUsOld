package io.github.thatkawaiisam.assemble.events;

import io.github.thatkawaiisam.assemble.AssembleBoard;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class AssembleBoardCreatedEvent extends Event {

    public static final HandlerList handlerList = new HandlerList();

    private boolean cancelled = false;
    private final AssembleBoard board;

    public AssembleBoardCreatedEvent(AssembleBoard board) {
        this.board = board;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }
    
    public static HandlerList getHandlerList() {
        return handlerList;
    }
    
    public void setCancelled(boolean c) {
    	this.cancelled = c;
    }
    
    public boolean getCancelled() {
    	return this.cancelled;
    }
    
    public AssembleBoard getBoard() {
    	return this.board;
    }
}
