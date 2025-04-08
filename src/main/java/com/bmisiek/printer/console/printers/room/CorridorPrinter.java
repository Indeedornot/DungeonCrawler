package com.bmisiek.printer.console.printers.room;

import com.bmisiek.game.room.CorridorRoom;
import com.bmisiek.game.room.Room;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class CorridorPrinter implements RoomPrinterInterface<CorridorRoom> {
    @Override
    public boolean Supports(Class<? extends Room> roomClass) {
        return roomClass == CorridorRoom.class;
    }

    @Override
    public @NotNull Room3x3 Print(Room room) {
       String initialDataString = """
               xxx
               xxx
               xxx""";

       var dataList = Arrays.stream(initialDataString.trim().split("[\n\r]"))
               .filter(p -> !p.isEmpty())
               .map(rowString -> rowString.split(""))
               .toArray(String[][]::new);

       return new Room3x3(dataList);
    }
}
