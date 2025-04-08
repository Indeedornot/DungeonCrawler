package com.bmisiek.printer.console.printers;

import com.bmisiek.game.dungeon.Dungeon;
import com.bmisiek.game.dungeon.DungeonInterface;
import com.bmisiek.game.player.Player;
import org.springframework.stereotype.Service;

@Service
public class DungeonPlayerPrinter {
    public DungeonPlayerPrinter() {}

    public void Print(DungeonInterface dungeon, Player player) {
        var playerPosition = dungeon.getPosition(player);
        System.out.println("Player info: ");
        System.out.println(" position: " + playerPosition);
        System.out.println(" health: " + player.getHealth());
    }
}
