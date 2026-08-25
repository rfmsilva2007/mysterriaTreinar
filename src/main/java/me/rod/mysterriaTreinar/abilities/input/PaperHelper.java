package me.rod.mysterriaTreinar.abilities.input;

import me.rod.mysterriaTreinar.MysterriaTreinar;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

/**
 * Utility class for creating and reading ability papers using Minecraft's NBT system.
 * Abilities are stored in paper items as persistent data so they survive player inventory saves.
 */
public class PaperHelper {

    private static final NamespacedKey ABILITY_ID_KEY = 
        new NamespacedKey(MysterriaTreinar.getInstance(), "ability_id");

    private PaperHelper() {
        // Utility class, no instantiation
    }

    /**
     * Create a paper ItemStack that represents an ability.
     * The ability ID is stored in the paper's NBT data.
     *
     * @param abilityId The ability ID to store
     * @param displayName The name shown to the player
     * @return ItemStack representing the ability paper
     */
    public static ItemStack createAbilityPaper(String abilityId, String displayName) {
        ItemStack paper = new ItemStack(Material.PAPER);
        ItemMeta meta = paper.getItemMeta();

        if (meta == null) {
            return paper;  // Fallback if meta can't be obtained
        }

        meta.setDisplayName(displayName);
        meta.setLore(List.of("§7Ability: " + abilityId));

        // Store ability ID in paper's persistent data
        PersistentDataContainer data = meta.getPersistentDataContainer();
        data.set(ABILITY_ID_KEY, PersistentDataType.STRING, abilityId);

        paper.setItemMeta(meta);
        return paper;
    }

    /**
     * Extract the ability ID from a paper ItemStack.
     * Returns null if the item is not a paper or doesn't have an ability ID stored.
     *
     * @param item The ItemStack to check
     * @return The ability ID, or null if not found
     */
    public static String getAbilityId(ItemStack item) {
        if (item == null || item.getType() != Material.PAPER) {
            return null;
        }

        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return null;
        }

        PersistentDataContainer data = meta.getPersistentDataContainer();
        return data.get(ABILITY_ID_KEY, PersistentDataType.STRING);
    }

    /**
     * Check if a paper has an ability ID stored.
     */
    public static boolean isAbilityPaper(ItemStack item) {
        return getAbilityId(item) != null;
    }
}
