package com.bmisiek.game.event.data;

import com.bmisiek.game.player.Player;
import lombok.Getter;

@Getter
public class PlayerEventData {
    private final Player player;

    public PlayerEventData(Player player) {
        this.player = player;
    }
}
