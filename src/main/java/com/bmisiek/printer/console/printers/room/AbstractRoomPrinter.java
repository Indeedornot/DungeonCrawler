package com.bmisiek.printer.console.printers.room;

import com.bmisiek.game.exception.InvalidActionException;
import org.springframework.stereotype.Service;

import java.util.Arrays;

public abstract class AbstractRoomPrinter {
    /**
     * transforms 3 character x 3 lines string into a Room3x3
     */
    protected Room3x3 fromString(String string) {
        return new Room3x3(Arrays.stream(string.trim().split("[\n\r]"))
                .filter(p -> !p.isEmpty())
                .map(rowString -> rowString.split(""))
                .toArray(String[][]::new));
    }
}
