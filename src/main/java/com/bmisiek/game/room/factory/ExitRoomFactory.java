package com.bmisiek.game.room.factory;

import com.bmisiek.game.room.ExitRoom;
import com.bmisiek.game.room.Room;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
public class ExitRoomFactory implements RoomFactoryInterface<ExitRoom> {
    private final ApplicationEventPublisher eventPublisher;

    public ExitRoomFactory(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @Override
    public boolean Supports(Class<? extends Room> roomClass) {
        return ExitRoom.class.isAssignableFrom(roomClass);
    }

    @Override
    public Room createRoom(Class<? extends Room> roomClass) {
        return new ExitRoom(eventPublisher);
    }
}
