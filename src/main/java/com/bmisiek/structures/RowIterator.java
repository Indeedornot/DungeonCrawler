package com.bmisiek.structures;

import java.util.Iterator;

public class RowIterator<T> implements Iterator<T[]> {
    private final Grid<T> grid;
    int yIdx = 0;

    public RowIterator(Grid<T> grid) {
        this.grid = grid;
    }

    @Override
    public boolean hasNext() {
        return yIdx < grid.getYSize();
    }

    @Override
    public T[] next() {
        var row = grid.GetRow(yIdx);
        yIdx++;
        return row;
    }
}
