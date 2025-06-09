package com.bmisiek.printer.contract.actions;

import com.bmisiek.structures.Point;
import com.bmisiek.printer.contract.GameAction;

public class MoveAction extends GameAction {
    private final String commandName;
    private final Point direction;
    
    private MoveAction(String commandName, Point direction) {
        this.commandName = commandName;
        this.direction = direction;
    }
    
    @Override
    public String getCommandName() {
        return commandName;
    }

    public Point getMovement() {
        return direction;
    }
    
    // Static instances for common movements
    public static final MoveAction UP = new MoveAction("up", Point.Up);
    public static final MoveAction DOWN = new MoveAction("down", Point.Down);
    public static final MoveAction LEFT = new MoveAction("left", Point.Left);
    public static final MoveAction RIGHT = new MoveAction("right", Point.Right);
}
