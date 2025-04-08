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
