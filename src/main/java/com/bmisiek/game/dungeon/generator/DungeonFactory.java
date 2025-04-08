package com.bmisiek.game.dungeon.generator;

import com.bmisiek.structures.Point;
import com.bmisiek.game.dungeon.Dungeon;
import com.bmisiek.game.room.Room;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class DungeonFactory {
    public DungeonFactory() {
    }

    public Dungeon create(Map<Point, Room> rooms, Point startPoint) {
        return new Dungeon(rooms, startPoint);
    }
}
