package com.bmisiek.printer.console.printers;

import com.bmisiek.structures.IteratorExtension;
import com.bmisiek.structures.Point;
import com.bmisiek.game.dungeon.Dungeon;
import com.bmisiek.game.dungeon.DungeonManagerInterface;
import com.bmisiek.game.room.NullRoom;
import com.bmisiek.game.room.Room;
import com.bmisiek.printer.console.printers.room.Room3x3;
import com.bmisiek.printer.contract.PrinterInterface;
import com.bmisiek.structures.grid.Grid;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class DungeonPrinter implements PrinterInterface<DungeonManagerInterface> {

    private static final int ROOM_HEIGHT = 3; // Based on Room3x3
    private static final int ROOM_WIDTH = 3;  // Based on Room3x3
    private static final int COORDINATE_AXIS_PADDING = 5; // Space reserved for Y-axis labels
    private static final String LINE_SEPARATOR = System.lineSeparator(); // Use system-specific separator

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

        // Convert Room representations to their multi-line String representations
        Grid<String> printStringGrid = assemblePrintStringGrid(roomGrid);

        // Add coordinate labels BEFORE processing rows for printing
        Grid<String> labeledStringGrid = addCoordinateLabels(printStringGrid, layoutInfo);

        // --- Printing Logic ---
        // Get rows from the final grid (including labels)
        String[][] rows = IteratorExtension.toStream(labeledStringGrid.getRows())
                .map(row -> Arrays.stream(row)
                        .map(Optional::orElseThrow)
                        .toArray(String[]::new)
                )
                .toArray(String[][]::new);

        // Iterate through the rows of cells (each cell contains a potentially multi-line string)
        for (String[] rowOfCells : rows) {
            String[] cellStrings = Arrays.stream(rowOfCells).toArray(String[]::new);

            int maxLinesInRow = Arrays.stream(cellStrings)
                    .mapToInt(s -> s.lines().toArray().length) // Count actual lines
                    .max()
                    .orElse(1); // Default to 1 line if row is empty or all cells are empty strings

            List<List<String>> processedCells = Arrays.stream(cellStrings)
                    .map(cellStr -> centerVertically(cellStr, maxLinesInRow, guessCellWidth(cellStr))) // Pass cell width
                    .toList();

            // Print the row line by line
            for (int lineIndex = 0; lineIndex < maxLinesInRow; lineIndex++) {
                for (List<String> cellLines : processedCells) {
                    // Print the specific line for this cell
                    // If a cell somehow ended up with fewer lines than maxLinesInRow after centering
                    // (shouldn't happen with correct centerVertically), print empty string.
                    System.out.print(lineIndex < cellLines.size() ? cellLines.get(lineIndex) : "");
                }
                System.out.println(); // Move to the next line of output
            }
        }
    }

    /**
     * Guesses the intended width for a cell based on its content.
     * Assumes fixed width (ROOM_WIDTH) for room cells and COORDINATE_AXIS_PADDING for the first column.
     * This is a heuristic and might need refinement based on actual grid structure.
     * NOTE: This approach is brittle. A better way would be to store width information alongside the string in the grid.
     */
     private int guessCellWidth(String cellContent) {
        // Basic check: if content looks like a coordinate label (mostly spaces, maybe a number)
        // use COORDINATE_AXIS_PADDING. Otherwise, assume ROOM_WIDTH.
        // This might fail if room content is just spaces or numbers.
         String trimmed = cellContent.trim();
         boolean possiblyCoordinate = trimmed.matches("-?\\d*") || trimmed.isEmpty();

         // A more robust check might involve knowing the column index, but printRow doesn't have it.
         // Let's assume for now: If it has multiple lines or typical room characters, it's a room.
         if (cellContent.lines().count() > 1 || cellContent.contains("#") || cellContent.contains(".") /* add other room chars */ ) {
             return ROOM_WIDTH;
         } else if (possiblyCoordinate && cellContent.length() <= COORDINATE_AXIS_PADDING ) {
             // If it might be a coordinate and fits the padding width, assume it is.
             return COORDINATE_AXIS_PADDING;
         }
         // Default assumption if unsure
         return ROOM_WIDTH;
     }


    /**
     * Centers a multi-line string vertically within a total number of lines (maxLines).
     * Also ensures each line is horizontally centered within the specified cellWidth.
     * Returns the result as a List of strings (each element is one line).
     */
    private static List<String> centerVertically(String content, int maxLines, int cellWidth) {
        List<String> originalLines = content.lines().toList();
        int numOriginalLines = originalLines.size();

        if (numOriginalLines >= maxLines) {
            // Already meets or exceeds max lines, just ensure horizontal centering
            return originalLines.stream()
                    .limit(maxLines) // Truncate if needed (though ideally shouldn't happen)
                    .map(line -> centerHorizontally(line, cellWidth))
                    .collect(Collectors.toList());
        }

        int paddingTop = (maxLines - numOriginalLines) / 2;
        int paddingBottom = maxLines - numOriginalLines - paddingTop;

        List<String> resultLines = new ArrayList<>(maxLines);
        String emptyPaddedLine = centerHorizontally("", cellWidth); // Pre-calculate empty centered line

        // Add top padding lines
        for (int i = 0; i < paddingTop; i++) {
            resultLines.add(emptyPaddedLine);
        }

        // Add original content lines (horizontally centered)
        for (String line : originalLines) {
            resultLines.add(centerHorizontally(line, cellWidth));
        }

        // Add bottom padding lines
        for (int i = 0; i < paddingBottom; i++) {
            resultLines.add(emptyPaddedLine);
            }

        return resultLines;
        }

    /**
     * Centers a single string horizontally within a given maximum width.
     */
    private static @NotNull String centerHorizontally(String label, int maxWidth) {
        if (label == null) label = "";
        int labelLength = label.length();
        if (labelLength >= maxWidth) {
            return label.substring(0, maxWidth); // Truncate if too long
        }

        int paddingTotal = maxWidth - labelLength;
        int paddingLeft = paddingTotal / 2;
        int paddingRight = paddingTotal - paddingLeft; // Ensures total width is maxWidth

        // Consider using String.format for potentially cleaner padding
        return " ".repeat(paddingLeft) + label + " ".repeat(paddingRight);
    }

    private Grid<String> addCoordinateLabels(Grid<String> printStringGrid, DungeonLayoutInfo layoutInfo) {
        // Create a new grid large enough for labels + original content
        int maxLinesAcrossAllCells = 0;
        for (int y = 0; y < printStringGrid.getYSize(); y++) {
             for (int x = 0; x < printStringGrid.getXSize(); x++) {
                 Optional<String> cellOpt = printStringGrid.getAt(x, y);
                 if (cellOpt.isPresent()) {
                     maxLinesAcrossAllCells = Math.max(maxLinesAcrossAllCells, (int) cellOpt.get().lines().count());
                 }
             }
        }
        int effectiveRoomHeight = (maxLinesAcrossAllCells == 0) ? ROOM_HEIGHT : maxLinesAcrossAllCells;

        // --- Add Y-Axis Labels (inserting a column) ---
        Grid<String> gridWithY = new Grid<>(String.class, printStringGrid.getXSize() + 1, printStringGrid.getYSize());
        gridWithY.insertSubgrid(1, 0, printStringGrid); // Shift original content right

        // Add Y labels centered vertically within each room's assumed height
        int roomCenterLineOffset = effectiveRoomHeight / 2; // Line index within a room block for the label

        for (int roomY = 0; roomY < gridWithY.getYSize(); roomY++) {
            // Calculate the actual Y coordinate in the dungeon space
            int dungeonY = layoutInfo.topLeft().getY() + roomY;
            String yLabel = String.valueOf(dungeonY);
            String centeredYLabel = centerHorizontally(yLabel, COORDINATE_AXIS_PADDING);

            // We need to pad the label string vertically to match the room height
            // Pad the single centered label line to match 'effectiveRoomHeight'
            List<String> paddedLabelLines = new ArrayList<>();
            int labelTopPad = roomCenterLineOffset;
            int labelBottomPad = effectiveRoomHeight - 1 - labelTopPad;
            String emptyPaddedLine = centerHorizontally("", COORDINATE_AXIS_PADDING);

            for(int i=0; i<labelTopPad; i++) paddedLabelLines.add(emptyPaddedLine);
            paddedLabelLines.add(centeredYLabel);
            for(int i=0; i<labelBottomPad; i++) paddedLabelLines.add(emptyPaddedLine);

            // Combine into a single multi-line string for the grid cell
            String finalLabelCell = String.join(LINE_SEPARATOR, paddedLabelLines);
            gridWithY.setAt(0, roomY, finalLabelCell);
        }

        // --- Add X-Axis Labels (inserting a row) ---
        Grid<String> finalGrid = new Grid<>(String.class, gridWithY.getXSize(), gridWithY.getYSize() + 1);
        finalGrid.insertSubgrid(0, 1, gridWithY); // Shift content down

        // Add empty top-left corner (padded to Y-axis width)
        // This also needs vertical padding to match the X-axis label row height (assume 1 line)
        finalGrid.setAt(0, 0, centerHorizontally("", COORDINATE_AXIS_PADDING));

        // Add X labels centered horizontally within each room's width
        for (int roomX = 0; roomX < printStringGrid.getXSize(); roomX++) { // Iterate original width
            int dungeonX = layoutInfo.topLeft().getX() + roomX;
            String xLabel = String.valueOf(dungeonX);
            // The cell index in finalGrid is roomX + 1 (due to Y-axis column)
            // Labels go in row 0.
            finalGrid.setAt(roomX + 1, 0, centerHorizontally(xLabel, ROOM_WIDTH));
    }

        return finalGrid;
    }

    private DungeonLayoutInfo calculateLayoutInfo(DungeonManagerInterface dungeon) {
        Map<Point, Room> rooms = dungeon.getRooms();

        // Use Optional for safety in case stream is empty (though checked above)
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
        // Avoid creating representation repeatedly if it's always the same
        Room3x3 nullRoomRepresentation = null; // Lazy initialize

        for (int y = 0; y < layoutInfo.gridHeight(); y++) {
            for (int x = 0; x < layoutInfo.gridWidth(); x++) {
                if (!grid.has(x, y)) {
                    if (nullRoomRepresentation == null) {
                        nullRoomRepresentation = roomPrinter.Print(new NullRoom());
                    }
                    grid.setAt(x, y, nullRoomRepresentation);
                }
            }
        }
    }

    /**
    * Converts the grid of Room representations (Room3x3) into a grid of their
    * corresponding multi-line String representations.
     */
    private Grid<String> assemblePrintStringGrid(Grid<Room3x3> roomGrid) {
        Grid<String> result = new Grid<>(String.class, roomGrid.getXSize(), roomGrid.getYSize());
         for (int y = 0; y < roomGrid.getYSize(); y++) {
             for (int x = 0; x < roomGrid.getXSize(); x++) {
                 result.setAt(x, y, roomGrid.getAt(x, y).orElseThrow().toString()); // Handle missing/null case
            }
        }

        return result;
    }

    private static @NotNull String CenterHorizontally(String label, int maxWidth) {
        int paddingLeft = (maxWidth - label.length()) / 2;
        int paddingRight = maxWidth - label.length() - paddingLeft;
        return " ".repeat(paddingLeft) + label + " ".repeat(paddingRight);
    }
}