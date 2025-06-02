package com.bmisiek.printer.contract;

import com.bmisiek.structures.Point;

public abstract class GameAction {
    public abstract String getCommandName();
    
    public boolean isMovement() {
        return false;
    }
    
    public Point getMovement() {
        throw new UnsupportedOperationException("This action does not support movement");
    }
    
    /**
     * Determines if this action consumes the player's turn
     * @return true if the action consumes the turn, false if another action can be performed immediately
     */
    public boolean consumesTurn() {
        return true; // Default behavior - most actions consume a turn
    }
}
