package com.bmisiek.game.dungeon;

import com.bmisiek.game.basic.Point;
import com.bmisiek.game.room.Room;
import com.bmisiek.game.room.RoomFactory;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;

@Service
public class DungeonFactory {
    public static final int MAX_ROOM_COUNT = 10;
    private final RoomFactory roomFactory;
    private final Random random = new SecureRandom();
    private Map<Point, Room> rooms = new HashMap<>();
    private Map<Class<? extends Room>, Double> roomWeights;

    public DungeonFactory(RoomFactory roomFactory) {
        this.roomFactory = roomFactory;
    }

    private void forEachNeighbor(Point point, BiConsumer<Point, Room> action) {
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                if (dx == 0 && dy == 0) continue;

                Point neighborPoint = new Point(point.getX() + dx, point.getY() + dy);
                if (!rooms.containsKey(neighborPoint)) {
                    continue;
                }

                action.accept(neighborPoint, rooms.get(neighborPoint));
            }
        }
    }

    private boolean canRoomExistNextToNeighbors(Point point, Class<? extends Room> roomClass) {
        var canRoomExist = new AtomicBoolean(true);
        forEachNeighbor(point, (neighborPoint, neighborRoom) -> {
            if (!canRoomExist.get() || !neighborRoom.canExistNextTo(roomClass)) {
                canRoomExist.set(false);
            }
        });

        return canRoomExist.get();
    }

    private Class<? extends Room> getRandomRoomClass(Point point) {
        List<Map.Entry<Class<? extends Room>, Double>> possibleRooms = roomWeights.entrySet().stream()
                .filter(entry -> canRoomExistNextToNeighbors(point, entry.getKey()))
                .toList();

        if(possibleRooms.isEmpty()) {
            return null;
        }

        double totalWeight = possibleRooms.stream()
                .mapToDouble(Map.Entry::getValue)
                .sum();

        double randomWeight = random.nextDouble(totalWeight);
        double cumulativeWeight = 0;

        for (Map.Entry<Class<? extends Room>, Double> entry : possibleRooms) {
            cumulativeWeight += entry.getValue();
            if (randomWeight < cumulativeWeight) {
                return entry.getKey();
            }
        }

        // Should ideally not reach here due to the random number generation logic
        return possibleRooms.getLast().getKey();
    }

    private Room createRoomAt(Point point) {
        Class<? extends Room> roomClass = getRandomRoomClass(point);
        return (roomClass != null) ? roomFactory.createRoom(roomClass) : null;
    }

    private Point getNextPotentialPoint(Point currentPoint) {
        return switch (random.nextInt(4)) {
            case 0 -> new Point(currentPoint.getX() + 1, currentPoint.getY());
            case 1 -> new Point(currentPoint.getX() - 1, currentPoint.getY());
            case 2 -> new Point(currentPoint.getX(), currentPoint.getY() - 1);
            default -> new Point(currentPoint.getX(), currentPoint.getY() + 1);
        };
    }

    /*
      @param roomWeights Weighted map of allowed room classes
     */
    public Dungeon createDungeon(Map<Class<? extends Room>, Double> roomWeights) {
        this.rooms = new HashMap<>();
        this.roomWeights = roomWeights;

        Point currentPoint = new Point(0, 0);
        rooms.put(currentPoint, createRoomAt(currentPoint)); // Create the starting room

        for (int i = 1; i < MAX_ROOM_COUNT; i++) {
            Point nextPotentialPoint = getNextPotentialPoint(currentPoint);
            if (!rooms.containsKey(nextPotentialPoint)) {
                Room nextRoom = createRoomAt(nextPotentialPoint);
                if (nextRoom != null) {
                    rooms.put(nextPotentialPoint, nextRoom);
                    currentPoint = nextPotentialPoint; // Move to the newly created room
                }
            } else {
                // If the next point is already occupied, try again from the current point
                i--;
            }
        }

        if(rooms.isEmpty()) {
            throw new IllegalArgumentException("No rooms could be created.");
        }

        return new Dungeon(rooms);
    }
}
