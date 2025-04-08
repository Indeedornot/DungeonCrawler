package com.bmisiek.printer.console.printers.room;

import com.bmisiek.structures.grid.Grid;
import lombok.Getter;

@Getter
public class Room3x3 extends Grid<String> {
    public Room3x3() {
        super(String.class, 3, 3);
    }

    public Room3x3(String[][] grid) {
        super(String.class, grid);
        if(grid.length != 3) {
            throw new IllegalArgumentException("Grid size must be 3x3");
        }
    }
}
