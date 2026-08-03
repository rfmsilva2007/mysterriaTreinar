package me.rod.mysterriaTreinar.abilities.impl;

import me.rod.mysterriaTreinar.abilities.AbstractAreaAbility;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

/**
 * Buffs the player and slows nearby entities for a fixed duration.
 * Stateless between activations, so it needs no lifecycle cleanup at all.
 */
public class TimeAccelerationAbility extends AbstractAreaAbility {

    private static final int PLAYER_BUFF_TICKS = 20 * 300; // 5 minutes
    private static final int SLOW_NEARBY_TICKS = 20 * 30;  // 30 seconds
    private static final long COOLDOWN_TICKS = 20 * 120;   // 2 minutes
    private static final int RANGE = 10;

    @Override
    public String getId() {
        return "time_acceleration";
    }

    @Override
    public long getCooldownTicks() {
        return COOLDOWN_TICKS;
    }

    @Override
    public void activate(Player player) {
        // Buff the player
        player.addPotionEffect(new org.bukkit.potion.PotionEffect(PotionEffectType.SPEED, PLAYER_BUFF_TICKS, 3));
        player.addPotionEffect(new org.bukkit.potion.PotionEffect(PotionEffectType.RESISTANCE, PLAYER_BUFF_TICKS, 1));
        player.addPotionEffect(new org.bukkit.potion.PotionEffect(PotionEffectType.STRENGTH, PLAYER_BUFF_TICKS, 1));

        // Slow nearby entities
        applyEffectToNearbyEntities(player, RANGE, PotionEffectType.SLOWNESS, SLOW_NEARBY_TICKS, 2);

        player.sendMessage("§bYou feel time speeding up around you.");
    }
}
