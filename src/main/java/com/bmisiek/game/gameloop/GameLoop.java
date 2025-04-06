package com.bmisiek.game.gameloop;

import com.bmisiek.game.dungeon.DungeonInterface;
import com.bmisiek.game.event.DungeonEmptyEvent;
import com.bmisiek.game.event.PlayerDiedEvent;
import com.bmisiek.game.player.Player;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GameLoop implements ApplicationListener<DungeonEmptyEvent> {
    private final Player player;
    private final DungeonInterface dungeon;
    private boolean isCancelled = false;

    public GameLoop(
            Player player,
            DungeonInterface dungeon
    ) {
        this.player = player;
        this.dungeon = dungeon;
    }

    public void run() {
        while(!isCancelled) {

        }
    }

    @Override
    public void onApplicationEvent(@NotNull DungeonEmptyEvent event) {
        this.isCancelled = true;
    }
}
