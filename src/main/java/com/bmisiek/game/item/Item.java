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
     * Use this item on the player
     * @param player The player to use the item on
     * @return True if the item was consumed, false otherwise
     */
    public abstract boolean use(Player player);
    
    /**
     * Whether this item can be used multiple times
     */
    public abstract boolean isConsumable();
}
