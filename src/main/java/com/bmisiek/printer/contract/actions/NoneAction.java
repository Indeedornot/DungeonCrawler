package com.bmisiek.printer.contract.actions;

import com.bmisiek.printer.contract.GameAction;

public class NoneAction extends GameAction {
    public static final NoneAction INSTANCE = new NoneAction();
    
    private NoneAction() {}
    
    @Override
    public String getCommandName() {
        return "none";
    }
}
