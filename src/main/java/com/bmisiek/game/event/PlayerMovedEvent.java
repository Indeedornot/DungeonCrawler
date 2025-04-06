package com.bmisiek.game.event;

import com.bmisiek.game.event.data.PlayerMovedEventData;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class PlayerMovedEvent extends ApplicationEvent {
    private final PlayerMovedEventData eventData;

    public PlayerMovedEvent(Object source, PlayerMovedEventData eventData) {
        super(source);
        this.eventData = eventData;
    }
}
