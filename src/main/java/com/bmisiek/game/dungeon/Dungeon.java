package com.bmisiek.game.dungeon;

import com.bmisiek.game.basic.Point;
import com.bmisiek.game.event.PlayerDiedEvent;
import com.bmisiek.game.exception.InvalidActionException;
import com.bmisiek.game.player.Player;
import com.bmisiek.game.room.Room;
import org.springframework.context.ApplicationListener;

import java.util.HashMap;
import java.util.Map;

/**
 * Main game object
 */
public class Dungeon implements ApplicationListener<PlayerDiedEvent> {
    private final Map<Point, Room> rooms;

    /**
     * Stores players with their position
     */
    private final Map<Player, Point> players;

    private Point startPoint;

    Dungeon(Map<Point, Room> rooms, Point startPoint) {
        this.rooms = rooms;
        if(rooms.isEmpty()) {
            throw new IllegalArgumentException("No rooms could be created.");
        }

        if(!rooms.containsKey(startPoint)) {
            throw new IllegalArgumentException("No start room found in the room array.");
        }

        this.players = new HashMap<>();
    }

    public void enter(Player player) {
        this.players.put(player, startPoint);
    }

    private boolean roomExists(Point point) {
        return this.rooms.containsKey(point);
    }

    private void AssertValidMove(Point from, Point to) throws InvalidActionException {
        if(!this.roomExists(to)) {
            throw new InvalidActionException("Room does not exist");
        }

        int distance = from.getDistance(to);
        if(distance > 1) {
            throw new InvalidActionException("Attempted to move too big distance");
        }

        if(distance == 0) {
            throw new InvalidActionException("Attempted to move to the same location");
        }
    }

    public void tryMove(Player player, Point point) throws InvalidActionException {
        Point playerLocation = this.players.get(player);
        AssertValidMove(playerLocation, point);

        this.players.put(player, point);
    }

    public Point getPosition(Player player) {
        return players.get(player);
    }

    /**
     * Removes dead players
     */
    @Override
    public void onApplicationEvent(PlayerDiedEvent event) {
        players.remove(event.getPlayer());
    }
}
