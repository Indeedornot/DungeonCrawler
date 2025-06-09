package com.bmisiek.structures;

import lombok.Getter;

import java.util.Objects;

@Getter
public class Point {
    private final int x;

    private final int y;

    public Point(
            int x,
            int y
    ) {
        this.x = x;
        this.y = y;
    }

    public int getDistance(Point point) {
        return Math.abs(x - point.x) + Math.abs(y - point.y);
    }

    public double getLength() {
        return Math.sqrt(x*x + y*x);
    }

    public Point add(Point point) {
        return new Point(this.x + point.x, this.y + point.y);
    }

    public Point subtract(Point point) {
        return new Point(this.x - point.x, this.y - point.y);
    }

    @Override
    public String toString() {
        return "Point{x=%d, y=%d}".formatted(x, y);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Point point = (Point) o;
        return x == point.x && y == point.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    public static final Point Right = new Point(1, 0);
    public static final Point Left = new Point(-1, 0);
    public static final Point Up = new Point(0, -1);
    public static final Point Down = new Point(0, 1);
}
