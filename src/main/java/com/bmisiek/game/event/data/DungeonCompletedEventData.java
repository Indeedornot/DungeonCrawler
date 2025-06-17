package com.bmisiek.game.event.data;

import com.bmisiek.game.player.Player;
import lombok.Getter;

@Getter
public class DungeonCompletedEventData extends PlayerEventData {
    private final String previousFloor;
    private final String newFloor;

    public DungeonCompletedEventData(Player player, String previousFloor, String newFloor) {
        super(player);
        this.previousFloor = previousFloor;
        this.newFloor = newFloor;
    }
}
