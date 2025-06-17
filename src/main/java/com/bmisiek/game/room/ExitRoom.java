package com.bmisiek.game.room;

import com.bmisiek.game.event.FloorCompletedEvent;
import com.bmisiek.game.event.data.FloorCompletedEventData;
import com.bmisiek.game.exception.InvalidActionException;
import com.bmisiek.game.item.DungeonKey;
import com.bmisiek.game.player.Player;
import org.springframework.context.ApplicationEventPublisher;

public class ExitRoom extends Room {
    private final ApplicationEventPublisher eventPublisher;
    
    public ExitRoom(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @Override
    public void Act(Player player) {
    }
    
    @Override
    public boolean HasAdditionalActions() {
        return true;
    }
    
    @Override
    public String GetAdditionalActionDescription() {
        return "Use key to proceed to next floor";
    }
    
    @Override
    public void PerformAdditionalAction(Player player) {
        if (!hasKey(player)) {
            throw new InvalidActionException("You need a key to proceed to the next floor!");
        }

        removeKey(player);
        eventPublisher.publishEvent(new FloorCompletedEvent(this, new FloorCompletedEventData(player)));
    }

    private static boolean hasKey(Player player) {
        return player.getInventory().stream()
            .anyMatch(item -> item instanceof DungeonKey);
    }

    private static void removeKey(Player player) {
        player.getInventory().stream()
            .filter(item -> item instanceof DungeonKey)
            .findFirst()
            .ifPresent(key -> {
                int keyIndex = player.getInventory().indexOf(key);
                if (keyIndex < 0) {
                    throw new IllegalStateException("Tried to remove a key that is not in the inventory");
                }

                player.getInventory().remove(keyIndex);
            });
    }

    @Override
    public boolean canExistNextTo(Class<? extends Room> roomClass) {
        return CorridorRoom.class.isAssignableFrom(roomClass);
    }
}
