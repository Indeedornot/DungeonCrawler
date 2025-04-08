package com.bmisiek.structures;

import java.util.Iterator;

public class ColumnIterator<T> implements Iterator<T[]> {
    private final Grid<T> grid;
    int xIdx = 0;

    public ColumnIterator(Grid<T> grid) {
        this.grid = grid;
    }

    @Override
    public boolean hasNext() {
        return xIdx < grid.getYSize();
    }

    @Override
    public T[] next() {
        var column = grid.GetColumn(xIdx);
        xIdx++;
        return column;
    }
}
