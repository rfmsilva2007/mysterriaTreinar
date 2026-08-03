package me.rod.mysterriaTreinar.abilities;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Forwards damage events to every registered ability that implements
 * DamageReactive. This keeps damage-event wiring generic: an ability that
 * needs to react to damage just implements DamageReactive, and it starts
 * receiving events automatically - no changes needed here or in the plugin
 * main class.
 */
public class DamageDispatchListener implements Listener {

    private final AbilityRegistry registry;
    private final Logger logger;

    public DamageDispatchListener(AbilityRegistry registry) {
        this.registry = registry;
        this.logger = Logger.getLogger("MysterriaTreinar");
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        try {
            if (!(event.getEntity() instanceof Player player)) return;
            
            for (Ability ability : registry.all()) {
                if (ability instanceof DamageReactive reactive) {
                    try {
                        reactive.onPlayerDamage(event);
                    } catch (Exception e) {
                        logger.log(Level.WARNING, 
                            "Ability " + ability.getId() + " threw exception in onPlayerDamage", e);
                    }
                }
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error in damage dispatch", e);
        }
    }
}
