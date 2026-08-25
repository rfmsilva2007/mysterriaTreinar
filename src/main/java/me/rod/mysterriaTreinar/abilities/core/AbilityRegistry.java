package me.rod.mysterriaTreinar.abilities.core;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Holds every registered ability, keyed by id.
 *
 * This is what replaces AbilityType + the switch statements: to add a new
 * ability you write a class implementing Ability and call register() on it
 * during onEnable(). Nothing else in the plugin needs to change.
 *
 * Uses a LinkedHashMap so registration order is predictable.
 */
public class AbilityRegistry {

    private final Map<String, Ability> abilities = new LinkedHashMap<>();

    public synchronized void register(Ability ability) {
        if (abilities.containsKey(ability.getId())) {
            throw new IllegalArgumentException("Ability with ID '" + ability.getId() + "' already registered!");
        }
        abilities.put(ability.getId(), ability);
    }

    public Optional<Ability> get(String id) {
        return Optional.ofNullable(abilities.get(id));
    }

    /**
     * Get the next ability ID in the same cycling group.
     * Used when player left-clicks a cycling ability.
     *
     * Only returns abilities with:
     * 1. Same CyclingGroup as current ability
     * 2. ActivationType == CYCLING
     */
    public String nextInGroup(String currentId) {
        Ability current = get(currentId).orElse(null);
        if (current == null) return currentId;

        if (current.getActivationType() != ActivationType.CYCLING) {
            return currentId;
        }

        String groupName = current.getCyclingGroup();
        if (groupName == null || groupName.isEmpty()) {
            return currentId;
        }

        List<String> idsInGroup = idsInGroup(groupName);
        if (idsInGroup.isEmpty()) return currentId;

        int idx = idsInGroup.indexOf(currentId);
        if (idx == -1) return idsInGroup.get(0);

        return idsInGroup.get((idx + 1) % idsInGroup.size());
    }

    /**
     * The first-registered ability id in {@code groupName}. Used as the
     * default selection for a player who hasn't cycled within this group yet.
     *
     * @throws IllegalStateException if no CYCLING ability is registered in that group -
     *         this should only happen if a pathway module is wired up wrong, so it's
     *         better to fail loudly at the point of use than silently return null.
     */
    public String firstInGroup(String groupName) {
        List<String> idsInGroup = idsInGroup(groupName);
        if (idsInGroup.isEmpty()) {
            throw new IllegalStateException("No CYCLING abilities registered in group: " + groupName);
        }
        return idsInGroup.get(0);
    }

    private List<String> idsInGroup(String groupName) {
        return abilities.entrySet().stream()
                .filter(e -> e.getValue().getActivationType() == ActivationType.CYCLING)
                .filter(e -> groupName.equals(e.getValue().getCyclingGroup()))
                .map(Map.Entry::getKey)
                .toList();
    }

    public Collection<Ability> all() {
        return abilities.values();
    }

    public Collection<Ability> getByGroup(String groupName) {
        return abilities.values().stream()
                .filter(a -> groupName.equals(a.getCyclingGroup()))
                .toList();
    }
}