package com.bmisiek.game.dungeon.generator;

import com.bmisiek.game.dungeon.interfaces.DungeonGeneratorInterface;
import com.bmisiek.game.room.Room;

import java.util.Map;

public class DungeonGeneratorFactory {
    private final RoomGeneratorFactory roomGeneratorFactory;

    public DungeonGeneratorFactory(RoomGeneratorFactory roomGeneratorFactory) {
        this.roomGeneratorFactory = roomGeneratorFactory;
    }

    public DungeonGeneratorInterface create(Map<Class<? extends Room>, Double> roomWeights) {
        return new DungeonGenerator(roomGeneratorFactory, roomWeights);
    }
}
