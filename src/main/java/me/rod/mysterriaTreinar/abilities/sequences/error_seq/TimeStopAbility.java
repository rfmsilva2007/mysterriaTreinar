package me.rod.mysterriaTreinar.abilities.sequences.error_seq;

import me.rod.mysterriaTreinar.abilities.core.*;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

/**
 * Immobilizes nearby living entities for a fixed duration.
 * Stateless between activations, so it needs no lifecycle cleanup at all.
 * 
 * Part of Error Seq, Time Manipulation group.
 * Cycling ability - left-click cycles through time manipulation abilities.
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
    public ActivationType getActivationType() {
        return ActivationType.CYCLING;
    }

    @Override
    public String getCyclingGroup() {
        return "time_manipulation";
    }

    @Override
    public void activate(Player player) {
        applyEffectToNearbyEntities(player, RANGE, PotionEffectType.SLOWNESS, DURATION_TICKS, 255);
        applyEffectToNearbyEntities(player, RANGE, PotionEffectType.MINING_FATIGUE, DURATION_TICKS, 255);
        applyEffectToNearbyEntities(player, RANGE, PotionEffectType.JUMP_BOOST, DURATION_TICKS, 255);
        player.sendMessage("§bTime stops around you.");
    }
}
