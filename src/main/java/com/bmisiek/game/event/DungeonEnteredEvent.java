package com.bmisiek.game.event;

import com.bmisiek.game.dungeon.Dungeon;
import com.bmisiek.game.event.data.DungeonEnteredEventData;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class DungeonEnteredEvent extends ApplicationEvent {
    private final DungeonEnteredEventData eventData;

    public DungeonEnteredEvent(Object source, DungeonEnteredEventData eventData) {
        super(source);
        this.eventData = eventData;
    }
}
