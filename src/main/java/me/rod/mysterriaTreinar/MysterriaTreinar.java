package me.rod.mysterriaTreinar;

import me.rod.mysterriaTreinar.abilities.core.*;
import me.rod.mysterriaTreinar.abilities.input.*;
import me.rod.mysterriaTreinar.abilities.lifecycle.*;
import me.rod.mysterriaTreinar.abilities.sequences.error_seq.*;
import me.rod.mysterriaTreinar.commands.GivePaperCommand;

import org.bukkit.plugin.java.JavaPlugin;

/**
 * Main plugin class for MysterriaTreinar.
 *
 * onEnable() is just wiring: create the framework pieces, register the
 * abilities, register the listeners. Adding a new ability later is
 * just a new class in abilities.sequences and a register() call here.
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

        // Register abilities - organized by sequence
        // Error Seq - Time Manipulation
        abilityRegistry.register(new TimeStopAbility());
        abilityRegistry.register(new TimeRewindAbility(this));
        abilityRegistry.register(new TimeAccelerationAbility());

        // Fool Seq - (abilities will go here)

        // White Tower Seq - (future sequence)

        // No default id here anymore - selection is scoped per cycling group,
        // and each group's default is its first registered ability.
        PlayerAbilitySelection selection = new PlayerAbilitySelection(abilityRegistry);
        lifecycleListener = new AbilityLifecycleListener(abilityRegistry, cooldowns, selection);

        // Register listeners
        getServer().getPluginManager().registerEvents(
                new PaperAbilityListener(abilityRegistry, cooldowns, selection), this);
        getServer().getPluginManager().registerEvents(lifecycleListener, this);
        getServer().getPluginManager().registerEvents(
                new DamageDispatchListener(abilityRegistry), this);

        // Register commands
        getCommand("givepaper").setExecutor(new GivePaperCommand(abilityRegistry));

        getLogger().info("✓ MysterriaTreinar loaded with " +
                abilityRegistry.all().size() + " abilities");
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