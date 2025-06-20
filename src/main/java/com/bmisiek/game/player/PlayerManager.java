package com.bmisiek.game.player;

import com.bmisiek.game.event.DamageTakenEvent;
import com.bmisiek.game.event.HealedEvent;
import com.bmisiek.game.event.PlayerDiedEvent;
import com.bmisiek.game.event.data.DamageTakenEventData;
import com.bmisiek.game.event.data.HealedEventData;
import com.bmisiek.game.event.data.PlayerEventData;
import com.bmisiek.game.exception.InvalidActionException;
import com.bmisiek.game.item.Item;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

/**
 * Main class responsible for holding and managing the player
 */
@Service
public class PlayerManager {
    public final ApplicationEventPublisher applicationEventPublisher;

    public PlayerManager(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    public void takeDamage(@NotNull Player player, int damage) {
        player.setHealth(player.getHealth() - damage);
        applicationEventPublisher.publishEvent(new DamageTakenEvent(this, new DamageTakenEventData(player, damage)));
        if(player.getHealth() <= 0) {
            applicationEventPublisher.publishEvent(new PlayerDiedEvent(this, new PlayerEventData(player)));
        }
    }

    public void healDirectly(@NotNull Player player, int heal) {
        heal(player, heal, false);
    }

    public void healWithAmplifier(@NotNull Player player, int heal) {
        heal(player, heal, true);
    }

    private void heal(@NotNull Player player, int heal, boolean applyAmplifier) {
        int actualHeal = (int) Math.round(heal * (applyAmplifier ? player.getHealingAmplifier() : 1));

        player.setHealth(player.getHealth() + actualHeal);
        applicationEventPublisher.publishEvent(new HealedEvent(this, new HealedEventData(player, actualHeal)));
    }

    /**
     * Use an item from the player's inventory
     * @param player The player using the item
     * @param itemIndex The index of the item in the inventory (0-based)
     * @return A message describing the result of the action
     * @throws InvalidActionException if the item index is invalid
     */
    public String useItem(@NotNull Player player, int itemIndex) throws InvalidActionException {
        var inventory = player.getInventory();
        
        if (itemIndex < 0 || itemIndex >= inventory.size()) {
            throw new InvalidActionException("Invalid item number");
        }
        
        Item item = inventory.get(itemIndex);
        boolean consumed = item.use(player);
        
        if (consumed) {
            player.removeItem(item);
        }
        
        return "Used " + item.getName();
    }
}
