package com.bmisiek.game.event;

import com.bmisiek.game.event.data.ItemFoundEventData;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class ItemFoundEvent extends ApplicationEvent {
    private final ItemFoundEventData eventData;

    public ItemFoundEvent(Object source, ItemFoundEventData eventData) {
        super(source);
        this.eventData = eventData;
    }
}
