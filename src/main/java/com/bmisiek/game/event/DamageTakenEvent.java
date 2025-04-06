package com.bmisiek.game.event;

import com.bmisiek.game.player.Player;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class DamageTakenEvent extends ApplicationEvent {
    private final Player player;

    public DamageTakenEvent(Object source, Player player) {
        super(source);
        this.player = player;
    }
}
