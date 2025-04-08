package com.bmisiek.printer.console.printers;

import com.bmisiek.structures.Point;
import com.bmisiek.game.dungeon.DungeonInterface;
import com.bmisiek.structures.RangeExtension;
import lombok.Getter;
import org.springframework.data.domain.Range;

@Getter
public class DungeonInfo {
    private final DungeonInterface dungeon;

    private Range<Integer> xBound;
    private int xSize;

    private Range<Integer> yBound;
    private int ySize;

    private final Point furthestPoint;

    public DungeonInfo(DungeonInterface dungeon) {
        this.dungeon = dungeon;
        this.calculateGridSpan(dungeon);
        this.xSize = RangeExtension.getLength(this.xBound);
        this.ySize = RangeExtension.getLength(this.yBound);
        this.furthestPoint = new Point(xBound.getLowerBound().getValue().orElseThrow(), yBound.getLowerBound().getValue().orElseThrow());
    }

    private void calculateGridSpan(DungeonInterface dungeon) {
        var keyStream = dungeon.getRooms().keySet();

        var xs = keyStream.stream().map(Point::getX).sorted().toList();
        this.xBound = Range.closed(xs.getFirst(), xs.getLast());

        var ys = keyStream.stream().map(Point::getY).sorted().toList();
        this.yBound = Range.closed(ys.getFirst(), ys.getLast());
    }
};