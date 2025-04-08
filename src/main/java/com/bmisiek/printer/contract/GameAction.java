package com.bmisiek.printer.contract;

import com.bmisiek.structures.Point;

public enum GameAction {
    MOVE_UP,
    MOVE_DOWN,
    MOVE_LEFT,
    MOVE_RIGHT;

    public boolean IsMovement() {
        return this == MOVE_UP || this == MOVE_DOWN || this == MOVE_LEFT || this == MOVE_RIGHT;
    }

    public Point GetMovement() {
        if(!IsMovement()) {
            throw new AssertionError();
        }

        return switch(this) {
            case MOVE_UP -> Point.Up;
            case MOVE_DOWN -> Point.Down;
            case MOVE_LEFT -> Point.Left;
            case MOVE_RIGHT -> Point.Right;
        };
    }
}
