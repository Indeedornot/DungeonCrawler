package com.bmisiek.printer.contract;

import com.bmisiek.game.dungeon.DungeonInterface;
import com.bmisiek.game.player.Player;

/**
 * Responsible for displaying elements to users and retrieving their next actions
 */
public interface GuiInterface {
    void Update(DungeonInterface dungeon);

    GameAction Act(DungeonInterface dungeon, Player player) throws RuntimeException;
}
