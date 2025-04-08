package com.bmisiek.printer.console.printers;

import com.bmisiek.game.basic.Point;
import com.bmisiek.game.dungeon.Dungeon;
import com.bmisiek.game.dungeon.DungeonInterface;
import com.bmisiek.game.room.NullRoom;
import com.bmisiek.printer.console.printers.room.Room3x3;
import com.bmisiek.printer.contract.PrinterInterface;
import com.bmisiek.structures.Grid;
import org.springframework.data.domain.Range;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

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

    private Integer getRangeLength(Range<Integer> range) {
        return Math.abs(range.getUpperBound().getValue().orElseThrow()) + Math.abs(range.getLowerBound().getValue().orElseThrow()) + 1;
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
        var grid = GetRoomRepresentations(object);

        var iterator = grid.getRowIterator();
        while (iterator.hasNext()) {
            var row = iterator.next();
            //print row by row the 3x3
            for(int i = 0; i < 3; i++) {
                for(var item : row) {
                    System.out.printf(String.join("", item.GetRow(i)));
                }
                System.out.println();
            }
        }
    }

    private Grid<Room3x3> GetRoomRepresentations(DungeonInterface object) {
        var span  = GetRoomGridSpan(object);
        int xSpan = getRangeLength(span.getFirst());
        int ySpan = getRangeLength(span.getSecond());

        var grid = new Grid<>(Room3x3.class, xSpan, ySpan);

        for(var pair: object.getRooms().entrySet()) {
            var adjustedPoint = adjustPointTo0x0(pair.getKey(), span.getFirst(), span.getSecond());
            var roomRepresentation = roomPrinter.Print(pair.getValue());
            grid.setAt(adjustedPoint, roomRepresentation);
        }

        for(int i = 0; i < xSpan; i++) {
            for(int j = 0; j < ySpan; j++) {
                if(!grid.has(i, j)) {
                    grid.setAt(i, j, roomPrinter.Print(new NullRoom()));
                }
            }
        }

        return grid;
    }
}
