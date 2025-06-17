package com.bmisiek.game.event;

import com.bmisiek.game.event.data.DungeonCompletedEventData;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class DungeonCompletedEvent extends ApplicationEvent {
    private final DungeonCompletedEventData eventData;

    public DungeonCompletedEvent(Object source, DungeonCompletedEventData eventData) {
        super(source);
        this.eventData = eventData;
    }
}
