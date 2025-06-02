package com.bmisiek.game.item;

import com.bmisiek.game.player.Player;
import com.bmisiek.game.player.PlayerManager;

public class HealthPotion extends Item {
    private final PlayerManager playerManager;
    private final int healAmount;
    
    public HealthPotion(PlayerManager playerManager, int healAmount) {
        super("Health Potion", "Restores " + healAmount + " health points");
        this.playerManager = playerManager;
        this.healAmount = healAmount;
    }
    
    @Override
    public boolean use(Player player) {
        playerManager.heal(player, healAmount, false); // Direct healing, not affected by amplifiers
        return true; // Consumed on use
    }
    
    @Override
    public boolean isConsumable() {
        return true;
    }
}
