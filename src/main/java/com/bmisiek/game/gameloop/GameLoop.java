package com.bmisiek.game.gameloop;

import com.bmisiek.game.dungeon.DungeonManagerInterface;
import com.bmisiek.game.dungeon.generator.DungeonGenerator;
import com.bmisiek.game.event.DungeonEmptyEvent;
import com.bmisiek.game.exception.InvalidActionException;
import com.bmisiek.game.player.Player;
import com.bmisiek.game.player.PlayerFactory;
import com.bmisiek.game.player.PlayerManager;
import com.bmisiek.printer.contract.GameAction;
import com.bmisiek.printer.contract.GuiInterface;
import com.bmisiek.printer.contract.actions.InventoryAction;
import com.bmisiek.printer.contract.actions.MoveAction;
import com.bmisiek.printer.contract.actions.SearchAction;
import com.bmisiek.printer.contract.actions.UseItemAction;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;

@Service
public class GameLoop implements ApplicationListener<DungeonEmptyEvent> {
    private final GuiInterface gui;
    private final DungeonManagerInterface dungeonManager;
    private final PlayerManager playerManager;
    private boolean isCancelled = false;
    private final Player player;

    public GameLoop(
            GuiInterface gui,
            DungeonManagerInterface dungeonManager,
            DungeonGenerator generator,
            PlayerFactory playerFactory,
            PlayerManager playerManager
    ) {
        this.gui = gui;
        this.playerManager = playerManager;
        this.player = playerFactory.createPlayer();

        dungeonManager.setDungeon(generator.createDungeon());
        this.dungeonManager = dungeonManager;
    }

    public void run() {
        dungeonManager.enter(player);

        while(!isCancelled) {
            this.gui.Update(dungeonManager);
            GameAction action;
            
            // Keep requesting actions until we get one that consumes a turn
            do {
                action = this.gui.Act(dungeonManager, player);
                handleAction(action);
            } while (!action.consumesTurn() && !isCancelled);
        }
    }

    private void handleAction(GameAction action) {
        try {
            if (action.isMovement()) {
                dungeonManager.tryMove(action.getMovement());
            } else if (action instanceof SearchAction) {
                dungeonManager.searchRoom();
            } else if (action instanceof UseItemAction useItemAction) {
                String result = playerManager.useItem(player, useItemAction.getItemIndex());
                System.out.println(result);
            } else if (action instanceof InventoryAction) {
                // Inventory is handled directly in ConsoleInterface
                // This is just a non-turn-consuming action
            }
        } catch (InvalidActionException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void onApplicationEvent(@NotNull DungeonEmptyEvent event) {
        this.isCancelled = true;
    }
}
