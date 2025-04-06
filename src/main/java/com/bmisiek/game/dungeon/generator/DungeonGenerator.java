package com.bmisiek.game.dungeon.generator;

import com.bmisiek.game.basic.Point;
import com.bmisiek.game.dungeon.Dungeon;
import com.bmisiek.game.dungeon.DungeonInterface;
import com.bmisiek.game.dungeon.interfaces.DungeonGeneratorInterface;
import com.bmisiek.game.dungeon.interfaces.RoomGeneratorInterface;
import com.bmisiek.game.room.Room;
import jakarta.annotation.Nullable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.util.Pair;

import java.security.SecureRandom;
import java.util.*;

class DungeonGenerator implements DungeonGeneratorInterface {
    public static final int MAX_ROOM_COUNT = 10;
    private final Random random = new SecureRandom();
    private final Map<Point, Room> rooms = new HashMap<>();
    private final RoomGeneratorInterface roomGenerator;
    private final DungeonFactory dungeonFactory;

    DungeonGenerator(DungeonFactory dungeonFactory, RoomGeneratorFactory roomGeneratorFactory, Map<Class<? extends Room>, Double> roomWeights) {
        this.dungeonFactory = dungeonFactory;
        roomGenerator = roomGeneratorFactory.create(roomWeights, rooms);
    }

    private Point getDirectionPoint(Point currentPoint, int direction) {
        return switch (direction) {
            case 0 -> new Point(currentPoint.getX() + 1, currentPoint.getY());
            case 1 -> new Point(currentPoint.getX() - 1, currentPoint.getY());
            case 2 -> new Point(currentPoint.getX(), currentPoint.getY() - 1);
            default -> new Point(currentPoint.getX(), currentPoint.getY() + 1);
        };
    }

    @Nullable
    private Point getNextPotentialPoint(Point currentPoint) {
        List<Integer> intList = Arrays.asList(0,1,2,3);
        Collections.shuffle(intList);

        Point nextPoint;
        for (Integer integer : intList) {
            nextPoint = getDirectionPoint(currentPoint, integer);
            if (!rooms.containsKey(nextPoint)) {
                return nextPoint;
            }
        }

        return null;
    }

    @Nullable
    private Pair<Point, Room> createNextRoomFrom(Point currentPoint) {
        Point nextPotentialPoint = getNextPotentialPoint(currentPoint);
        if(nextPotentialPoint == null) {
            return null;
        }

        Room nextRoom = roomGenerator.createRoomAt(nextPotentialPoint);
        if(nextRoom == null) {
            return null;
        }

        return Pair.of(nextPotentialPoint, nextRoom);
    }

    @Nullable
    private Point tryCreateNextRoomFrom(Point currentPoint) {
        var pair = createNextRoomFrom(currentPoint);
        if (pair == null) {
            return null;
        }

        rooms.put(pair.getFirst(), pair.getSecond());
        return pair.getFirst();

    }

    /*
        If there are issues with creating rooms from other entrypoint, find a new one
     */
    private Point getNewEntrypoint(Point currentPoint) {
        return rooms.keySet().toArray(new Point[0])[random.nextInt(rooms.size())];
    }

    /*
      @param roomWeights Weighted map of allowed room classes
     */
    public DungeonInterface createDungeon(Map<Class<? extends Room>, Double> roomWeights) {
        Point startPoint = new Point(0,0);
        CreateRooms(startPoint);

        ValidateDungeon();

        return dungeonFactory.create(rooms, startPoint);
    }

    private void CreateRooms(Point currentPoint) {
        rooms.put(currentPoint, roomGenerator.createRoomAt(currentPoint)); // Create the starting room

        for (int i = 1; i < MAX_ROOM_COUNT; i++) {
            currentPoint = tryCreateNextRoomFrom(currentPoint);
            if(currentPoint == null) {
                currentPoint = getNewEntrypoint(currentPoint);
                i--;
            }
        }
    }

    private void ValidateDungeon() {
        if(rooms.isEmpty()) {
            throw new IllegalArgumentException("No rooms could be created.");
        }
    }
}
