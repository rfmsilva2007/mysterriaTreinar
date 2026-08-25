package me.rod.mysterriaTreinar.abilities.core;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Tracks which ability id each player currently has selected, PER CYCLING
 * GROUP.
 *
 * This has to be scoped by group: a player can carry an Error paper (group
 * "time_manipulation") and a Fool paper (group "fool_trickery") at the same
 * time. Cycling or activating one must never read or overwrite the other's
 * selection. A single player -> abilityId map (the previous design) can't
 * express that - it would let cycling one group's paper silently change
 * what a completely different group's paper activates.
 */
public class PlayerAbilitySelection {

    private final Map<UUID, Map<String, String>> selectedByGroup = new HashMap<>();
    private final AbilityRegistry registry;

    public PlayerAbilitySelection(AbilityRegistry registry) {
        this.registry = registry;
    }

    /**
     * Currently selected ability id within {@code group} for this player.
     * Defaults to the first ability registered in that group if the player
     * hasn't cycled within it yet.
     */
    public String getSelected(Player player, String group) {
        String stored = selectedByGroup
                .getOrDefault(player.getUniqueId(), Map.of())
                .get(group);
        return stored != null ? stored : registry.firstInGroup(group);
    }

    public void setSelected(Player player, String group, String abilityId) {
        selectedByGroup
                .computeIfAbsent(player.getUniqueId(), k -> new HashMap<>())
                .put(group, abilityId);
    }

    /**
     * Cycle forward from {@code currentId} within its own group and store the
     * result. Returns the new selection so the caller can message the player.
     */
    public String cycle(Player player, String currentId) {
        String nextId = registry.nextInGroup(currentId);
        registry.get(currentId).ifPresent(current ->
                setSelected(player, current.getCyclingGroup(), nextId));
        return nextId;
    }

    /** Call this on PlayerQuitEvent so selections don't leak for players who left. */
    public void clear(Player player) {
        selectedByGroup.remove(player.getUniqueId());
    }
}