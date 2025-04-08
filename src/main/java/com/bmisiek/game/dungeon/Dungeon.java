package com.bmisiek.game.dungeon;

import com.bmisiek.structures.Point;
import com.bmisiek.game.player.Player;
import com.bmisiek.game.room.Room;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Main game object
 */
public class Dungeon {
    protected final Map<Point, Room> rooms;

    /**
     * Stores players with their position
     */
    protected final Map<Player, Point> players;

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

        this.players = new HashMap<>();
    }
}
