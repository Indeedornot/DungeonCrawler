package com.bmisiek.game.player;

import com.bmisiek.game.event.DamageTakenEvent;
import com.bmisiek.game.event.HealedEvent;
import com.bmisiek.game.event.PlayerDiedEvent;
import com.bmisiek.game.event.data.DamageTakenEventData;
import com.bmisiek.game.event.data.HealedEventData;
import com.bmisiek.game.event.data.PlayerEventData;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationEvent;
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
        applicationEventPublisher.publishEvent(new PlayerDiedEvent(this, new PlayerEventData(player)));
    }

    public void heal(@NotNull Player player, int heal) {
        player.setHealth(player.getHealth() + heal);
        applicationEventPublisher.publishEvent(new HealedEvent(this, new HealedEventData(player, heal)));
    }

    protected boolean isAlive(@NotNull Player player) {
        return player.getHealth() > 0;
    }

    protected boolean isDead(@NotNull Player player) {
        return player.getHealth() <= 0;
    }
}
