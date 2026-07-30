package me.rod.mysterriaTreinar.abilities;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

/**
 * Forwards damage events to every registered ability that implements
 * DamageReactive. This keeps damage-event wiring generic: an ability that
 * needs to react to damage just implements DamageReactive, and it starts
 * receiving events automatically - no changes needed here or in the plugin
 * main class.
 */
public class DamageDispatchListener implements Listener {

    private final AbilityRegistry registry;

    public DamageDispatchListener(AbilityRegistry registry) {
        this.registry = registry;
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        for (Ability ability : registry.all()) {
            if (ability instanceof DamageReactive reactive) {
                reactive.onPlayerDamage(event);
            }
        }
    }
}
