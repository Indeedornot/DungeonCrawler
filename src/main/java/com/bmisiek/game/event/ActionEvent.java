package com.bmisiek.game.event;

import com.bmisiek.game.event.data.ActionEventData;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class ActionEvent extends ApplicationEvent {
    private final ActionEventData eventData;

    public ActionEvent(Object source, ActionEventData eventData) {
        super(source);
        this.eventData = eventData;
    }
}
