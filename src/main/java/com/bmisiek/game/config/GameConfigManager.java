package com.bmisiek.game.config;

import com.bmisiek.game.room.*;
import lombok.Getter;
import org.springframework.data.domain.Range;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
@Getter
public class GameConfigManager {
    private final GameConfig config;

    public GameConfigManager() {
        var config = new GameConfig();
        config.setRoomWeights(new HashMap<>() {
            {
                put(SpikyRoom.class, 4.0);
                put(HospitalRoom.class, 1.0);
                put(CorridorRoom.class, 3.0);
                put(TreasureRoom.class, 0.5);
                put(KeyRoom.class, 0.0);
                put(ExitRoom.class, 0.0);
            }
        });
        config.setRoomCount(Range.closed(5, 10));
        config.setPlayerHealth(10);
        this.config = config;
    }
}
