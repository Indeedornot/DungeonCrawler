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
import java.util.stream.IntStream;

@Service
public class DungeonPrinter implements PrinterInterface<DungeonManagerInterface> {

    private static final int ROOM_HEIGHT = 3;
    private static final int ROOM_WIDTH = 3;
    private static final int COORDINATE_AXIS_PADDING = 5; // Space reserved for Y-axis labels
    private static final String LINE_SEPARATOR = System.lineSeparator();

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
        Grid<String> printStringGrid = assembleAndPadRoomStrings(roomGrid);
        Grid<String> labeledStringGrid = addPaddedCoordinateLabels(printStringGrid, layoutInfo);

        String[][] rows = initializePrintRows(labeledStringGrid);
        processAndPrintRows(rows);
    }

    private static void processAndPrintRows(String[][] rows) {
        for (String[] rowOfCells : rows) {
            int maxLinesInRow = Arrays.stream(rowOfCells)
                    .mapToInt(s -> (int) s.lines().count())
                    .max()
                    .orElse(1);

            List<List<String>> processedCells = Arrays.stream(rowOfCells)
                    .map(cellStr -> centerVertically(cellStr, maxLinesInRow))
                    .toList();

            for (int lineIndex = 0; lineIndex < maxLinesInRow; lineIndex++) {
                for (List<String> cellLines : processedCells) {
                    System.out.print(lineIndex < cellLines.size() ? cellLines.get(lineIndex) : "");
                }
                System.out.println();
            }
        }
    }

    private static String[] @NotNull [] initializePrintRows(Grid<String> labeledStringGrid) {
        return IteratorExtension.toStream(labeledStringGrid.getRows())
                .map(row -> Arrays.stream(row)
                        .map(Optional::orElseThrow)
                        .toArray(String[]::new)
                )
                .toArray(String[][]::new);
    }

    /**
     * Centers a multi-line string vertically within a total number of lines (maxLines).
     * Assumes that each individual line within the `content` string
     * is *already* horizontally padded to its intended display width.
     * Returns the result as a List of strings (each element is one line, correctly padded).
     *
     * @param content The multi-line, horizontally-padded string to be vertically centered.
     * @param maxLines The total number of lines the content should occupy.
     * @return A List of strings, each string represents a line, with
     * empty padding lines above and below the original content to achieve vertical centering.
     */
    private static List<String> centerVertically(String content, int maxLines) {
        List<String> originalLines = content.lines().toList();
        int numOriginalLines = originalLines.size();

        if (numOriginalLines >= maxLines) {
            return originalLines.stream().limit(maxLines).collect(Collectors.toList());
        }

        int paddingTop = (maxLines - numOriginalLines) / 2;
        int paddingBottom = maxLines - numOriginalLines - paddingTop;

        List<String> resultLines = new ArrayList<>(maxLines);

        int assumedCellWidth = originalLines.isEmpty() ? ROOM_WIDTH : originalLines.getFirst().length();
        String emptyPaddedLine = " ".repeat(assumedCellWidth); // Create a horizontally padded empty line

        IntStream.range(0, paddingTop).mapToObj(i -> emptyPaddedLine).forEachOrdered(resultLines::add);
        resultLines.addAll(originalLines);
        IntStream.range(0, paddingBottom).mapToObj(i -> emptyPaddedLine).forEachOrdered(resultLines::add);

        return resultLines;
    }

    /**
     * Centers a single string horizontally within a given maximum width.
     * @return The horizontally centered and padded string.
     */
    private static @NotNull String centerHorizontally(String label, int maxWidth) {
        if (label == null) {
            label = "";
        }
        int labelLength = label.length();
        if (labelLength >= maxWidth) {
            return label.substring(0, maxWidth); // Truncate if too long
        }

        int paddingTotal = maxWidth - labelLength;
        int paddingLeft = paddingTotal / 2;
        int paddingRight = paddingTotal - paddingLeft;

        return " ".repeat(paddingLeft) + label + " ".repeat(paddingRight);
    }

    /**
     * Adds X and Y coordinate labels to the grid of room strings.
     * The labels are also correctly padded to ensure alignment with room cells.
     *
     * @param printStringGrid The grid containing multi-line string representations of rooms.
     * @return New Grid<String> including the coordinate labels.
     */
    private Grid<String> addPaddedCoordinateLabels(Grid<String> printStringGrid, DungeonLayoutInfo layoutInfo) {
        Grid<String> gridWithY = addYCoordinates(printStringGrid, layoutInfo);
        Grid<String> gridWithX = addXCoordinates(printStringGrid, layoutInfo, gridWithY);

        return gridWithX;
    }

    private static @NotNull Grid<String> addXCoordinates(Grid<String> printStringGrid, DungeonLayoutInfo layoutInfo, Grid<String> gridWithY) {
        Grid<String> finalGrid = new Grid<>(String.class, gridWithY.getXSize(), gridWithY.getYSize() + 1);
        finalGrid.insertSubgrid(0, 1, gridWithY);
        finalGrid.setAt(0, 0, centerHorizontally("", COORDINATE_AXIS_PADDING));

        for (int roomX = 0; roomX < printStringGrid.getXSize(); roomX++) {
            int dungeonX = layoutInfo.topLeft().getX() + roomX;
            String xLabel = String.valueOf(dungeonX);
            finalGrid.setAt(roomX + 1, 0, centerHorizontally(xLabel, ROOM_WIDTH));
        }
        return finalGrid;
    }

    private static @NotNull Grid<String> addYCoordinates(Grid<String> printStringGrid, DungeonLayoutInfo layoutInfo) {
        int maxLinesAcrossAllRoomCells = 0;
        for (int y = 0; y < printStringGrid.getYSize(); y++) {
            for (int x = 0; x < printStringGrid.getXSize(); x++) {
                Optional<String> cellOpt = printStringGrid.getAt(x, y);
                if (cellOpt.isPresent()) {
                    maxLinesAcrossAllRoomCells = Math.max(maxLinesAcrossAllRoomCells, (int) cellOpt.get().lines().count());
                }
            }
        }

        int effectiveCellHeight = (maxLinesAcrossAllRoomCells == 0) ? ROOM_HEIGHT : maxLinesAcrossAllRoomCells;
        Grid<String> gridWithY = new Grid<>(String.class, printStringGrid.getXSize() + 1, printStringGrid.getYSize());
        gridWithY.insertSubgrid(1, 0, printStringGrid);

        int labelCenterLineOffset = effectiveCellHeight / 2;
        for (int roomY = 0; roomY < gridWithY.getYSize(); roomY++) {
            int dungeonY = layoutInfo.topLeft().getY() + roomY;
            String yLabel = String.valueOf(dungeonY);
            String centeredYLabel = centerHorizontally(yLabel, COORDINATE_AXIS_PADDING);

            List<String> paddedLabelLines = new ArrayList<>(effectiveCellHeight);
            String emptyPaddedLine = centerHorizontally("", COORDINATE_AXIS_PADDING);

            IntStream.range(0, labelCenterLineOffset).mapToObj(i -> emptyPaddedLine).forEachOrdered(paddedLabelLines::add);
            paddedLabelLines.add(centeredYLabel);
            IntStream.range(paddedLabelLines.size(), effectiveCellHeight).mapToObj(i -> emptyPaddedLine).forEachOrdered(paddedLabelLines::add);

            String finalLabelCell = String.join(LINE_SEPARATOR, paddedLabelLines);
            gridWithY.setAt(0, roomY, finalLabelCell);
        }
        return gridWithY;
    }

    private DungeonLayoutInfo calculateLayoutInfo(DungeonManagerInterface dungeon) {
        Map<Point, Room> rooms = dungeon.getRooms();

        if (rooms.isEmpty()) {
            return new DungeonLayoutInfo(new Point(0, 0), new Point(0, 0), 1, 1);
        }

        var xs = rooms.keySet().stream().mapToInt(Point::getX).toArray();
        var ys = rooms.keySet().stream().mapToInt(Point::getY).toArray();

        int minX = Arrays.stream(xs).min().orElse(0);
        int maxX = Arrays.stream(xs).max().orElse(0);
        int minY = Arrays.stream(ys).min().orElse(0);
        int maxY = Arrays.stream(ys).max().orElse(0);

        Point topLeft = new Point(minX, minY);
        Point bottomRight = new Point(maxX, maxY);
        int gridWidth = maxX - minX + 1;
        int gridHeight = maxY - minY + 1;

        return new DungeonLayoutInfo(topLeft, bottomRight, gridWidth, gridHeight);
    }

    /**
     * Creates a 2D grid of `Room3x3` representations from the dungeon data.
     * Coordinates are adjusted so the top-left room in the dungeon is at grid position (0,0).
     * Empty spaces in the dungeon layout are filled with `NullRoom` representations.
     *
     * @return A `Grid<Room3x3>` representing the dungeon's rooms and empty spaces.
     */
    private Grid<Room3x3> createRoomGrid(DungeonManagerInterface dungeon, DungeonLayoutInfo layoutInfo) {
        Grid<Room3x3> grid = new Grid<>(Room3x3.class, layoutInfo.gridWidth(), layoutInfo.gridHeight());
        Point offsetTo0x0 = layoutInfo.topLeft();

        for (Map.Entry<Point, Room> entry : dungeon.getRooms().entrySet()) {
            Point originalPoint = entry.getKey();
            Room room = entry.getValue();

            Point gridPoint = originalPoint.subtract(offsetTo0x0);

            Room3x3 roomRepresentation = roomPrinter.Print(room);
            grid.setAt(gridPoint, roomRepresentation);
        }

        fillEmptyGridCells(grid, layoutInfo);
        return grid;
    }

    /**
     * Fills any unassigned cells in the `Room3x3` grid with the representation of a `NullRoom`.
     */
    private void fillEmptyGridCells(Grid<Room3x3> grid, DungeonLayoutInfo layoutInfo) {
        Room3x3 nullRoom = roomPrinter.Print(new NullRoom());

        for (int y = 0; y < layoutInfo.gridHeight(); y++) {
            for (int x = 0; x < layoutInfo.gridWidth(); x++) {
                if (!grid.has(x, y)) {
                    grid.setAt(x, y, nullRoom);
                }
            }
        }
    }

    /**
     * Converts the grid of `Room3x3` representations into a grid of their
     * corresponding multi-line String representations.
     *
     * @return A `Grid<String>` where each cell contains a multi-line string,
     * with each line horizontally padded to `ROOM_WIDTH`.
     */
    private Grid<String> assembleAndPadRoomStrings(Grid<Room3x3> roomGrid) {
        Grid<String> result = new Grid<>(String.class, roomGrid.getXSize(), roomGrid.getYSize());
        for (int y = 0; y < roomGrid.getYSize(); y++) {
            for (int x = 0; x < roomGrid.getXSize(); x++) {
                String roomContent = roomGrid.getAt(x, y).orElseThrow().toString();
                List<String> paddedLines = roomContent.lines()
                        .map(line -> centerHorizontally(line, ROOM_WIDTH))
                        .collect(Collectors.toList());

                result.setAt(x, y, String.join(LINE_SEPARATOR, paddedLines));
            }
        }
        return result;
    }

    private record DungeonLayoutInfo(Point topLeft, Point bottomRight, int gridWidth, int gridHeight) {}
}
