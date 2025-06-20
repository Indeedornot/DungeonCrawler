package com.bmisiek.game.dungeon;

import com.bmisiek.structures.Point;
import com.bmisiek.game.player.Player;
import com.bmisiek.game.room.Room;
import org.jetbrains.annotations.Nullable;
import org.springframework.data.util.Pair;

import java.util.Map;

/**
 * Main game object
 */
public class Dungeon {
    protected final Map<Point, Room> rooms;

    /**
     * Stores players with their position
     */
    protected @Nullable Pair<Player, Point> player;

    protected final Point startPoint;

    public Dungeon(Map<Point, Room> rooms, Point startPoint
    ) {
        this.rooms = rooms;
        this.startPoint = startPoint;

        if(rooms.isEmpty()) {
            throw new IllegalArgumentException("No rooms could be created.");
        }

        if(!rooms.containsKey(startPoint)) {
            throw new IllegalArgumentException("No start room found in the room array.");
        }
    }
}
