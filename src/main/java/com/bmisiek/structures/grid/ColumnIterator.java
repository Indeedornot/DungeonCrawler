package com.bmisiek.structures.grid;

import java.util.Iterator;
import java.util.Optional;

public class ColumnIterator<T> implements Iterator<Optional<T>[]> {
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
    public Optional<T>[] next() {
        var column = grid.getColumn(xIdx);
        xIdx++;
        return column;
    }
}
