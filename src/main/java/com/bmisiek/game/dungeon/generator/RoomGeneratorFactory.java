package com.bmisiek.game.dungeon.generator;

import com.bmisiek.structures.Point;
import com.bmisiek.game.config.GameConfigManager;
import com.bmisiek.game.dungeon.interfaces.RoomGeneratorInterface;
import com.bmisiek.game.room.Room;
import com.bmisiek.game.room.RoomFactory;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class RoomGeneratorFactory {
    private final GameConfigManager gameConfigManager;
    private final RoomFactory roomFactory;

    private RoomGeneratorFactory(GameConfigManager gameConfigManager, RoomFactory roomFactory) {
        this.gameConfigManager = gameConfigManager;
        this.roomFactory = roomFactory;
    }

    public RoomGeneratorInterface create(Map<Point, Room> rooms) {
        var roomWeights = gameConfigManager.getConfig().getRoomWeights();
        return new RoomGenerator(roomFactory, roomWeights, rooms);
    }
}
