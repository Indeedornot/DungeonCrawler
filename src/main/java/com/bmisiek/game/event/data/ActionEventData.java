package com.bmisiek.game.event.data;

import com.bmisiek.game.player.Player;
import com.bmisiek.printer.contract.GameAction;
import lombok.Getter;

@Getter
public class ActionEventData extends PlayerEventData {
    private final GameAction action;
    private final String parameter;

    public ActionEventData(Player player, GameAction action, String parameter) {
        super(player);
        this.action = action;
        this.parameter = parameter;
    }
    
    public ActionEventData(Player player, GameAction action) {
        this(player, action, null);
    }
    
    public boolean hasParameter() {
        return parameter != null && !parameter.isEmpty();
    }
}
