package com.bmisiek.printer.console.printers.room;

import com.bmisiek.structures.IteratorExtension;
import com.bmisiek.structures.grid.Grid;
import lombok.Getter;

import java.util.Arrays;
import java.util.Optional;

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

    public String toString() {
        return String.join("\n", IteratorExtension.toStream(this.getRows())
                .map(row -> String.join("", Arrays.stream(row).map(Optional::orElseThrow).toArray(String[]::new)))
                .toArray(String[]::new));
    }
}
