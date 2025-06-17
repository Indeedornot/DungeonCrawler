package com.bmisiek.game.event;

import com.bmisiek.game.event.data.FloorCompletedEventData;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * Intermediate event used for communication with the game loop.
 * For communication with the interface use {@link DungeonCompletedEvent}
 */
@Getter
public class FloorCompletedEvent extends ApplicationEvent {
    private final FloorCompletedEventData eventData;

    public FloorCompletedEvent(Object source, FloorCompletedEventData eventData) {
        super(source);
        this.eventData = eventData;
    }
}
