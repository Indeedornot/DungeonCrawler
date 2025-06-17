package com.bmisiek.game.item;

import com.bmisiek.game.player.Player;
import lombok.Getter;

@Getter
public abstract class Item {
    private final String name;
    private final String description;
    
    protected Item(String name, String description) {
        this.name = name;
        this.description = description;
    }
    
    /**
     * @param player The player to use the item on
     * @return True if the item was consumed, false otherwise
     */
    public boolean use(Player player) {
        return isConsumable();
    }
    
    public abstract boolean isConsumable();
}
