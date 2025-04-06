package com.bmisiek.game.event;

import org.springframework.context.ApplicationEvent;

public class DungeonEmptyEvent extends ApplicationEvent {
    public DungeonEmptyEvent(Object source) {
        super(source);
    }
}
