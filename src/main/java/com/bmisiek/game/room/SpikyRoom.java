package com.bmisiek.game.room;

import com.bmisiek.game.player.Player;
import com.bmisiek.game.player.PlayerManager;

import java.security.SecureRandom;

/**
 * Room with a chance to deal damage to a player
 */
public class SpikyRoom extends Room {
    private final SecureRandom random = new SecureRandom();

    private final PlayerManager playerManager;

    public SpikyRoom(PlayerManager playerManager) {
        this.playerManager = playerManager;
    }

    private boolean shouldDealDamage() {
        return random.nextBoolean();
    }

    @Override
    public void Act(Player player) {
        if(shouldDealDamage()) {
            playerManager.takeDamage(player, 5);
        }
    }

    @Override
    public boolean canExistNextTo(Class<? extends Room> roomClass) {
        return roomClass != HospitalRoom.class && super.canExistNextTo(roomClass);
    }
}
