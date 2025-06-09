package com.bmisiek.game.player;

import com.bmisiek.game.item.Item;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
class GamePlayer extends Player {
    private final int identifier;
    @Setter
    private int health;
    private final List<Item> inventory = new ArrayList<>();
    @Setter
    private double healingAmplifier = 1.0; // Default is no amplification

    GamePlayer(int identifier, int health) {
        this.identifier = identifier;
        this.health = health;
    }

    @Override
    public List<Item> getInventory() {
        return inventory;
    }

    @Override
    public void addItem(Item item) {
        inventory.add(item);
    }

    @Override
    public void removeItem(Item item) {
        if (!inventory.contains(item)) {
            throw new IllegalArgumentException("Item not found in inventory: " + item);
        }

        inventory.remove(item);
    }

    @Override
    public int hashCode() {
        return identifier;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        GamePlayer that = (GamePlayer) o;
        return identifier == that.identifier;
    }
}
