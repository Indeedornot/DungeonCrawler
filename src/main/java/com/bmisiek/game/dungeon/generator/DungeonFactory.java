package com.bmisiek.game.dungeon.generator;

import com.bmisiek.game.basic.Point;
import com.bmisiek.game.dungeon.Dungeon;
import com.bmisiek.game.dungeon.DungeonInterface;
import com.bmisiek.game.room.Room;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Map;

public class DungeonFactory {
    private final ApplicationEventPublisher applicationEventPublisher;

    public DungeonFactory(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    public DungeonInterface create(Map<Point, Room> rooms, Point startPoint) {
        return new Dungeon(applicationEventPublisher, rooms, startPoint);
    }
}
