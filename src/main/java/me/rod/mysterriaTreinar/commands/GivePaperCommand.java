package me.rod.mysterriaTreinar.commands;

import me.rod.mysterriaTreinar.abilities.core.AbilityRegistry;
import me.rod.mysterriaTreinar.abilities.input.PaperHelper;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Admin command to give ability papers for testing.
 * Usage: /givepaper <ability_id>
 * Example: /givepaper time_stop
 */
public class GivePaperCommand implements CommandExecutor {

    private final AbilityRegistry registry;

    public GivePaperCommand(AbilityRegistry registry) {
        this.registry = registry;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cOnly players can use this command!");
            return true;
        }

        if (args.length == 0) {
            player.sendMessage("§cUsage: /givepaper <ability_id>");
            player.sendMessage("§eRegistered abilities: " +
                    registry.all().stream().map(a -> a.getId()).reduce((a, b) -> a + ", " + b).orElse("(none)"));
            return true;
        }

        String abilityId = args[0];
        if (registry.get(abilityId).isEmpty()) {
            player.sendMessage("§cUnknown ability: " + abilityId);
            return true;
        }

        ItemStack paper = PaperHelper.createAbilityPaper(abilityId, "§6" + abilityId);
        player.getInventory().addItem(paper);
        player.sendMessage("§aReceived ability paper: §6" + abilityId);
        return true;
    }
}