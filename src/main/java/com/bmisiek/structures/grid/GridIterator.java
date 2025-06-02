package com.bmisiek.structures.grid;

import com.bmisiek.structures.Point;
import org.jetbrains.annotations.Nullable;
import org.springframework.data.util.Pair;

import java.util.Iterator;
import java.util.Optional;

public class GridIterator<T> implements Iterator<Pair<Optional<T>, Point>> {
    private final Grid<T> grid;

    public GridIterator(Grid<T> grid) {
        this.grid = grid;
    }

    int xIdx = 0;
    int yIdx = 0;

    private Integer getXSize() {
        return grid.getXSize();
    }

    private Integer getYSize() {
        return grid.getYSize();
    }

    @Override
    public boolean hasNext() {
        return xIdx < getXSize() && yIdx < getYSize();
    }

    @Override
    public @Nullable Pair<Optional<T>, Point> next() {
        var pair = Pair.of(grid.getAt(xIdx, yIdx), new Point(xIdx, yIdx));
        xIdx = (xIdx + 1) % grid.getXSize();
        if(xIdx == 0) {
            yIdx++;
        }

        return pair;
    }
}
