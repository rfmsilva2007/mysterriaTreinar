package me.rod.mysterriaTreinar.abilities;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Handles clock interactions: left-click cycles the selected ability,
 * right-click activates it (respecting cooldown).
 *
 * This class no longer knows what abilities exist or how many there are -
 * it only talks to AbilityRegistry, PlayerAbilitySelection and
 * CooldownManager, so it never needs to change when abilities are
 * added/removed.
 */
public class AbilityCycleListener implements Listener {

    private final AbilityRegistry registry;
    private final PlayerAbilitySelection selection;
    private final CooldownManager cooldowns;
    private final Logger logger;

    public AbilityCycleListener(AbilityRegistry registry, PlayerAbilitySelection selection, CooldownManager cooldowns) {
        this.registry = registry;
        this.selection = selection;
        this.cooldowns = cooldowns;
        this.logger = Logger.getLogger("MysterriaTreinar");
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        try {
            Player player = event.getPlayer();
            Action action = event.getAction();
            ItemStack hold = player.getInventory().getItemInMainHand();

            if (hold.getType() != Material.CLOCK) return;

            if (action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK) {
                selection.cycle(player);
                return;
            }

            if (action == Action.PHYSICAL) return;

            activateSelected(player);
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error in clock interaction", e);
            event.getPlayer().sendMessage("§c[Error] An error occurred. Check console.");
        }
    }

    private void activateSelected(Player player) {
        String id = selection.getSelected(player);
        registry.get(id).ifPresentOrElse(
            ability -> {
                try {
                    if (!cooldowns.isReady(player.getUniqueId(), id)) {
                        long secondsLeft = cooldowns.remainingMillis(player.getUniqueId(), id) / 1000;
                        player.sendMessage("§7Still on cooldown: " + secondsLeft + "s");
                        return;
                    }
                    ability.activate(player);
                    cooldowns.startCooldown(player.getUniqueId(), id, ability.getCooldownTicks());
                } catch (Exception e) {
                    logger.log(Level.WARNING, "Error activating ability " + id, e);
                    player.sendMessage("§c[Error] Ability failed. Check console.");
                }
            },
            () -> {
                logger.log(Level.WARNING, "Ability '" + id + "' not found in registry");
                player.sendMessage("§c[Error] Ability not found.");
            }
        );
    }
}
