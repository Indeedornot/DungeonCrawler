package com.bmisiek.printer.contract.actions;

import com.bmisiek.printer.contract.GameAction;

public class InventoryAction extends GameAction {
    public static final InventoryAction INSTANCE = new InventoryAction();
    
    private InventoryAction() {}
    
    @Override
    public String getCommandName() {
        return "inventory";
    }
    
    @Override
    public boolean consumesTurn() {
        return false;
    }
}
