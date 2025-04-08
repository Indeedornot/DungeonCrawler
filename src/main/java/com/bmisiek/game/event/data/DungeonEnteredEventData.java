package com.bmisiek.game.event.data;

import com.bmisiek.game.player.Player;
import lombok.Getter;

@Getter
public class DungeonEnteredEventData {
    private final Player player;

    public DungeonEnteredEventData(Player player) {
        this.player = player;
    }
}
