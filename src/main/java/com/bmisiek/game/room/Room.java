package com.bmisiek.game.room;

import com.bmisiek.game.player.Player;

public abstract class Room {
    public abstract void Act(Player player);

    public boolean canExistNextTo(Class<? extends Room> roomClass) {
        return roomClass != this.getClass();
    }
}
