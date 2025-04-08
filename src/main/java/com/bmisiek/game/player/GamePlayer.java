package com.bmisiek.game.player;

import lombok.Getter;
import lombok.Setter;

@Getter
class GamePlayer extends Player {
    private final int identifier;
    @Setter
    private int health;

    GamePlayer(int identifier, int health) {
        this.identifier = identifier;
        this.health = health;
    }
}
