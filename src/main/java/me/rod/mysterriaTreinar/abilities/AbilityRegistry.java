package me.rod.mysterriaTreinar.abilities;

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
 * Uses a LinkedHashMap so registration order defines cycle order (left-click
 * on the clock), which is predictable and easy to reason about.
 */
public class AbilityRegistry {

    private final Map<String, Ability> abilities = new LinkedHashMap<>();

    public void register(Ability ability) {
        if (abilities.containsKey(ability.getId())) {
            throw new IllegalArgumentException("Ability with ID '" + ability.getId() + "' already registered!");
        }
        abilities.put(ability.getId(), ability);
    }

    public Optional<Ability> get(String id) {
        return Optional.ofNullable(abilities.get(id));
    }

    /** Returns the id after {@code currentId} in registration order, wrapping around. */
    public String next(String currentId) {
        List<String> ids = List.copyOf(abilities.keySet());
        int idx = ids.indexOf(currentId);
        if (idx == -1) return ids.get(0);
        return ids.get((idx + 1) % ids.size());
    }

    public Collection<Ability> all() {
        return abilities.values();
    }
}
