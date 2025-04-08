package com.bmisiek.printer.console.printers.room;

import com.bmisiek.game.room.Room;
import com.bmisiek.game.room.SpawnRoom;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

@Service
public class SpawnPrinter extends AbstractRoomPrinter implements RoomPrinterInterface<SpawnRoom> {
    @Override
    public boolean Supports(Class<? extends Room> roomClass) {
        return roomClass == SpawnRoom.class;
    }

    @Override
    public @NotNull Room3x3 Print(Room room) {
        return fromString("""
                ???
                ???
                ???
                """);
    }
}
