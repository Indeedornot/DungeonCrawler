package com.bmisiek.game.room.factory;

import com.bmisiek.game.item.ItemFactory;
import com.bmisiek.game.room.KeyRoom;
import com.bmisiek.game.room.Room;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
public class KeyRoomFactory implements RoomFactoryInterface<KeyRoom> {
    private final ItemFactory itemFactory;
    private final ApplicationEventPublisher eventPublisher;

    public KeyRoomFactory(ItemFactory itemFactory, ApplicationEventPublisher eventPublisher) {
        this.itemFactory = itemFactory;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public boolean Supports(Class<? extends Room> roomClass) {
        return KeyRoom.class.isAssignableFrom(roomClass);
    }

    @Override
    public Room createRoom(Class<? extends Room> roomClass) {
        return new KeyRoom(itemFactory, eventPublisher);
    }
}
