package com.bmisiek.game.room;

import com.bmisiek.game.event.ItemFoundEvent;
import com.bmisiek.game.event.data.ItemFoundEventData;
import com.bmisiek.game.exception.InvalidActionException;
import com.bmisiek.game.item.Item;
import com.bmisiek.game.item.ItemFactory;
import com.bmisiek.game.player.Player;
import lombok.Getter;
import org.springframework.context.ApplicationEventPublisher;

/**
 * A special room that always contains a key to the exit
 */
public class KeyRoom extends Room {
    private final ApplicationEventPublisher eventPublisher;
    
    @Getter
    private final Item key;
    private boolean keyFound = false;
    
    public KeyRoom(ItemFactory itemFactory, ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
        this.key = itemFactory.createDungeonKey();
    }
    
    @Override
    public void Act(Player player) {
    }
    
    @Override
    public boolean HasAdditionalActions() {
        return !keyFound;
    }
    
    @Override
    public String GetAdditionalActionDescription() throws InvalidActionException {
        if (keyFound) {
            throw new InvalidActionException("There is nothing else to find here.");
        }
        return "Search for a key";
    }
    
    @Override
    public void PerformAdditionalAction(Player player) throws InvalidActionException {
        if (keyFound) {
            throw new InvalidActionException("You have already found the key in this room.");
        }
        
        player.addItem(key);
        keyFound = true;
        
        eventPublisher.publishEvent(new ItemFoundEvent(this, new ItemFoundEventData(player, key)));
    }
    
    @Override
    public boolean canExistNextTo(Class<? extends Room> roomClass) {
        return !ExitRoom.class.isAssignableFrom(roomClass) &&
               !SpawnRoom.class.isAssignableFrom(roomClass);
    }
}
