package com.bmisiek.game.room.factory;

import com.bmisiek.game.player.PlayerManager;
import com.bmisiek.game.room.HospitalRoom;
import com.bmisiek.game.room.Room;
import org.springframework.stereotype.Service;

@Service
public class HospitalRoomFactory implements RoomFactoryInterface<HospitalRoom> {
    private final PlayerManager playerManager;

    public HospitalRoomFactory(PlayerManager playerManager) {
        this.playerManager = playerManager;
    }

    @Override
    public boolean Supports(Class<? extends Room> roomClass) {
        return roomClass == HospitalRoom.class;
    }

    @Override
    public Room createRoom(Class<? extends Room> roomClass) {
        return new HospitalRoom(playerManager);
    }
}
