package com.bmisiek.game.room;

import com.bmisiek.game.exception.InvalidActionException;
import com.bmisiek.game.player.Player;

public abstract class Room {
    public abstract void Act(Player player);

    public boolean HasAdditionalActions() {
        return false;
    }

    public String GetAdditionalActionDescription() throws InvalidActionException {
        assert HasAdditionalActions();
        throw new IllegalStateException("No additional action description available");
    }

    public void PerformAdditionalAction(Player player) {
        assert HasAdditionalActions();
    }

    public boolean canExistNextTo(Class<? extends Room> roomClass) {
        return roomClass != this.getClass();
    }
}
