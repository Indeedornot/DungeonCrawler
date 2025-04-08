package com.bmisiek.game.event.data;

import com.bmisiek.game.player.Player;
import lombok.Getter;

@Getter
public class DamageTakenEventData extends PlayerEventData {
    private final int damage;

    public DamageTakenEventData(Player player, int damage) {
        super(player);
        this.damage = damage;
    }
}
