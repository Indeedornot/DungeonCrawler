package com.bmisiek.structures.grid;

import org.jetbrains.annotations.Nullable;

import java.util.Iterator;

public class GridIterator<T> implements Iterator<T> {
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
    public @Nullable T next() {
        var item = grid.getAt(xIdx, yIdx);
        xIdx = (xIdx + 1) % grid.getXSize();
        if(xIdx == 0) {
            yIdx++;
        }

        return item;
    }
}
