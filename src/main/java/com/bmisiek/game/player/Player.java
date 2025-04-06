package com.bmisiek.game.player;

import lombok.Getter;

public class Player {
    @Getter
    private int health;

    public Player(int health) {}

    public void takeDamage(int damage) {
        this.health -= damage;
    }
}
