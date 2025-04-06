package com.bmisiek.game.player;

import com.bmisiek.game.event.DamageTakenEvent;
import com.bmisiek.game.event.PlayerDiedEvent;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
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
        ApplicationEvent event = isAlive(player)
                ? new DamageTakenEvent(this, player)
                : new PlayerDiedEvent(this, player);

        applicationEventPublisher.publishEvent(event);
    }

    public void heal(@NotNull Player player, int heal) {
        player.setHealth(player.getHealth() + heal);
    }

    protected boolean isAlive(@NotNull Player player) {
        return player.getHealth() > 0;
    }

    protected boolean isDead(@NotNull Player player) {
        return player.getHealth() <= 0;
    }
}
