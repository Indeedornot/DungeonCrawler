package com.bmisiek.game.dungeon;

import com.bmisiek.structures.Point;
import com.bmisiek.game.event.DungeonEmptyEvent;
import com.bmisiek.game.event.DungeonEnteredEvent;
import com.bmisiek.game.event.PlayerDiedEvent;
import com.bmisiek.game.event.PlayerMovedEvent;
import com.bmisiek.game.event.data.DungeonEnteredEventData;
import com.bmisiek.game.event.data.PlayerMovedEventData;
import com.bmisiek.game.exception.InvalidActionException;
import com.bmisiek.game.player.Player;
import com.bmisiek.game.room.Room;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Main game object
 */
@Service
public class DungeonManager implements ApplicationListener<PlayerDiedEvent>, DungeonManagerInterface {
    private final ApplicationEventPublisher applicationEventPublisher;

    @Setter
    private Dungeon dungeon;

    public DungeonManager(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    public void enter(Player player) {
        this.dungeon.players.put(player, dungeon.startPoint);
        applicationEventPublisher.publishEvent(new DungeonEnteredEvent(this, new DungeonEnteredEventData(player)));
    }

    private boolean roomExists(Point point) {
        return this.dungeon.rooms.containsKey(point);
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

    public void tryMove(Player player, Point direction) throws InvalidActionException {
        Point currentLocation = this.dungeon.players.get(player);
        Point newLocation = currentLocation.add(direction);

        AssertValidMove(currentLocation, newLocation);

        this.dungeon.players.put(player, newLocation);
        applicationEventPublisher.publishEvent(new PlayerMovedEvent(
                this,
                new PlayerMovedEventData(
                        player,
                        currentLocation,
                        newLocation,
                        this.dungeon.rooms.get(direction)
                )
        ));

        this.dungeon.rooms.get(newLocation).Act(player);
    }

    @Override
    public List<Player> getPlayers() {
        return dungeon.players.keySet().stream().toList();
    }

    @Override
    public Map<Point, Room> getRooms() {
        return dungeon.rooms;
    }

    public Point getPosition(Player player) {
        return dungeon.players.get(player);
    }

    /**
     * Removes dead players
     */
    @Override
    public void onApplicationEvent(@NotNull PlayerDiedEvent event) {
        dungeon.players.remove(event.getEventData().getPlayer());
        if(dungeon.players.isEmpty()) {
            applicationEventPublisher.publishEvent(new DungeonEmptyEvent(this));
        }
    }
}
