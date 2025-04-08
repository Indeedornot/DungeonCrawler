package com.bmisiek.printer.console.printers;

import com.bmisiek.structures.Point;
import com.bmisiek.game.dungeon.Dungeon;
import com.bmisiek.game.dungeon.DungeonManagerInterface;
import com.bmisiek.game.room.NullRoom;
import com.bmisiek.game.room.Room;
import com.bmisiek.printer.console.printers.room.Room3x3;
import com.bmisiek.printer.contract.PrinterInterface;
import com.bmisiek.structures.grid.Grid;
import com.bmisiek.structures.IteratorExtension;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class DungeonPrinter implements PrinterInterface<DungeonManagerInterface> {

    private static final int ROOM_HEIGHT = 3; // Based on Room3x3
    private static final int ROOM_WIDTH = 3;  // Based on Room3x3
    private static final int COORDINATE_AXIS_PADDING = 5; // Space reserved for Y-axis labels
    private static final String LINE_SEPARATOR = "\n";

    private final RoomPrinter roomPrinter;

    // --- Constructor ---
    public DungeonPrinter(RoomPrinter roomPrinter) {
        this.roomPrinter = roomPrinter;
    }

    @Override
    public boolean Supports(Class<?> className) {
        return className == Dungeon.class;
    }

    @Override
    public void Print(DungeonManagerInterface dungeon) {
        if (dungeon == null || dungeon.getRooms().isEmpty()) {
            System.out.println("Dungeon is empty.");
            return;
    }

        // 1. Calculate dungeon layout information
        DungeonLayoutInfo layoutInfo = calculateLayoutInfo(dungeon);

        // 2. Create a grid representation of the dungeon rooms
        Grid<Room3x3> roomGrid = createRoomGrid(dungeon, layoutInfo);

        // 3. Assemble the lines for printing from the grid
        List<String> printLines = assemblePrintLines(roomGrid);

        // 4. Add coordinate labels
        addYAxisLabels(printLines, layoutInfo);
        addXAxisLabels(printLines, layoutInfo);

        // 5. Print to console
        printLines.forEach(System.out::println);
    }

    // --- Helper Methods ---

    /**
     * Calculates the boundaries and dimensions of the dungeon layout.
     */
    private DungeonLayoutInfo calculateLayoutInfo(DungeonManagerInterface dungeon) {
        Map<Point, Room> rooms = dungeon.getRooms();

        int minX = rooms.keySet().stream().mapToInt(Point::getX).min().orElse(0);
        int maxX = rooms.keySet().stream().mapToInt(Point::getX).max().orElse(0);
        int minY = rooms.keySet().stream().mapToInt(Point::getY).min().orElse(0);
        int maxY = rooms.keySet().stream().mapToInt(Point::getY).max().orElse(0);

        Point topLeft = new Point(minX, minY);
        Point bottomRight = new Point(maxX, maxY);
        int gridWidth = maxX - minX + 1;
        int gridHeight = maxY - minY + 1;

        return new DungeonLayoutInfo(topLeft, bottomRight, gridWidth, gridHeight);
        }

    /**
     * Creates a 2D grid of Room3x3 representations from the dungeon data.
     * Coordinates are adjusted so the top-left room is at grid position (0,0).
     * Empty spaces in the grid are filled with NullRoom representations.
     */
    private Grid<Room3x3> createRoomGrid(DungeonManagerInterface dungeon, DungeonLayoutInfo layoutInfo) {
        Grid<Room3x3> grid = new Grid<>(Room3x3.class, layoutInfo.gridWidth(), layoutInfo.gridHeight());
        Point offset = layoutInfo.topLeft(); // Use this point to adjust coordinates

        // Place existing rooms onto the grid
        for (Map.Entry<Point, Room> entry : dungeon.getRooms().entrySet()) {
            Point originalPoint = entry.getKey();
            Room room = entry.getValue();

            // Adjust coordinates relative to the top-left corner
            Point gridPoint = originalPoint.substract(offset);

            Room3x3 roomRepresentation = roomPrinter.Print(room); // Assuming RoomPrinter returns Room3x3
            grid.setAt(gridPoint, roomRepresentation);
    }

        // Fill empty cells with NullRoom representations
        fillEmptyGridCells(grid, layoutInfo);

        return grid;
    }

    /**
     * Fills any empty cells in the grid with the representation of a NullRoom.
     */
    private void fillEmptyGridCells(Grid<Room3x3> grid, DungeonLayoutInfo layoutInfo) {
        Room3x3 nullRoomRepresentation = roomPrinter.Print(new NullRoom());
        for (int y = 0; y < layoutInfo.gridHeight(); y++) {
            for (int x = 0; x < layoutInfo.gridWidth(); x++) {
                if (!grid.has(x, y)) {
                    grid.setAt(x, y, nullRoomRepresentation);
                }
            }
        }
    }

    /**
     * Assembles the multi-line string output for the entire grid, row by row.
     */
    private List<String> assemblePrintLines(Grid<Room3x3> roomGrid) {
         // Assuming grid.GetRows() returns an Iterable<Room3x3[]> or similar
        return IteratorExtension.toStream(roomGrid.GetRows()) // Keep using custom extension if necessary
                .map(this::formatRoomRowAsString)
                .collect(Collectors.toCollection(ArrayList::new)); // Use ArrayList for addFirst later
    }

    /**
     * Formats a single row of rooms (Room3x3[]) into a multi-line string.
     * Example for a row of 3 rooms:
     * room1_line0 + room2_line0 + room3_line0 + "\n" +
     * room1_line1 + room2_line1 + room3_line1 + "\n" +
     * room1_line2 + room2_line2 + room3_line2
     */
    private String formatRoomRowAsString(Room3x3[] roomRow) {
        return IntStream.range(0, ROOM_HEIGHT) // Iterate through 0, 1, 2 for a 3x3 room
                .mapToObj(subRowIndex -> formatSingleLineAcrossRooms(roomRow, subRowIndex))
                .collect(Collectors.joining(LINE_SEPARATOR));
    }

    /**
     * Creates a single line of text by concatenating the corresponding sub-line
     * from each room in the row.
     * Example: Get line 1 from room1, line 1 from room2, line 1 from room3 and join them.
     */
    private String formatSingleLineAcrossRooms(Room3x3[] roomRow, int subRowIndex) {
        return Arrays.stream(roomRow)
                // Assuming Room3x3.GetRow(i) returns the String for that specific line
                .map(room -> String.join("", room.GetRow(subRowIndex)))
                .collect(Collectors.joining()); // Join lines directly side-by-side
    }

    /**
     * Adds Y-axis coordinate labels to the left of the print lines.
     * Labels are aligned with the vertical center of each room row.
     */
    private void addYAxisLabels(List<String> printLines, DungeonLayoutInfo layoutInfo) {
        String verticalPadding = " ".repeat(COORDINATE_AXIS_PADDING);
        int middleLineIndexInRoom = ROOM_HEIGHT / 2; // e.g., index 1 for height 3

        for (int gridRowIndex = 0; gridRowIndex < printLines.size(); gridRowIndex++) {
            // Calculate the original Y coordinate corresponding to this grid row
            // Assuming i is the index of the *room row* string (which contains ROOM_HEIGHT actual lines)
            // Use RangeExtension like the original, assuming it maps grid index back to world coordinate
            // int originalY = RangeExtension.AdjustToBounds(layoutInfo.getYBound(), gridRowIndex); // If YBound holds minY, maxY
             // Or calculate directly if RangeExtension isn't needed:
            int originalY = layoutInfo.topLeft().getY() + gridRowIndex;


            String yLabel = String.valueOf(originalY);

            int paddingLeft = (COORDINATE_AXIS_PADDING - yLabel.length()) / 2;
            int paddingRight = COORDINATE_AXIS_PADDING - yLabel.length() - paddingLeft;
            String labelLinePrefix = " ".repeat(paddingLeft) + yLabel + " ".repeat(paddingRight);

            // Split the multi-line room row string
            String[] subLines = printLines.get(gridRowIndex).split(LINE_SEPARATOR, -1); // Keep trailing empty strings

            // Apply padding/label
            for (int lineIdx = 0; lineIdx < subLines.length; lineIdx++) {
                if (lineIdx == middleLineIndexInRoom) {
                    subLines[lineIdx] = labelLinePrefix + subLines[lineIdx];
                } else {
                    subLines[lineIdx] = verticalPadding + subLines[lineIdx];
                }
    }

            // Join back and update the list
            printLines.set(gridRowIndex, String.join(LINE_SEPARATOR, subLines));
                }
            }

    /**
     * Adds an X-axis coordinate label row at the top of the print lines.
     * Labels are centered above each room column.
     */
     private void addXAxisLabels(List<String> printLines, DungeonLayoutInfo layoutInfo) {
        StringBuilder header = new StringBuilder();
        header.append(" ".repeat(COORDINATE_AXIS_PADDING)); // Initial padding to align with Y labels

        for (int gridColIndex = 0; gridColIndex < layoutInfo.gridWidth(); gridColIndex++) {
            // Calculate original X coordinate
             // Use RangeExtension like the original, assuming it maps grid index back to world coordinate
            // int originalX = RangeExtension.AdjustToBounds(layoutInfo.getXBound(), gridColIndex); // If XBound holds minX, maxX
            // Or calculate directly:
            int originalX = layoutInfo.topLeft().getX() + gridColIndex;

            String xLabel = String.valueOf(originalX);

            // Basic centering: Add padding before the label within the space for one room column
            int totalPaddingNeeded = ROOM_WIDTH - xLabel.length();
            int leftPadding = totalPaddingNeeded / 2;
            int rightPadding = totalPaddingNeeded - leftPadding; // Accounts for odd/even total padding

            header.append(" ".repeat(leftPadding));
            header.append(xLabel);
            header.append(" ".repeat(rightPadding));
        }

        // Add the header row at the beginning
        // Use add(0, ...) for ArrayList or addFirst() if it were a LinkedList
         if (!printLines.isEmpty()) { // Avoid adding header if there are no rows
            printLines.addFirst(header.toString());
         } else {
             // Handle case of empty grid (maybe print only header?)
             System.out.println(header.toString()); // Print just the header if no rooms
    }
}

    // --- Inner Record for Layout Info ---

    /**
     * Holds calculated information about the dungeon's layout in the grid.
     *
     * @param topLeft     The minimum (top-leftmost) coordinate point containing a room.
     * @param bottomRight The maximum (bottom-rightmost) coordinate point containing a room.
     * @param gridWidth   The width of the grid needed to contain all rooms (in room units).
     * @param gridHeight  The height of the grid needed to contain all rooms (in room units).
     */
    private record DungeonLayoutInfo(
            Point topLeft,
            Point bottomRight,
            int gridWidth,
            int gridHeight
    ) {}

}