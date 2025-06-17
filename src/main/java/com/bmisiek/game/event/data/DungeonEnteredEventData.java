package com.bmisiek.game.event.data;

import com.bmisiek.game.player.Player;
import lombok.Getter;

@Getter
public class DungeonEnteredEventData extends PlayerEventData{
    public DungeonEnteredEventData(Player player) {
        super(player);
    }
}
