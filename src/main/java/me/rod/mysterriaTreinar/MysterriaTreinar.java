package me.rod.mysterriaTreinar;

import me.rod.mysterriaTreinar.abilities.AbilityCycleListener;
import me.rod.mysterriaTreinar.abilities.AbilityLifecycleListener;
import me.rod.mysterriaTreinar.abilities.AbilityRegistry;
import me.rod.mysterriaTreinar.abilities.CooldownManager;
import me.rod.mysterriaTreinar.abilities.DamageDispatchListener;
import me.rod.mysterriaTreinar.abilities.PlayerAbilitySelection;
import me.rod.mysterriaTreinar.abilities.impl.TimeAccelerationAbility;
import me.rod.mysterriaTreinar.abilities.impl.TimeRewindAbility;
import me.rod.mysterriaTreinar.abilities.impl.TimeStopAbility;

import org.bukkit.plugin.java.JavaPlugin;

/**
 * Main plugin class for MysterriaTreinar.

 * onEnable() is now just wiring: create the framework pieces, register the
 * three abilities, register the listeners. Adding a fourth ability later is
 * a one-line registry.register(...) call here plus a new class in
 * abilities.impl - nothing else in this file changes.
 */
public final class MysterriaTreinar extends JavaPlugin {

    private static MysterriaTreinar instance;

    private AbilityRegistry abilityRegistry;
    private AbilityLifecycleListener lifecycleListener;

    @Override
    public void onEnable() {
        instance = this;

        abilityRegistry = new AbilityRegistry();
        CooldownManager cooldowns = new CooldownManager();

        // register abilities - order here defines clock-cycle order
        abilityRegistry.register(new TimeStopAbility());
        abilityRegistry.register(new TimeRewindAbility(this));
        abilityRegistry.register(new TimeAccelerationAbility());

        PlayerAbilitySelection selection = new PlayerAbilitySelection(abilityRegistry, "time_stop");
        lifecycleListener = new AbilityLifecycleListener(abilityRegistry, cooldowns, selection);

        getServer().getPluginManager().registerEvents(
                new AbilityCycleListener(abilityRegistry, selection, cooldowns), this);
        getServer().getPluginManager().registerEvents(lifecycleListener, this);
        getServer().getPluginManager().registerEvents(
                new DamageDispatchListener(abilityRegistry), this);
    }

    @Override
    public void onDisable() {
        // give every ability a chance to cancel its own scheduled tasks
        if (lifecycleListener != null) {
            lifecycleListener.onPluginDisable();
        }
    }

    /** Get the singleton plugin instance. */
    public static MysterriaTreinar getInstance() {
        return instance;
    }

    /** Exposed in case other classes need to register more abilities later. */
    public AbilityRegistry getAbilityRegistry() {
        return abilityRegistry;
    }
}
