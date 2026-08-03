package me.rod.mysterriaTreinar.abilities;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Tracks per-player, per-ability cooldown expiry.
 *
 * This is one of the pieces the old code had no model for at all: without it,
 * every future ability would have needed to reinvent its own cooldown timer,
 * inconsistently.
 */
public class CooldownManager {

    private final Map<UUID, Map<String, Long>> readyAtByPlayer = new HashMap<>();

    public boolean isReady(UUID playerId, String abilityId) {
        long readyAt = readyAtByPlayer
                .getOrDefault(playerId, Map.of())
                .getOrDefault(abilityId, 0L);
        return System.currentTimeMillis() >= readyAt;
    }

    public synchronized void startCooldown(UUID playerId, String abilityId, long cooldownTicks) {
        long millis = cooldownTicks * 50L; // 1 tick = 50ms
        readyAtByPlayer
                .computeIfAbsent(playerId, k -> new HashMap<>())
                .put(abilityId, System.currentTimeMillis() + millis);
    }

    public long remainingMillis(UUID playerId, String abilityId) {
        long readyAt = readyAtByPlayer.getOrDefault(playerId, Map.of()).getOrDefault(abilityId, 0L);
        return Math.max(0, readyAt - System.currentTimeMillis());
    }

    /** Call this on PlayerQuitEvent so cooldown data doesn't leak for players who left. */
    public void clear(UUID playerId) {
        readyAtByPlayer.remove(playerId);
    }
}
