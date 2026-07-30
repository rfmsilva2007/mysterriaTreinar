package me.rod.mysterriaTreinar.abilities.impl;

import me.rod.mysterriaTreinar.abilities.Ability;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * Buffs the player and slows nearby entities for a fixed duration.
 * Stateless between activations, so it needs no lifecycle cleanup at all.
 */
public class TimeAccelerationAbility implements Ability {

    private static final int PLAYER_BUFF_TICKS = 20 * 300; // 5 minutes
    private static final int SLOW_NEARBY_TICKS = 20 * 30;  // 30 seconds
    private static final long COOLDOWN_TICKS = 20 * 120;   // 2 minutes - tune to taste

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
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, PLAYER_BUFF_TICKS, 3));
        player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, PLAYER_BUFF_TICKS, 1));
        player.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, PLAYER_BUFF_TICKS, 1));

        for (Entity entity : player.getNearbyEntities(10, 10, 10)) {
            if (!(entity instanceof LivingEntity living) || living.equals(player)) continue;
            living.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, SLOW_NEARBY_TICKS, 2));
        }
        player.sendMessage("§bYou feel time speeding up around you.");
    }
}
