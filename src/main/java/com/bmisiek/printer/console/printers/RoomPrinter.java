package com.bmisiek.printer.console.printers;

import com.bmisiek.game.room.Room;
import com.bmisiek.printer.console.printers.room.NullRoomPrinter;
import com.bmisiek.printer.console.printers.room.Room3x3;
import com.bmisiek.printer.console.printers.room.RoomPrinterInterface;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class RoomPrinter {
    private final List<RoomPrinterInterface<?>> printers;

    private RoomPrinter(List<RoomPrinterInterface<?>> printers) {
        this.printers = printers;
    }

    public <T extends Room> Room3x3 Print(T room) throws NoSuchElementException {
        var printer = printers.stream().filter(p -> p.Supports(room.getClass()));
        return printer.findFirst().orElseThrow().Print(room);
    }
}
