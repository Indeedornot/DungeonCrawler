package com.bmisiek.game.gameloop;

import com.bmisiek.game.dungeon.generator.DungeonGenerator;
import com.bmisiek.game.player.PlayerFactory;
import com.bmisiek.printer.contract.GuiInterface;
import org.springframework.stereotype.Service;

@Service
public class GameLoopFactory {
    private final GuiInterface gui;
    private final DungeonGenerator dungeonGenerator;
    private final PlayerFactory playerFactory;

    public GameLoopFactory(
            GuiInterface gui,
            DungeonGenerator dungeonGenerator,
            PlayerFactory playerFactory
    ) {
        this.gui = gui;
        this.dungeonGenerator = dungeonGenerator;
        this.playerFactory = playerFactory;
    }

    public GameLoop createGameLoop() {
        return new GameLoop(
                gui,
                playerFactory.createPlayer(),
                dungeonGenerator.createDungeon()
        );
    }
}
