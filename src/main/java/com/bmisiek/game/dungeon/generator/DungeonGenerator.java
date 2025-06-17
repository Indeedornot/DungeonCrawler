package com.bmisiek.game.dungeon.generator;

import com.bmisiek.game.dungeon.Dungeon;
import com.bmisiek.structures.Point;
import com.bmisiek.game.config.GameConfigManager;
import com.bmisiek.game.dungeon.interfaces.RoomGeneratorInterface;
import com.bmisiek.game.room.*;
import jakarta.annotation.Nullable;
import org.springframework.data.domain.Range;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.*;

@Service
public class DungeonGenerator {
    private final Random random = new SecureRandom();
    private final Map<Point, Room> rooms = new HashMap<>();
    private final RoomGeneratorInterface roomGenerator;
    private final DungeonFactory dungeonFactory;
    private final Range<Integer> roomCountRange;
    private final RoomFactory roomFactory;

    DungeonGenerator(DungeonFactory dungeonFactory,
                    RoomGeneratorFactory roomGeneratorFactory,
                    GameConfigManager gameConfigManager,
                    RoomFactory roomFactory) {
        this.dungeonFactory = dungeonFactory;
        this.roomGenerator = roomGeneratorFactory.create(rooms);
        this.roomCountRange = gameConfigManager.getConfig().getRoomCount();
        this.roomFactory = roomFactory;
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

    /**
     *   If there are issues with creating rooms from other entrypoint, find a new one
     */
    private Point getNewEntrypoint() {
        return rooms.keySet().toArray(new Point[0])[random.nextInt(rooms.size())];
    }

    public Dungeon createDungeon() {
        rooms.clear();

        Point startPoint = new Point(0,0);
        CreateRooms(startPoint);

        addSpecialRoom(KeyRoom.class);
        addSpecialRoom(ExitRoom.class);

        ValidateDungeon();

        return dungeonFactory.create(rooms, startPoint);
    }

    /**
     * Add a special room to the dungeon
     */
    private void addSpecialRoom(Class<? extends Room> roomClass) {
        Point attachPoint = getValidAttachmentPoint(roomClass);

        if (attachPoint != null) {
            try {
                Room specialRoom = roomFactory.createRoom(roomClass);
                Point position = getNextPotentialPoint(attachPoint);

                if (position != null) {
                    rooms.put(position, specialRoom);
                }
            } catch (Exception e) {
                System.err.println("Failed to create special room: " + e.getMessage());
            }
        }
    }

    /**
     * Find a valid point to attach special room
     */
    private Point getValidAttachmentPoint(Class<? extends Room> roomClass) {
        List<Point> validPoints = new ArrayList<>();

        for (Map.Entry<Point, Room> entry : rooms.entrySet()) {
            if (roomClass.equals(KeyRoom.class) && !(entry.getValue() instanceof SpawnRoom) &&
                !(entry.getValue() instanceof ExitRoom)) {
                Point point = entry.getKey();
                if (getNextPotentialPoint(point) != null) {
                    validPoints.add(point);
                }
            } else if (roomClass.equals(ExitRoom.class) && entry.getValue() instanceof CorridorRoom) {
                Point point = entry.getKey();
                if (getNextPotentialPoint(point) != null) {
                    validPoints.add(point);
                }
            }
        }

        if (!validPoints.isEmpty()) {
            return validPoints.get(random.nextInt(validPoints.size()));
        }

        return getNewEntrypoint(); // Fallback to any point
    }

    private void CreateRooms(Point currentPoint) {
        rooms.put(currentPoint, new SpawnRoom()); // Create the starting room

        var roomCount = getRoomCount();
        for (int i = 1; i < roomCount; i++) {
            currentPoint = tryCreateNextRoomFrom(currentPoint);
            if(currentPoint == null) {
                currentPoint = getNewEntrypoint();
                i--;
            }
        }
    }

    private int getRoomCount() {
        return random.nextInt(
                roomCountRange.getLowerBound().getValue().orElseThrow(),
                roomCountRange.getUpperBound().getValue().orElseThrow() + 1
        );
    }

    private void ValidateDungeon() {
        if(rooms.isEmpty()) {
            throw new IllegalArgumentException("No rooms could be created.");
        }
    }
}
