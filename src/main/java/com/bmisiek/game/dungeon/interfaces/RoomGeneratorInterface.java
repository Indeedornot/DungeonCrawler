package com.bmisiek.game.dungeon.interfaces;

import com.bmisiek.game.basic.Point;
import com.bmisiek.game.room.Room;

public interface RoomGeneratorInterface {
    Room createRoomAt(Point point);
}
