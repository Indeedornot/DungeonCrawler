package com.bmisiek.game.config;

import com.bmisiek.game.room.Room;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Range;

import java.util.Map;

@org.springframework.context.annotation.Configuration
@Getter
@Setter(AccessLevel.PACKAGE)
public class GameConfig {
    /*
        Weighted list of possible rooms to generate
     */
    public Map<Class<? extends Room>, Double> roomWeights;

    public Range<Integer> roomCount;

    public int playerHealth;
}
