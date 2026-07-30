package me.rod.mysterriaTreinar.abilities.impl;

import me.rod.mysterriaTreinar.abilities.Ability;
import me.rod.mysterriaTreinar.abilities.DamageReactive;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * On activation, takes a snapshot and opens a window during which damage that
 * would drop the player below a threshold gets cancelled and rewound instead.
 *
 * Unlike the old ErrorSeq1, this class owns ONLY Time Rewind's state and
 * behaviour. It implements DamageReactive itself, and implements the
 * lifecycle hooks (onExpire, onPlayerDisconnect, onPluginDisable) so its
 * pending tasks can never leak or run against a disabled plugin/offline
 * player. Adding a fourth ability later means writing a class like this one -
 * this file doesn't change.
 */
public class TimeRewindAbility implements Ability, DamageReactive {

    private static final double LOW_HEALTH_THRESHOLD = 4.0; // 2 hearts
    private static final int WINDOW_TICKS = 20 * 45;         // 45 seconds
    private static final long COOLDOWN_TICKS = 20 * 90;      // 90 seconds - tune to taste

    private final Plugin plugin;
    private final Map<UUID, BukkitTask> pendingWindows = new HashMap<>();
    private final Map<UUID, RewindState> snapshots = new HashMap<>();

    private record RewindState(Location location, double health, int food) {}

    public TimeRewindAbility(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getId() {
        return "time_rewind";
    }

    @Override
    public long getCooldownTicks() {
        return COOLDOWN_TICKS;
    }

    @Override
    public void activate(Player player) {
        UUID id = player.getUniqueId();
        if (pendingWindows.containsKey(id)) {
            player.sendMessage("§bTime is already rewinding...");
            return;
        }

        snapshots.put(id, new RewindState(
                player.getLocation().clone(),
                player.getHealth(),
                player.getFoodLevel()
        ));
        player.sendMessage("§bYou feel time rewinding...");

        BukkitTask task = plugin.getServer().getScheduler().runTaskLater(
                plugin,
                () -> onExpire(player),
                WINDOW_TICKS
        );
        pendingWindows.put(id, task);
    }

    /** Window closed naturally without the player taking lethal-ish damage. */
    @Override
    public void onExpire(Player player) {
        UUID id = player.getUniqueId();
        pendingWindows.remove(id);
        snapshots.remove(id);
        player.sendMessage("§bYour control over time has faded.");
    }

    @Override
    public void onPlayerDisconnect(Player player) {
        cancelSilently(player.getUniqueId());
    }

    @Override
    public void onPluginDisable() {
        pendingWindows.values().forEach(BukkitTask::cancel);
        pendingWindows.clear();
        snapshots.clear();
    }

    @Override
    public void onPlayerDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        UUID id = player.getUniqueId();
        if (!pendingWindows.containsKey(id)) return;

        double resultingHealth = player.getHealth() - event.getFinalDamage();
        if (resultingHealth < LOW_HEALTH_THRESHOLD) {
            event.setCancelled(true);
            executeRewind(player);
        }
    }

    private void executeRewind(Player player) {
        UUID id = player.getUniqueId();
        RewindState state = snapshots.remove(id);
        BukkitTask task = pendingWindows.remove(id);
        if (task != null) task.cancel();
        if (state == null) return;

        player.teleport(state.location());
        player.setHealth(state.health());
        player.setFoodLevel(state.food());
        player.sendMessage("§bTime rewound!");
    }

    private void cancelSilently(UUID id) {
        BukkitTask task = pendingWindows.remove(id);
        if (task != null) task.cancel();
        snapshots.remove(id);
    }
}
