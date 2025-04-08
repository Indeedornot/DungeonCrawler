package com.bmisiek.printer.console.printers.room;

import com.bmisiek.game.room.Room;
import org.jetbrains.annotations.NotNull;

public interface RoomPrinterInterface<T extends Room> {
    boolean Supports(Class<? extends Room> roomClass);

    @NotNull
    Room3x3 Print(Room room);
}
