package com.bmisiek.game.dungeon.generator;

import com.bmisiek.structures.Point;
import com.bmisiek.game.dungeon.interfaces.RoomGeneratorInterface;
import com.bmisiek.game.room.Room;
import com.bmisiek.game.room.RoomFactory;
import lombok.Setter;
import lombok.SneakyThrows;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;

class RoomGenerator implements RoomGeneratorInterface {
    private final RoomFactory roomFactory;

    @Setter
    private Map<Class<? extends Room>, Double> roomWeights;

    @Setter
    private Map<Point, Room> rooms;

    private final Random random = new Random();

    RoomGenerator(RoomFactory roomFactory, Map<Class<? extends Room>, Double> roomWeights, Map<Point, Room> rooms) {
        this.roomFactory = roomFactory;
        this.roomWeights = roomWeights;
        this.rooms = rooms;
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

    @SneakyThrows
    public Room createRoomAt(Point point) {
        Class<? extends Room> roomClass = getRandomRoomClass(point);
        return (roomClass != null) ? roomFactory.createRoom(roomClass) : null;
    }
}