package com.bmisiek.game.room;

import com.bmisiek.game.player.Player;
import com.bmisiek.game.player.PlayerManager;

public class CorridorRoom extends Room {
    private final PlayerManager playerManager;

    public CorridorRoom(PlayerManager playerManager) {
        this.playerManager = playerManager;
    }

    @Override
    public void Act(Player player) {
    }

    @Override
    boolean canExistNextTo(Class<? extends Room> roomClass) {
        return true;
    }
}
