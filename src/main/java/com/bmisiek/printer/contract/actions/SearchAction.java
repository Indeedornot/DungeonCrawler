package com.bmisiek.printer.contract.actions;

import com.bmisiek.printer.contract.GameAction;

public class SearchAction extends GameAction {
    public static final SearchAction INSTANCE = new SearchAction();
    
    private SearchAction() {}
    
    @Override
    public String getCommandName() {
        return "search";
    }
}
