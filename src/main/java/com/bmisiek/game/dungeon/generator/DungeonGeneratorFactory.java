package com.bmisiek.game.dungeon.generator;

import com.bmisiek.game.dungeon.interfaces.DungeonGeneratorInterface;
import com.bmisiek.game.room.Room;

import java.util.Map;

public class DungeonGeneratorFactory {
    private final RoomGeneratorFactory roomGeneratorFactory;
    private final DungeonFactory dungeonFactory;

    public DungeonGeneratorFactory(RoomGeneratorFactory roomGeneratorFactory, DungeonFactory dungeonFactory) {
        this.roomGeneratorFactory = roomGeneratorFactory;
        this.dungeonFactory = dungeonFactory;
    }

    public DungeonGeneratorInterface create(Map<Class<? extends Room>, Double> roomWeights) {
        return new DungeonGenerator(dungeonFactory, roomGeneratorFactory, roomWeights);
    }
}
