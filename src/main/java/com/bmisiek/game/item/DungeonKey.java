package com.bmisiek.game.item;

public class DungeonKey extends Item {
    
    public DungeonKey() {
        super("Dungeon Key", "Key required to unlock the exit to the next floor");
    }
    
    @Override
    public boolean isConsumable() {
        return true;
    }
}
