package com.bmisiek.printer.console.printers;

import com.bmisiek.structures.Point;

/**
* Holds calculated information about the dungeon's layout in the grid.
*
* @param topLeft     The minimum (top-leftmost) coordinate point containing a room.
* @param bottomRight The maximum (bottom-rightmost) coordinate point containing a room.
* @param gridWidth   The width of the grid needed to contain all rooms (in room units).
* @param gridHeight  The height of the grid needed to contain all rooms (in room units).
*/
public record DungeonLayoutInfo(
       Point topLeft,
       Point bottomRight,
       int gridWidth,
       int gridHeight
) {}
