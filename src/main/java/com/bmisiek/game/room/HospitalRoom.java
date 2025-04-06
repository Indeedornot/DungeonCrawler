package com.bmisiek.game.room;

import com.bmisiek.game.player.Player;
import com.bmisiek.game.player.PlayerManager;

/**
 * Heals player
 */
public class HospitalRoom extends Room{
    private final PlayerManager playerManager;

    public HospitalRoom(PlayerManager playerManager) {
        this.playerManager = playerManager;
    }

    @Override
    public void Act(Player player) {
        playerManager.heal(player, 10);
    }

    @Override
    public boolean canExistNextTo(Class<? extends Room> roomClass) {
       return roomClass != SpikyRoom.class && super.canExistNextTo(roomClass);
    }
}
