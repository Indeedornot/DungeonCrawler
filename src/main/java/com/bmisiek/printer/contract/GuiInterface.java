package com.bmisiek.printer.contract;

import com.bmisiek.game.dungeon.DungeonManagerInterface;
import com.bmisiek.game.player.Player;

/**
 * Responsible for displaying elements to users and retrieving their next actions
 */
public interface GuiInterface {
    void Update(DungeonManagerInterface dungeon);

    GameAction Act(DungeonManagerInterface dungeon, Player player) throws RuntimeException;
}
