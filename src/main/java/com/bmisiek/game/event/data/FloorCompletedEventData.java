package com.bmisiek.game.event.data;

import com.bmisiek.game.player.Player;
import lombok.Getter;

@Getter
public class FloorCompletedEventData extends PlayerEventData {
    public FloorCompletedEventData(Player player) {
        super(player);
    }
}
