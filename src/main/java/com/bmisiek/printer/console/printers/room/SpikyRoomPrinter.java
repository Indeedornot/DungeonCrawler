package com.bmisiek.printer.console.printers.room;

import com.bmisiek.game.room.Room;
import com.bmisiek.game.room.SpikyRoom;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

@Service
public class SpikyRoomPrinter extends AbstractRoomPrinter implements RoomPrinterInterface<SpikyRoom> {

    @Override
    public boolean Supports(Class<? extends Room> roomClass) {
        return roomClass == SpikyRoom.class;
    }

    @Override
    public @NotNull Room3x3 Print(Room room) {
        return fromString("""
                ---
                ---
                ---
                """);
    }
}
