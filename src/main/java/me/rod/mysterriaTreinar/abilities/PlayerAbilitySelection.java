package me.rod.mysterriaTreinar.abilities;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Tracks which ability id each player currently has selected (i.e. what the
 * clock will activate on right-click). This is the replacement for the old
 * AbilityManager, but it no longer knows anything about the specific
 * abilities that exist - it just asks the AbilityRegistry what comes next.
 */
public class PlayerAbilitySelection {

    private final Map<UUID, String> selected = new HashMap<>();
    private final AbilityRegistry registry;
    private final String defaultId;

    public PlayerAbilitySelection(AbilityRegistry registry, String defaultId) {
        this.registry = registry;
        this.defaultId = defaultId;
    }

    public String getSelected(Player player) {
        return selected.getOrDefault(player.getUniqueId(), defaultId);
    }

    public void setSelected(Player player, String abilityId) {
        selected.put(player.getUniqueId(), abilityId);
    }

    public void cycle(Player player) {
        String current = getSelected(player);
        String nextId = registry.next(current);
        setSelected(player, nextId);
        player.sendMessage("§bCurrent ability: " + nextId);
    }

    /** Call this on PlayerQuitEvent so selections don't leak for players who left. */
    public void clear(Player player) {
        selected.remove(player.getUniqueId());
    }
}
