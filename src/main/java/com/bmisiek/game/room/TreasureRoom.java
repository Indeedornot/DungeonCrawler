package com.bmisiek.game.room;

import com.bmisiek.game.exception.InvalidActionException;
import com.bmisiek.game.item.Item;
import com.bmisiek.game.item.ItemFactory;
import com.bmisiek.game.player.Player;
import lombok.Getter;

/**
 * A room that contains treasure (items) that can be found
 */
public class TreasureRoom extends Room {
    @Getter
    private Item treasure;
    private boolean treasureFound = false;
    
    public TreasureRoom(ItemFactory itemFactory) {
        this.treasure = itemFactory.createRandomItem();
    }
    
    @Override
    public void Act(Player player) {
    }
    
    @Override
    public boolean HasAdditionalActions() {
        return !treasureFound;
    }
    
    @Override
    public String GetAdditionalActionDescription() {
        return "Search for treasure";
    }
    
    @Override
    public void PerformAdditionalAction(Player player) {
        if (!treasureFound) {
            player.addItem(treasure);
            treasureFound = true;
        } else {
            throw new InvalidActionException("There is no more treasure to find");
        }
    }
    
    @Override
    public boolean canExistNextTo(Class<? extends Room> roomClass) {
        return roomClass != TreasureRoom.class && super.canExistNextTo(roomClass);
    }
}
