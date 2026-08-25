package me.rod.mysterriaTreinar.abilities.core;

import org.bukkit.event.entity.EntityDamageEvent;

/**
 * Optional interface for abilities that need to react to damage events
 * (e.g. Time Rewind cancelling lethal damage). Kept exactly as before -
 * this part of the original design was already a fine extension point,
 * it was just being used by one class that did too much else besides.
 */
public interface DamageReactive {
    void onPlayerDamage(EntityDamageEvent event);
}
