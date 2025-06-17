package com.bmisiek.structures;

import org.jetbrains.annotations.NotNull;

public record Point(int x, int y) {

    public int getDistance(Point point) {
        return Math.abs(x - point.x) + Math.abs(y - point.y);
    }

    public Point add(Point point) {
        return new Point(this.x + point.x, this.y + point.y);
    }

    public Point subtract(Point point) {
        return new Point(this.x - point.x, this.y - point.y);
    }

    @Override
    public @NotNull String toString() {
        return "Point{x=%d, y=%d}".formatted(x, y);
    }

    public static final Point Right = new Point(1, 0);
    public static final Point Left = new Point(-1, 0);
    public static final Point Up = new Point(0, -1);
    public static final Point Down = new Point(0, 1);
}
