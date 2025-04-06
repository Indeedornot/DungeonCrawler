package com.bmisiek.game.dungeon;

import com.bmisiek.game.basic.Point;
import com.bmisiek.game.exception.InvalidActionException;
import com.bmisiek.game.player.Player;

public interface DungeonInterface {
    void enter(Player player);

    Point getPosition(Player player);

    void tryMove(Player player, Point point) throws InvalidActionException;
}
