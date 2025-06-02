package com.bmisiek.game.event.data;

import com.bmisiek.game.item.Item;
import com.bmisiek.game.player.Player;
import lombok.Getter;

@Getter
public class ItemFoundEventData extends PlayerEventData {
    private final Item item;

    public ItemFoundEventData(Player player, Item item) {
        super(player);
        this.item = item;
    }
}
