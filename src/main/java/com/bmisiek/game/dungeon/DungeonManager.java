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
import org.springframework.data.util.Pair;
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
        this.dungeon.player = Pair.of(player, dungeon.startPoint);
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

    public void tryMove(Point direction) throws InvalidActionException {
        assert this.dungeon.player != null;
        Point currentLocation = this.dungeon.player.getSecond();
        Point newLocation = currentLocation.add(direction);

        AssertValidMove(currentLocation, newLocation);

        this.dungeon.player = Pair.of(getPlayer(), newLocation);
        applicationEventPublisher.publishEvent(new PlayerMovedEvent(
                this,
                new PlayerMovedEventData(
                        getPlayer(),
                        currentLocation,
                        newLocation,
                        this.dungeon.rooms.get(direction)
                )
        ));

        this.dungeon.rooms.get(newLocation).Act(getPlayer());
    }

    @Override
    public Player getPlayer() {
        assert dungeon.player != null;
        return dungeon.player.getFirst();
    }

    @Override
    public Point getPlayerPosition() {
        assert this.dungeon.player != null;
        return this.dungeon.player.getSecond();
    }

    @Override
    public Map<Point, Room> getRooms() {
        return dungeon.rooms;
    }
    /**
     * Removes dead players
     */
    @Override
    public void onApplicationEvent(@NotNull PlayerDiedEvent event) {
        dungeon.player = null;
        applicationEventPublisher.publishEvent(new DungeonEmptyEvent(this));
    }
}
