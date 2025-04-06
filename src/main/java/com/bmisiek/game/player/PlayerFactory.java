package com.bmisiek.game.player;

import org.springframework.stereotype.Service;

@Service
public class PlayerFactory {
    public Player createPlayer() {
        return new GamePlayer(1, 10);
    }
}
