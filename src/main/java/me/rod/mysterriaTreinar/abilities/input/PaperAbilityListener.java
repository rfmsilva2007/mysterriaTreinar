package me.rod.mysterriaTreinar.abilities.input;

import me.rod.mysterriaTreinar.abilities.core.*;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Unified listener for paper-based ability activation.
 * Handles both CYCLING and DIRECT activation types.
 *
 * Left-click on a CYCLING ability paper cycles within THAT PAPER'S OWN
 * cycling group. Right-click on any ability paper activates whichever
 * ability is currently selected within that paper's group (respecting
 * cooldown). The group always comes from the paper actually in hand -
 * never from "whatever the player selected last," so carrying papers from
 * two different groups can't cross-contaminate each other's selection.
 */
public class PaperAbilityListener implements Listener {

    private final AbilityRegistry registry;
    private final CooldownManager cooldowns;
    private final PlayerAbilitySelection selection;
    private final Logger logger;

    public PaperAbilityListener(AbilityRegistry registry, CooldownManager cooldowns,
                                PlayerAbilitySelection selection) {
        this.registry = registry;
        this.cooldowns = cooldowns;
        this.selection = selection;
        this.logger = Logger.getLogger("MysterriaTreinar");
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        try {
            Player player = event.getPlayer();
            ItemStack item = player.getInventory().getItemInMainHand();

            String abilityId = PaperHelper.getAbilityId(item);
            if (abilityId == null) return;

            Ability paperAbility = registry.get(abilityId).orElse(null);
            if (paperAbility == null) {
                logger.warning("Paper references unknown ability: " + abilityId);
                player.sendMessage("§c[Error] Ability not found.");
                return;
            }

            Action action = event.getAction();

            if (action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK) {
                if (paperAbility.getActivationType() == ActivationType.CYCLING) {
                    handleCycleLeft(player, paperAbility);
                }
                // DIRECT abilities ignore left-click
                return;
            }

            if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
                handleActivate(player, paperAbility);
            }

        } catch (Exception e) {
            logger.log(Level.WARNING, "Error in paper ability listener", e);
            event.getPlayer().sendMessage("§c[Error] An error occurred. Check console.");
        }
    }

    private void handleCycleLeft(Player player, Ability paperAbility) {
        try {
            // Start from THIS paper's own group selection, not a global "last selected"
            String group = paperAbility.getCyclingGroup();
            String currentId = selection.getSelected(player, group);
            String nextId = selection.cycle(player, currentId);
            player.sendMessage("§bCurrent ability: " + nextId);
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error cycling ability", e);
            player.sendMessage("§c[Error] Cycling failed.");
        }
    }

    private void handleActivate(Player player, Ability paperAbility) {
        try {
            String abilityId;
            if (paperAbility.getActivationType() == ActivationType.CYCLING) {
                // Use whatever's selected within THIS paper's group specifically
                abilityId = selection.getSelected(player, paperAbility.getCyclingGroup());
            } else {
                abilityId = paperAbility.getId();
            }

            Ability ability = registry.get(abilityId).orElse(paperAbility);
            UUID playerId = player.getUniqueId();

            if (!cooldowns.isReady(playerId, abilityId)) {
                long secondsLeft = cooldowns.remainingMillis(playerId, abilityId) / 1000;
                player.sendMessage("§7Still on cooldown: " + secondsLeft + "s");
                return;
            }

            ability.activate(player);
            cooldowns.startCooldown(playerId, abilityId, ability.getCooldownTicks());

        } catch (Exception e) {
            logger.log(Level.WARNING, "Error activating ability " + paperAbility.getId(), e);
            player.sendMessage("§c[Error] Ability failed. Check console.");
        }
    }
}