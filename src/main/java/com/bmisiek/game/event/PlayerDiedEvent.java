package com.bmisiek.game.event;

import com.bmisiek.game.player.Player;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

public class PlayerDiedEvent extends ApplicationEvent {
    @Getter
    private final Player player;

    public PlayerDiedEvent(Object source, Player player) {
        super(source);
        this.player = player;
    }
}
