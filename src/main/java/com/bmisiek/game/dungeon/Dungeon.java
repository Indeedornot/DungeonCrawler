package com.bmisiek.game.dungeon;

import com.bmisiek.game.basic.Point;
import com.bmisiek.game.event.DungeonEmptyEvent;
import com.bmisiek.game.event.DungeonEnteredEvent;
import com.bmisiek.game.event.PlayerDiedEvent;
import com.bmisiek.game.event.PlayerMovedEvent;
import com.bmisiek.game.event.data.DungeonEnteredEventData;
import com.bmisiek.game.event.data.PlayerMovedEventData;
import com.bmisiek.game.exception.InvalidActionException;
import com.bmisiek.game.player.Player;
import com.bmisiek.game.room.Room;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Main game object
 */
public class Dungeon implements ApplicationListener<PlayerDiedEvent>, DungeonInterface {
    private final Map<Point, Room> rooms;

    /**
     * Stores players with their position
     */
    private final Map<Player, Point> players;

    private final Point startPoint;

    private final ApplicationEventPublisher applicationEventPublisher;

    public Dungeon(ApplicationEventPublisher applicationEventPublisher, Map<Point, Room> rooms, Point startPoint) {
        this.applicationEventPublisher = applicationEventPublisher;
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

    public void enter(Player player) {
        this.players.put(player, startPoint);
        applicationEventPublisher.publishEvent(new DungeonEnteredEvent(this, new DungeonEnteredEventData(player)));
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
        applicationEventPublisher.publishEvent(new PlayerMovedEvent(this, new PlayerMovedEventData(player, playerLocation, point, this.rooms.get(point))));

    }

    @Override
    public List<Player> getPlayers() {
        return players.keySet().stream().toList();
    }

    @Override
    public Map<Point, Room> getRooms() {
       return rooms;
    }

    public Point getPosition(Player player) {
        return players.get(player);
    }

    /**
     * Removes dead players
     */
    @Override
    public void onApplicationEvent(PlayerDiedEvent event) {
        players.remove(event.getEventData().getPlayer());
        if(players.isEmpty()) {
            applicationEventPublisher.publishEvent(new DungeonEmptyEvent(this));
        }
    }
}
