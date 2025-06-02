package com.bmisiek.game.dungeon;

import com.bmisiek.structures.Point;
import com.bmisiek.game.exception.InvalidActionException;
import com.bmisiek.game.player.Player;
import com.bmisiek.game.room.Room;

import java.util.Map;

public interface DungeonManagerInterface {
    void setDungeon(Dungeon dungeon);

    void enter(Player player);

    Point getPlayerPosition();

    void tryMove(Point point) throws InvalidActionException;
    
    void searchRoom() throws InvalidActionException;

    Player getPlayer();

    Map<Point, Room> getRooms();
}
