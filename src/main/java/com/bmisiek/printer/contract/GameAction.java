package com.bmisiek.printer.contract;

public abstract class GameAction {
    public abstract String getCommandName();

    /**
     * Determines if this action consumes the player's turn
     * @return true if the action consumes the turn, false if another action can be performed immediately
     */
    public boolean consumesTurn() {
        return true; // Default behavior - most actions consume a turn
    }
}
