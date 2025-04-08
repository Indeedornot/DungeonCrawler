package com.bmisiek.printer.console.printers;

import com.bmisiek.game.basic.Point;
import com.bmisiek.game.dungeon.Dungeon;
import com.bmisiek.game.dungeon.DungeonInterface;
import com.bmisiek.game.room.NullRoom;
import com.bmisiek.printer.console.printers.room.Room3x3;
import com.bmisiek.printer.contract.PrinterInterface;
import com.bmisiek.structures.Grid;
import com.bmisiek.structures.RangeExtension;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Range;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.stream.Collectors;

@Service
public class DungeonPrinter implements PrinterInterface<DungeonInterface> {
    public RoomPrinter roomPrinter;

    public DungeonPrinter(RoomPrinter roomPrinter) {
        this.roomPrinter = roomPrinter;
    }

    @Override
    public boolean Supports(Class<?> className) {
        return className == Dungeon.class;
    }

    private Pair<Range<Integer>, Range<Integer>> GetRoomGridSpan(DungeonInterface dungeon) {
        var keyStream = dungeon.getRooms().keySet();

        var xs = keyStream.stream().map(Point::getX).sorted().toList();
        var maxX = xs.getLast();
        var minX = xs.getFirst();

        var ys = keyStream.stream().map(Point::getY).sorted().toList();
        var maxY = ys.getLast();
        var minY = ys.getFirst();

        return Pair.of(Range.closed(minX, maxX), Range.closed(minY, maxY));
    }

    /**
     * Ensures that values in ranges starting from non-zero numbers are correctly adjusted to 0
     */
    private Integer adjustFromLowerBoundTo0(Range<Integer> range, int value) {
        var lowerBound = range.getLowerBound().getValue().orElseThrow();
        return value - lowerBound;
    }

    private Point adjustPointTo0x0(Point point, Range<Integer> xRange, Range<Integer> yRange) {
        var adjustedX = adjustFromLowerBoundTo0(xRange, point.getX());
        var adjustedY = adjustFromLowerBoundTo0(yRange, point.getY());
        return new Point(adjustedX, adjustedY);
    }

    /**
     * Due to console working in sequential square based way - printing from left top to right bottom
     * We need to convert hashmap of rooms to 2d array
     */
    @Override
    public void Print(DungeonInterface object) {
        PrintRooms(object);
    }

    private void PrintRooms(DungeonInterface object) {
        var grid = GetRoomRepresentations(object);
        grid.getRowIterator().forEachRemaining(DungeonPrinter::PrintRoomRow);
    }

    /*
        Prints a row by going through each row
        [
            1. -> xxx xxx xxx
            2. -> xxx xxx xxx
            3. -> xxx xxx xxx
        ]
     */
    private static void PrintRoomRow(Room3x3[] roomRow) {
        for(int subRow = 0; subRow < 3; subRow++) {
            var line = GetGridRow(roomRow, subRow);
            System.out.println(line);
        }
    }

    private static @NotNull String GetGridRow(Room3x3[] roomRow, int subRow) {
        return Arrays.stream(roomRow)
                .map(room -> String.join("", room.GetRow(subRow)))
                .collect(Collectors.joining());
    }

    /*
        Maps Room hashmap to a 2d grid, adjusting room positions to 0x0 to allow for uniform printing
     */
    private Grid<Room3x3> GetRoomRepresentations(DungeonInterface object) {
        var span  = GetRoomGridSpan(object);
        int xSpan = RangeExtension.getLength(span.getFirst());
        int ySpan = RangeExtension.getLength(span.getSecond());

        var grid = new Grid<>(Room3x3.class, xSpan, ySpan);

        for(var pair: object.getRooms().entrySet()) {
            var adjustedPoint = adjustPointTo0x0(pair.getKey(), span.getFirst(), span.getSecond());
            var roomRepresentation = roomPrinter.Print(pair.getValue());
            grid.setAt(adjustedPoint, roomRepresentation);
        }

        FillEmptyWithNullRooms(xSpan, ySpan, grid);

        return grid;
    }

    private void FillEmptyWithNullRooms(int xSpan, int ySpan, Grid<Room3x3> grid) {
        for(int i = 0; i < xSpan; i++) {
            for(int j = 0; j < ySpan; j++) {
                if(!grid.has(i, j)) {
                    grid.setAt(i, j, roomPrinter.Print(new NullRoom()));
                }
            }
        }
    }
}
