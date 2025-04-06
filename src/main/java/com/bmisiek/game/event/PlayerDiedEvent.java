package com.bmisiek.game.event;

import com.bmisiek.game.event.data.PlayerEventData;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class PlayerDiedEvent extends ApplicationEvent {
    private final PlayerEventData eventData;

    public PlayerDiedEvent(Object source, PlayerEventData eventData) {
        super(source);
        this.eventData = eventData;
    }
}
