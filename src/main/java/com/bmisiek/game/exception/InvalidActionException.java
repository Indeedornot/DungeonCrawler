package com.bmisiek.game.exception;

public class InvalidActionException extends IllegalStateException {
    public InvalidActionException(String message) {
        super(message);
    }
}
