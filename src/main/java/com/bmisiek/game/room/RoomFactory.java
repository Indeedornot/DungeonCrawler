package com.bmisiek.game.room;

import com.bmisiek.game.room.factory.RoomFactoryInterface;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

@Service
public class RoomFactory {
    private final List<RoomFactoryInterface<?>> factories;

    public RoomFactory(List<RoomFactoryInterface<?>> factories) {
        this.factories = factories;
    }

    public Room createRoom(Class<? extends Room> roomClass) throws InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        var factory = factories.stream()
                .filter(f -> f.Supports(roomClass))
                .findFirst().orElse(null);
        if (factory != null) {
            return factory.createRoom(roomClass);
        }

        return roomClass.getConstructor().newInstance();
    }
}
