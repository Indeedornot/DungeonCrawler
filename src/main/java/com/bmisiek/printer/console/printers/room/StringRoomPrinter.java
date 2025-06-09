package com.bmisiek.printer.console.printers.room;

import com.bmisiek.game.room.*;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

@Service
public class StringRoomPrinter extends AbstractRoomPrinter implements RoomPrinterInterface<Room> {
    private static final Map<Class<? extends Room>, String> symbols = new HashMap<>() {
        { put(SpikyRoom.class, "-"); }
        { put(SpawnRoom.class, "?"); }
        { put(HospitalRoom.class, "+"); }
        { put(CorridorRoom.class, "x"); }
        { put(NullRoom.class, "."); }
        { put(TreasureRoom.class, "$"); }
    };

    @Override
    public boolean Supports(Class<? extends Room> roomClass) {
        return symbols.keySet().stream().anyMatch(x -> x == roomClass);
    }

    @Override
    public @NotNull Room3x3 Print(Room room) {
        var symbol = symbols.get(room.getClass());
        return fromString(MessageFormat.format("""
                  {0}{0}{0}
                  {0}{0}{0}
                  {0}{0}{0}
                """, symbol));
    }
}
