package com.bmisiek.game.gameloop;

import com.bmisiek.game.dungeon.DungeonManagerInterface;
import com.bmisiek.game.dungeon.generator.DungeonGenerator;
import com.bmisiek.game.event.DungeonEmptyEvent;
import com.bmisiek.game.exception.InvalidActionException;
import com.bmisiek.game.player.Player;
import com.bmisiek.game.player.PlayerFactory;
import com.bmisiek.printer.contract.GameAction;
import com.bmisiek.printer.contract.GuiInterface;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;

@Service
public class GameLoop implements ApplicationListener<DungeonEmptyEvent> {
    private final GuiInterface gui;
    private final DungeonManagerInterface dungeonManager;
    private boolean isCancelled = false;
    private final Player player;

    public GameLoop(
            GuiInterface gui,
            DungeonManagerInterface dungeonManager,
            DungeonGenerator generator,
            PlayerFactory playerFactory
    ) {
        this.gui = gui;
        this.player = playerFactory.createPlayer();

        dungeonManager.setDungeon(generator.createDungeon());
        this.dungeonManager = dungeonManager;
    }

    public void run() {
        dungeonManager.enter(player);

        while(!isCancelled) {
            this.gui.Update(dungeonManager);
            var action = this.gui.Act(dungeonManager, player);
            tryPerformAction(action);
        }
    }

    private void tryPerformAction(GameAction action) {
        try {
            if(action.IsMovement()) {
                dungeonManager.tryMove(action.GetMovement());
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
