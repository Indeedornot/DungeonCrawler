package com.bmisiek.game.item;

import com.bmisiek.game.player.Player;

public class HealingAmplifier extends Item {
    
    public HealingAmplifier() {
        super("Healing Amplifier", "Doubles all healing received");
    }
    
    @Override
    public boolean use(Player player) {
        player.setHealingAmplifier(2.0); // Double healing
        return false; // Not consumed on use
    }
    
    @Override
    public boolean isConsumable() {
        return false; // Can be used indefinitely
    }
}
