package com.bmisiek.game.room;

import com.bmisiek.game.player.PlayerManager;
import org.springframework.stereotype.Service;

@Service
public class RoomFactory {
    private final PlayerManager playerManager;

    public RoomFactory(PlayerManager playerManager) {
        this.playerManager = playerManager;
    }

    public Room createRoom(Class<? extends Room> roomClass) {
        return new CorridorRoom(playerManager);
    }
}
