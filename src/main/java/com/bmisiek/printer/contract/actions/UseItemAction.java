package com.bmisiek.printer.contract.actions;

import com.bmisiek.printer.contract.GameAction;
import lombok.Getter;

public class UseItemAction extends GameAction {
    @Getter
    private final int itemIndex;
    
    public UseItemAction(int itemIndex) {
        this.itemIndex = itemIndex;
    }
    
    @Override
    public String getCommandName() {
        return "use";
    }
}
