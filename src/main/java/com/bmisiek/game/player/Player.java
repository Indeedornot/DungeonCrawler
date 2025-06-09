package com.bmisiek.game.player;

import com.bmisiek.game.item.Item;

import java.util.List;

public abstract class Player {
    public abstract int getIdentifier();
    protected abstract void setHealth(int health);
    public abstract int getHealth();
    
    /**
     * Get the player's inventory of items
     */
    public abstract List<Item> getInventory();
    
    /**
     * Add an item to the player's inventory
     */
    public abstract void addItem(Item item);
    
    /**
     * Remove an item from the player's inventory
     */
    public abstract void removeItem(Item item);
    
    /**
     * Get the healing amplifier multiplier
     */
    public abstract double getHealingAmplifier();
    
    /**
     * Set the healing amplifier multiplier
     */
    public abstract void setHealingAmplifier(double multiplier);
}
