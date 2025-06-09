package com.bmisiek.structures.grid;

import com.bmisiek.structures.Point;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.util.Pair;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Optional;

/**
 * 2d array grid wrapper meant for removing complexity of remembering coordinate placement (x,y; y,x)
 */
public class Grid<T> implements Iterable<Pair<Optional<T>, Point>> {
    @Getter
    protected final int xSize;

    @Getter
    protected final int ySize;

    private final Optional<T>[][] grid;

    private final Class<T> type;

    /**
     * Constructs a new empty grid with specified dimensions.
     *
     * @param type  the class type of elements to be stored
     * @param xSize the width of the grid (x dimension)
     * @param ySize the height of the grid (y dimension)
     */
    @SuppressWarnings("unchecked")
    public Grid(Class<T> type, int xSize, int ySize) {
        if (xSize <= 0 || ySize <= 0) {
            throw new IllegalArgumentException("Grid dimensions must be positive");
        }

        this.xSize = xSize;
        this.ySize = ySize;
        this.type = type;
        this.grid = (Optional<T>[][]) Array.newInstance(Optional.class, xSize, ySize);

        // Initialize all positions with empty Optional
        for (int x = 0; x < xSize; x++) {
            for (int y = 0; y < ySize; y++) {
                grid[x][y] = Optional.empty();
            }
        }
    }

    /**
     * Constructs a grid from an existing 2D Optional array.
     *
     * @param type the class type of elements
     * @param grid the 2D array to use as the grid
     * @throws IllegalArgumentException if the array is empty or not rectangular
     */
    public Grid(Class<T> type, Optional<T>[][] grid) {
        validateGridArray(grid);

        this.grid = grid;
        this.xSize = grid.length;
        this.ySize = grid[0].length;
        this.type = type;
    }

    /**
     * Constructs a grid from an existing 2D Optional array.
     *
     * @param type the class type of elements
     * @param grid the 2D array to use as the grid
     * @throws IllegalArgumentException if the array is empty or not rectangular
     */
    @SuppressWarnings("unchecked")
    public Grid(Class<T> type, T[][] grid) {
        var optGrid = (Optional<T>[][]) Arrays.stream(grid)
                .map(row -> Arrays.stream(row)
                        .map(Optional::ofNullable)
                        .toArray(Optional[]::new))
                .toArray(Optional[][]::new);
        validateGridArray(optGrid);

        this.grid = optGrid;
        this.xSize = grid.length;
        this.ySize = grid[0].length;
        this.type = type;
    }

    /**
     * Validates that the input array is a proper rectangular 2D array.
     */
    private void validateGridArray(Optional<T>[][] array) {
        if (array.length == 0 || array[0].length == 0) {
            throw new IllegalArgumentException("Grid array must have at least one element");
        }

        boolean allRowsSameLength = Arrays.stream(array)
                .allMatch(row -> row.length == array[0].length);

        if (!allRowsSameLength) {
            throw new IllegalArgumentException("Grid array must be rectangular");
        }
    }

    // Value setting methods
    public void setAt(int x, int y, T value) {
        checkBounds(x, y);
        grid[x][y] = Optional.ofNullable(value);
    }

    public void setAt(int x, int y, @NotNull Optional<T> value) {
        checkBounds(x, y);
        grid[x][y] = value;
    }

    public void setAt(Point point, T value) {
        setAt(point.getX(), point.getY(), value);
    }

    public void setAt(Point point, @NotNull Optional<T> value) {
        setAt(point.getX(), point.getY(), value);
    }

    public Optional<T> getAt(Point point) {
        return getAt(point.getX(), point.getY());
    }

    public Optional<T> getAt(int x, int y) {
        checkBounds(x, y);
        return grid[x][y];
    }

    public boolean has(int x, int y) {
        checkBounds(x, y);
        return grid[x][y].isPresent();
    }

    public boolean has(Point point) {
        return has(point.getX(), point.getY());
    }

    /**
     * Inserts a subgrid into this grid at the specified position.
     *
     * @param x       the x-coordinate of the insertion point
     * @param y       the y-coordinate of the insertion point
     * @param subgrid the grid to insert
     * @throws IllegalArgumentException if the subgrid would extend beyond this grid's bounds
     */
    public void insertSubgrid(int x, int y, Grid<T> subgrid) {
        if (x < 0 || y < 0 ||
                x + subgrid.xSize > this.xSize ||
                y + subgrid.ySize > this.ySize) {
            throw new IllegalArgumentException("Subgrid exceeds grid boundaries");
        }

        for (int subX = 0; subX < subgrid.xSize; subX++) {
            for (int subY = 0; subY < subgrid.ySize; subY++) {
                this.setAt(x + subX, y + subY, subgrid.getAt(subX, subY));
            }
        }
    }

    public void insertSubgrid(Point point, Grid<T> subgrid) {
        insertSubgrid(point.getX(), point.getY(), subgrid);
    }

    @Override
    public @NotNull Iterator<Pair<Optional<T>, Point>> iterator() {
        return new GridIterator<>(this);
    }

    public @NotNull Iterator<Optional<T>[]> getRowIterator() {
        return new RowIterator<>(this);
    }

    public @NotNull Iterator<Optional<T>[]> getColumnIterator() {
        return new ColumnIterator<>(this);
    }

    public @NotNull Iterable<Optional<T>[]> getRows() {
        return this::getRowIterator;
    }

    public @NotNull Iterable<Optional<T>[]> getColumns() {
        return this::getColumnIterator;
    }

    // Row/column access methods
    @SuppressWarnings("unchecked")
    public Optional<T>[] getRow(int y) {
        checkBounds(0, y);  // Just check y coordinate
        Optional<T>[] row = (Optional<T>[]) Array.newInstance(Optional.class, xSize);
        for (int x = 0; x < xSize; x++) {
            row[x] = grid[x][y];
        }
        return row;
    }

    @SuppressWarnings("unchecked")
    public Optional<T>[] getColumn(int x) {
        checkBounds(x, 0);  // Just check x coordinate
        Optional<T>[] column = (Optional<T>[]) Array.newInstance(Optional.class, ySize);
        System.arraycopy(grid[x], 0, column, 0, ySize);
        return column;
    }

    // Utility method for bounds checking
    private void checkBounds(int x, int y) {
        if (x < 0 || x >= xSize || y < 0 || y >= ySize) {
            String errorMessage = String.format(
                    "Coordinates (%d, %d) out of bounds for grid size (%d, %d)",
                    x, y, xSize, ySize
            );
            throw new IndexOutOfBoundsException(errorMessage);
        }
    }
}