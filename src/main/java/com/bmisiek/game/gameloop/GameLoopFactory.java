package com.bmisiek.game.gameloop;

import com.bmisiek.game.dungeon.generator.DungeonGenerator;
import com.bmisiek.game.player.PlayerFactory;
import org.springframework.stereotype.Service;

@Service
public class GameLoopFactory {
    private final DungeonGenerator dungeonGenerator;
    private final PlayerFactory playerFactory;

    public GameLoopFactory(
            DungeonGenerator dungeonGenerator,
            PlayerFactory playerFactory
    ) {
        this.dungeonGenerator = dungeonGenerator;
        this.playerFactory = playerFactory;
    }

    public GameLoop createGameLoop() {
        return new GameLoop(
                playerFactory.createPlayer(),
                dungeonGenerator.createDungeon()
        );
    }
}
