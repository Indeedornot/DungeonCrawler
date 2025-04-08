package com.bmisiek.game.event;

import com.bmisiek.game.event.data.HealedEventData;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class HealedEvent extends ApplicationEvent {
    private final HealedEventData eventData;

    public HealedEvent(Object source, HealedEventData eventData) {
        super(source);
        this.eventData = eventData;
    }
}
