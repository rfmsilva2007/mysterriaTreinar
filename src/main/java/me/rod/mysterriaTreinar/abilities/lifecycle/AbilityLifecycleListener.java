package me.rod.mysterriaTreinar.abilities.lifecycle;

import me.rod.mysterriaTreinar.abilities.core.*;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Central place where per-player and plugin-wide lifecycle cleanup happens.
 *
 * This is the piece the old code was missing entirely: without it, a player
 * who disconnects mid-rewind (or mid whatever future ability) leaves scheduled
 * tasks and stored state behind forever, and a /reload or server stop leaves
 * scheduled tasks running against a plugin instance that no longer exists.
 */
public class AbilityLifecycleListener implements Listener {

    private final AbilityRegistry registry;
    private final CooldownManager cooldowns;
    private final PlayerAbilitySelection selection;

    public AbilityLifecycleListener(AbilityRegistry registry, CooldownManager cooldowns, PlayerAbilitySelection selection) {
        this.registry = registry;
        this.cooldowns = cooldowns;
        this.selection = selection;
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        for (Ability ability : registry.all()) {
            ability.onPlayerDisconnect(player);
        }
        cooldowns.clear(player.getUniqueId());
        selection.clear(player);
    }

    /** Call this from the plugin's onDisable(). */
    public void onPluginDisable() {
        for (Ability ability : registry.all()) {
            ability.onPluginDisable();
        }
    }
}
