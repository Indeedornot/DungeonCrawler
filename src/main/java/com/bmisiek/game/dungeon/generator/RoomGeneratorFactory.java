package com.bmisiek.game.dungeon.generator;

import com.bmisiek.game.basic.Point;
import com.bmisiek.game.dungeon.interfaces.RoomGeneratorInterface;
import com.bmisiek.game.room.Room;
import com.bmisiek.game.room.RoomFactory;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class RoomGeneratorFactory {
    private final RoomFactory roomFactory;

    private RoomGeneratorFactory(RoomFactory roomFactory) {
        this.roomFactory = roomFactory;
    }

    public RoomGeneratorInterface create(Map<Class<? extends Room>, Double> roomWeights, Map<Point, Room> rooms) {
        return new RoomGenerator(roomFactory, roomWeights, rooms);
    }
}
