package com.bmisiek.game.player;

public abstract class Player {
    public abstract int getIdentifier();
    protected abstract void setHealth(int health);
    public abstract int getHealth();
}
