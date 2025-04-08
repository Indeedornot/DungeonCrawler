package com.bmisiek.game.room.factory;

import com.bmisiek.game.player.PlayerManager;
import com.bmisiek.game.room.Room;
import com.bmisiek.game.room.SpikyRoom;
import org.springframework.stereotype.Service;

@Service
public class SpikyRoomFactory implements RoomFactoryInterface<SpikyRoom> {
    private final PlayerManager playerManager;

    public SpikyRoomFactory(PlayerManager playerManager) {
        this.playerManager = playerManager;
    }

    @Override
    public boolean Supports(Class<? extends Room> roomClass) {
        return roomClass == SpikyRoom.class;
    }

    @Override
    public Room createRoom(Class<? extends Room> roomClass) {
        return new SpikyRoom(playerManager);
    }
}
