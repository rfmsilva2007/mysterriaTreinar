package me.rod.mysterriaTreinar.abilities.impl;

import me.rod.mysterriaTreinar.abilities.Ability;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * Immobilizes nearby living entities for a fixed duration.
 * Stateless between activations, so it needs no lifecycle cleanup at all.
 */
public class TimeStopAbility implements Ability {

    private static final int DURATION_TICKS = 20 * 30; // 30 seconds
    private static final long COOLDOWN_TICKS = 20 * 60; // 60 seconds - tune to taste

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
        for (Entity entity : player.getNearbyEntities(15, 15, 15)) {
            if (!(entity instanceof LivingEntity living) || living.equals(player)) continue;
            living.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, DURATION_TICKS, 255, false, false));
            living.addPotionEffect(new PotionEffect(PotionEffectType.MINING_FATIGUE, DURATION_TICKS, 255, false, false));
        }
        player.sendMessage("§bTime stops around you.");
    }
}
