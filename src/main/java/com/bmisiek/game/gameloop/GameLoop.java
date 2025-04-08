package com.bmisiek.game.gameloop;

import com.bmisiek.game.dungeon.DungeonInterface;
import com.bmisiek.game.event.DungeonEmptyEvent;
import com.bmisiek.game.player.Player;
import com.bmisiek.printer.contract.GuiInterface;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationListener;

public class GameLoop implements ApplicationListener<DungeonEmptyEvent> {
    private final GuiInterface gui;
    private final Player player;
    private final DungeonInterface dungeon;
    private boolean isCancelled = false;

    public GameLoop(
            GuiInterface gui,
            Player player,
            DungeonInterface dungeon
    ) {
        this.gui = gui;
        this.player = player;
        this.dungeon = dungeon;
    }

    public void run() {
        dungeon.enter(player);

        while(!isCancelled) {
            this.gui.Update(dungeon);
        }
    }

    @Override
    public void onApplicationEvent(@NotNull DungeonEmptyEvent event) {
        this.isCancelled = true;
    }
}
