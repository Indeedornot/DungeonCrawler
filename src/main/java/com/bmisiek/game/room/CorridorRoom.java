package com.bmisiek.game.room;

import com.bmisiek.game.player.Player;

public class CorridorRoom extends Room {

    public CorridorRoom() {
    }

    @Override
    public void Act(Player player) {
    }

    @Override
    public boolean canExistNextTo(Class<? extends Room> roomClass) {
        return true;
    }
}
