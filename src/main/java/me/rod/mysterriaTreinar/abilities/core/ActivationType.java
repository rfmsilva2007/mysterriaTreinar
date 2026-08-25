package me.rod.mysterriaTreinar.abilities.core;

/**
 * Defines how an ability is activated/triggered by the player.
 */
public enum ActivationType {
    /**
     * Left-click cycles through all abilities in the same cycling group.
     * Right-click activates the current selection.
     * 
     * Multiple cycling groups can exist within the same sequence path.
     * Example: "time_manipulation" and "time_theft" groups both in Error Seq
     */
    CYCLING,

    /**
     * Right-click activates the ability directly.
     * Left-click has no effect.
     * No cycling involved.
     */
    DIRECT
}
