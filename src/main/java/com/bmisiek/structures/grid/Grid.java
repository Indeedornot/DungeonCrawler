package com.bmisiek.structures.grid;

import com.bmisiek.game.basic.Point;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Iterator;

/**
 * 2d array grid wrapper meant for removing complexity of remembering coordinate placement (x,y; y,x)
 */
public class Grid<T> implements Iterable<T> {
    @Getter
    protected int xSize;

    @Getter
    protected int ySize;

    private final T[][] grid;

    private final Class<T> type;

    public Grid(Class<T> type, int xSize, int ySize) {
        this.xSize = xSize;
        this.ySize = ySize;
        this.type = type;

        this.grid = (T[][]) Array.newInstance(type, xSize, ySize);
    }

    private void AssertValidArrayForGrid(T[][] array) {
        if(array.length == 0 || array[0].length == 0) {
            throw new IllegalArgumentException("Array provided to grid should be a 2d array of at least one element");
        }

        var lengths = Arrays.stream(array).map(el -> el.length);
        boolean areAllEqualLength = lengths.allMatch(length -> length == array[0].length);
        if(!areAllEqualLength) {
            throw new IllegalArgumentException("Array provided to grid should have be a rectangle 2d array");
        }
    }

    public Grid(Class<T> type, T[][] grid) {
        AssertValidArrayForGrid(grid);

        this.grid = grid;
        this.xSize = grid[0].length;
        this.ySize = grid.length;
        this.type = type;
    }

    public void setAt(int x, int y, T value) {
        grid[x][y] = value;
    }

    public void setAt(Point point, T value) {
        this.setAt(point.getX(), point.getY(), value);
    }

    public @Nullable T getAt(Point point) {
        return this.getAt(point.getX(), point.getY());
    }

    public @Nullable T getAt(int x, int y) {
        return grid[x][y];
    }

    public boolean has(int x, int y) {
        return grid[x][y] != null;
    }

    public boolean has(Point point) {
        return this.has(point.getX(), point.getY());
    }

    @Override
    public @NotNull Iterator<T> iterator() {
        return new GridIterator<>(this);
    }

    public @NotNull Iterator<T[]> getRowIterator() {
        return new RowIterator<>(this);
    }

    public @NotNull Iterable<T[]> GetRows() {
        return new Iterable<>() {
            @Override
            public @NotNull Iterator<T[]> iterator() {
                return getRowIterator();
            }
        };
    }

    public @NotNull Iterable<T[]> GetColumns() {
        return new Iterable<>() {
            @Override
            public @NotNull Iterator<T[]> iterator() {
                return getColumnIterator();
            }
        };
    }

    public @NotNull Iterator<T[]> getColumnIterator() {
        return new ColumnIterator<>(this);
    }

    public T[] GetRow(int y) {
        @SuppressWarnings("unchecked")
        var items = (T[]) Array.newInstance(type, xSize);
        for (int x = 0; x < xSize; x++) {
            items[x] = grid[x][y];
        }

        return items;
    }

    public T[] GetColumn(int x) {
        @SuppressWarnings("unchecked")
        var items = (T[]) Array.newInstance(type, ySize);
        for(int y = 0; y < ySize; y++) {
            items[y] = grid[x][y];
        }

        return items;
    }
}
