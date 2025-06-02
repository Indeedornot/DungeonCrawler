package com.bmisiek.game.room.factory;

import com.bmisiek.game.item.ItemFactory;
import com.bmisiek.game.room.Room;
import com.bmisiek.game.room.TreasureRoom;
import org.springframework.stereotype.Service;

@Service
public class TreasureRoomFactory implements RoomFactoryInterface<TreasureRoom> {
    private final ItemFactory itemFactory;
    
    public TreasureRoomFactory(ItemFactory itemFactory) {
        this.itemFactory = itemFactory;
    }
    
    @Override
    public boolean Supports(Class<? extends Room> roomClass) {
        return roomClass == TreasureRoom.class;
    }
    
    @Override
    public Room createRoom(Class<? extends Room> roomClass) {
        return new TreasureRoom(itemFactory);
    }
}
