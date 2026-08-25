package me.rod.mysterriaTreinar.abilities.core;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * Base class for abilities that apply potion effects to nearby entities.
 * Eliminates code duplication between abilities that use the same pattern.
 */
public abstract class AbstractAreaAbility implements Ability {

    /**
     * Applies a potion effect to all living entities near the player (excluding the player).
     * @param player The player activating the ability
     * @param range The radius in all directions (x, y, z)
     * @param effect The potion effect type to apply
     * @param duration Duration in ticks (20 ticks = 1 second)
     * @param amplifier Effect amplifier (0 = level 1, 1 = level 2, etc.)
     */
    protected void applyEffectToNearbyEntities(Player player, int range,
                                               PotionEffectType effect, int duration, int amplifier) {
        for (Entity entity : player.getNearbyEntities(range, range, range)) {
            if (!(entity instanceof LivingEntity living) || living.equals(player)) continue;
            living.addPotionEffect(new PotionEffect(effect, duration, amplifier, false, false));
        }
    }

    /**
     * Applies multiple potion effects to nearby entities in one call.
     * Useful for abilities that apply multiple effects.
     */
    protected void applyEffectsToNearbyEntities(Player player, int range, 
            PotionEffectType... effects) {
        for (Entity entity : player.getNearbyEntities(range, range, range)) {
            if (!(entity instanceof LivingEntity living) || living.equals(player)) continue;
            for (PotionEffectType effect : effects) {
                living.addPotionEffect(new PotionEffect(effect, 20 * 30, 0, false, false));
            }
        }
    }
}
