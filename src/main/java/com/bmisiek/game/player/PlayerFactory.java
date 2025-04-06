package com.bmisiek.game.player;

import com.bmisiek.game.config.GameConfigManager;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class PlayerFactory {
    private final GameConfigManager configurationManager;

    public PlayerFactory(GameConfigManager configurationManager) {
        this.configurationManager = configurationManager;
    }

    public Player createPlayer() {
        return new GamePlayer(
                UUID.randomUUID().hashCode(),
                configurationManager.getConfig().playerHealth
        );
    }
}
