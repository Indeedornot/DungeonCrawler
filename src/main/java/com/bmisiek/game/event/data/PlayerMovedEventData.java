package com.bmisiek.game.event.data;

import com.bmisiek.game.basic.Point;
import com.bmisiek.game.player.Player;
import com.bmisiek.game.room.Room;
import lombok.Getter;

@Getter
public class PlayerMovedEventData extends PlayerEventData {
    private final Point from;
    private final Point to;
    private final Room room;

    public PlayerMovedEventData(Player player, Point from, Point to, Room room) {
        super(player);

        this.from = from;
        this.to = to;
        this.room = room;
    }
}