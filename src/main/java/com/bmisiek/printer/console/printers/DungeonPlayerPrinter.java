package com.bmisiek.printer.console.printers;

import com.bmisiek.game.dungeon.DungeonManagerInterface;
import com.bmisiek.game.player.Player;
import org.springframework.stereotype.Service;

@Service
public class DungeonPlayerPrinter {
    public DungeonPlayerPrinter() {}

    public void Print(DungeonManagerInterface dungeon) {
        System.out.println("Player info: ");
        System.out.println(" position: " + dungeon.getPlayerPosition());
        System.out.println(" health: " + dungeon.getPlayer().getHealth());
    }
}
