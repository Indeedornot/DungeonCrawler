package com.bmisiek.printer.console.printers.room;

import com.bmisiek.game.room.HospitalRoom;
import com.bmisiek.game.room.Room;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

@Service
public class HospitalRoomPrinter extends AbstractRoomPrinter implements RoomPrinterInterface<HospitalRoom> {
    @Override
    public boolean Supports(Class<? extends Room> roomClass) {
        return roomClass == HospitalRoom.class;
    }

    @Override
    public @NotNull Room3x3 Print(Room room) {
        return fromString("""
                +++
                +++
                +++
                """);
    }
}
