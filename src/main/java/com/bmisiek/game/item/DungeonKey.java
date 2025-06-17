package com.bmisiek.game.item;

import com.bmisiek.game.player.Player;

public class DungeonKey extends Item {
    
    public DungeonKey() {
        super("Dungeon Key", "Key required to unlock the exit to the next floor");
    }
    
    @Override
    public boolean use(Player player) {
        return false;
    }
    
    @Override
    public boolean isConsumable() {
        return true;
    }
}
