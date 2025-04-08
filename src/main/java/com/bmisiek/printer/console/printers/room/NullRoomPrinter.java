package com.bmisiek.printer.console.printers.room;

import com.bmisiek.game.room.NullRoom;
import com.bmisiek.game.room.Room;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

@Service
public class NullRoomPrinter implements RoomPrinterInterface<NullRoom> {
    @Override
    public boolean Supports(Class<? extends Room> roomClass) {
        return roomClass == NullRoom.class;
    }

    @Override
    public @NotNull Room3x3 Print(Room room) {
        return new Room3x3(new String[][]{
                {" ", " ", " "},
                {" ", " ", " "},
                {" ", " ", " "},
        });
    }
}
