package com.bmisiek.game.dungeon.interfaces;

import com.bmisiek.game.dungeon.DungeonInterface;
import com.bmisiek.game.room.Room;

import java.util.Map;

public interface DungeonGeneratorInterface {
    /*
        @param roomWeights Weighted map of allowed room classes
    */
    public DungeonInterface createDungeon(Map<Class<? extends Room>, Double> roomWeights);
}
