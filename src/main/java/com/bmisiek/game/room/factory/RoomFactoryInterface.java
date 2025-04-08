package com.bmisiek.game.room.factory;

import com.bmisiek.game.room.Room;

public interface RoomFactoryInterface<T> {
    boolean Supports(Class<? extends Room> roomClass);

    Room createRoom(Class<? extends Room> roomClass);
}
