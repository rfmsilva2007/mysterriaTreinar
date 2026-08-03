package me.rod.mysterriaTreinar.abilities.impl;

import me.rod.mysterriaTreinar.abilities.AbstractAreaAbility;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

/**
 * Immobilizes nearby living entities for a fixed duration.
 * Stateless between activations, so it needs no lifecycle cleanup at all.
 */
public class TimeStopAbility extends AbstractAreaAbility {

    private static final int DURATION_TICKS = 20 * 30; // 30 seconds
    private static final long COOLDOWN_TICKS = 20 * 60; // 60 seconds
    private static final int RANGE = 15;

    @Override
    public String getId() {
        return "time_stop";
    }

    @Override
    public long getCooldownTicks() {
        return COOLDOWN_TICKS;
    }

    @Override
    public void activate(Player player) {
        applyEffectToNearbyEntities(player, RANGE, PotionEffectType.SLOWNESS, DURATION_TICKS, 255);
        applyEffectToNearbyEntities(player, RANGE, PotionEffectType.MINING_FATIGUE, DURATION_TICKS, 255);
        player.sendMessage("§bTime stops around you.");
    }
}
