package com.bmisiek.game.item;

import com.bmisiek.game.player.PlayerManager;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

@Service
public class ItemFactory {
    private final PlayerManager playerManager;
    private final SecureRandom random = new SecureRandom();
    
    public ItemFactory(PlayerManager playerManager) {
        this.playerManager = playerManager;
    }
    
    public Item createRandomItem() {
        List<Item> possibleItems = new ArrayList<>();
        possibleItems.add(new HealingAmplifier());
        possibleItems.add(new HealthPotion(playerManager, 5));
        
        return possibleItems.get(random.nextInt(possibleItems.size()));
    }
    
    public Item createHealingAmplifier() {
        return new HealingAmplifier();
    }
    
    public Item createHealthPotion(int amount) {
        return new HealthPotion(playerManager, amount);
    }
    
    public Item createDungeonKey() {
        return new DungeonKey();
    }
}
