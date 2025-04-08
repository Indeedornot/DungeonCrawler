package com.bmisiek.game.event;

import com.bmisiek.game.event.data.DamageTakenEventData;
import com.bmisiek.game.event.data.PlayerEventData;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class DamageTakenEvent extends ApplicationEvent {
    private final DamageTakenEventData eventData;

    public DamageTakenEvent(Object source, DamageTakenEventData eventData) {
        super(source);
        this.eventData = eventData;
    }
}
