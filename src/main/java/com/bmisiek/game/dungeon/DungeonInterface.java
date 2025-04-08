package com.bmisiek.game.dungeon;

import com.bmisiek.structures.Point;
import com.bmisiek.game.exception.InvalidActionException;
import com.bmisiek.game.player.Player;
import com.bmisiek.game.room.Room;

import java.util.List;
import java.util.Map;

public interface DungeonInterface {
    void enter(Player player);

    Point getPosition(Player player);

    void tryMove(Player player, Point point) throws InvalidActionException;

    List<Player> getPlayers();

    Map<Point, Room> getRooms();
}
