package com.bmisiek.structures.grid;

import java.util.Iterator;
import java.util.Optional;

public class RowIterator<T> implements Iterator<Optional<T>[]> {
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
    public Optional<T>[] next() {
        var row = grid.getRow(yIdx);
        yIdx++;
        return row;
    }
}
