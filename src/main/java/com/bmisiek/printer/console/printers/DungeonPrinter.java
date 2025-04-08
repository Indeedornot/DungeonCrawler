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
import org.jetbrains.annotations.NotNull;
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

    public DungeonPrinter(RoomPrinter roomPrinter) {
        this.roomPrinter = roomPrinter;
    }

    @Override
    public boolean Supports(Class<?> className) {
        return className == Dungeon.class;
    }

    @Override
    public void Print(DungeonManagerInterface dungeon) {
        DungeonLayoutInfo layoutInfo = calculateLayoutInfo(dungeon);

        Grid<Room3x3> roomGrid = createRoomGrid(dungeon, layoutInfo);
        List<String> printLines = assemblePrintLines(roomGrid);

        AddCoordinateLabels(printLines, layoutInfo);

        printLines.forEach(System.out::println);
    }

    private void AddCoordinateLabels(List<String> printLines, DungeonLayoutInfo layoutInfo) {
        addYAxisLabels(printLines, layoutInfo);
        AddXAxisLabels(printLines, layoutInfo);
    }

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
     * Creates a single line of text by concatenating the corresponding subline
     * from each room in the row.
     * Example: Get line 1 from room1, line 1 from room2, line 1 from room3 and join them.
     */
    private String formatSingleLineAcrossRooms(Room3x3[] roomRow, int subRowIndex) {
        return Arrays.stream(roomRow)
                .map(room -> String.join("", room.GetRow(subRowIndex)))
                .collect(Collectors.joining());
    }

    /**
     * Adds Y-axis coordinate labels to the left of the print lines.
     * Labels are aligned with the vertical center of each room row.
     */
    private void addYAxisLabels(List<String> printLines, DungeonLayoutInfo layoutInfo) {

        for (int gridRowIndex = 0; gridRowIndex < printLines.size(); gridRowIndex++) {
            AddYAxisLabel(printLines, layoutInfo, gridRowIndex);
        }
    }

    private static void AddYAxisLabel(List<String> printLines, DungeonLayoutInfo layoutInfo, int gridRowIndex) {
        int middleLineIndexInRoom = ROOM_HEIGHT / 2;
        String verticalPadding = " ".repeat(COORDINATE_AXIS_PADDING);

        //Retrieve original Y index by reversing adjustment
        int originalY = layoutInfo.topLeft().getY() + gridRowIndex;

        String yLabel = String.valueOf(originalY);
        String labelLinePrefix = CenterLine(yLabel, COORDINATE_AXIS_PADDING);

        // Split the multi-line room row string
        String[] subLines = printLines.get(gridRowIndex).split(LINE_SEPARATOR, -1);

        // Apply padding/label
        subLines = Arrays.stream(subLines).map(line -> verticalPadding + line).toArray(String[]::new);
        subLines[middleLineIndexInRoom] = subLines[middleLineIndexInRoom].replace(verticalPadding, labelLinePrefix);

        // Join back and update the list
        printLines.set(gridRowIndex, String.join(LINE_SEPARATOR, subLines));
    }

    private static @NotNull String CenterLine(String yLabel, int maxWidth) {
        int paddingLeft = (maxWidth - yLabel.length()) / 2;
        int paddingRight = maxWidth - yLabel.length() - paddingLeft;
        return " ".repeat(paddingLeft) + yLabel + " ".repeat(paddingRight);
    }

    /**
     * Adds an X-axis coordinate label row at the top of the print lines.
     * Labels are centered above each room column.
     */
     private void AddXAxisLabels(List<String> printLines, DungeonLayoutInfo layoutInfo) {
        StringBuilder header = new StringBuilder();
        header.append(" ".repeat(COORDINATE_AXIS_PADDING)); // Initial padding to align with Y labels

        for (int gridColIndex = 0; gridColIndex < layoutInfo.gridWidth(); gridColIndex++) {
            // Calculate original X coordinate by reversing mapping
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

        printLines.addFirst(header.toString());
    }
}