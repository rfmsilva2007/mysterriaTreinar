package me.rod.mysterriaTreinar.abilities.core;

import org.bukkit.entity.Player;

/**
 * Base contract every Beyonder ability must implement.
 *
 * This replaces the old "enum + switch" approach: adding a new ability
 * no longer means editing AbilityType, AbilityManager and AbilityUse.
 * It means writing one new class and registering it in AbilityRegistry.
 *
 * Cooldown, selection cycling and lifecycle bookkeeping are handled by the
 * framework classes (AbilityRegistry, CooldownManager, PlayerAbilitySelection,
 * AbilityLifecycleListener) so each ability only needs to implement what makes
 * it unique.
 */
public interface Ability {

    /** Unique, stable id used for registration, cooldowns, messages, etc. */
    String getId();

    /** Base cooldown, in ticks (20 ticks = 1 second), before this can be used again. */
    long getCooldownTicks();

    /**
     * How this ability is activated.
     * CYCLING: Left-click cycles within the cycling group, right-click activates
     * DIRECT: Right-click activates only, no cycling
     */
    ActivationType getActivationType();

    /**
     * The cycling group this ability belongs to (only used for CYCLING activation type).
     * Abilities with the same cycling group cycle together when left-clicked.
     * Example: "time_manipulation", "time_theft", "fool_trickery"
     * 
     * Return null or empty string for DIRECT abilities (they don't cycle).
     */
    String getCyclingGroup();

    /** Called once the player has activated the ability and the cooldown check has passed. */
    void activate(Player player);

    /**
     * Called when an effect this ability started naturally expires
     * (a buff running out, a rewind window closing, etc).
     * Optional: only override it if the ability has something that expires on its own.
     */
    default void onExpire(Player player) {}

    /**
     * Called when the player disconnects while this ability has pending/active state,
     * so the ability can cancel tasks and drop that state instead of leaking it.
     */
    default void onPlayerDisconnect(Player player) {}

    /**
     * Called when the plugin is disabling (server stop or /reload),
     * so the ability can cancel any scheduled tasks it owns.
     */
    default void onPluginDisable() {}
}
