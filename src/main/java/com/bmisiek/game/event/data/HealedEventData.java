package com.bmisiek.game.event.data;

import com.bmisiek.game.player.Player;
import lombok.Getter;

@Getter
public class HealedEventData extends PlayerEventData {
    private final int heal;

    public HealedEventData(Player player, int heal) {
        super(player);
        this.heal = heal;
    }
}
