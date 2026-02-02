package org.allaymc.itemrepair.manager;

import lombok.Getter;
import org.allaymc.api.item.ItemStack;
import org.allaymc.api.item.interfaces.ItemAirStack;
import org.allaymc.api.item.type.ItemType;
import org.allaymc.api.item.type.ItemTypes;
import org.allaymc.api.registry.Registries;
import org.allaymc.itemrepair.ItemRepairPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Manager for handling item repair operations.
 * Calculates repair costs and applies repairs to damaged items.
 */
public class RepairManager {

    private final ItemRepairPlugin plugin;

    @Getter
    private final Map<UUID, RepairSession> activeSessions = new HashMap<>();

    public RepairManager(ItemRepairPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Calculate the repair cost in experience levels for a damaged item.
     *
     * @param item The item to repair
     * @return The number of experience levels required
     */
    public int calculateRepairCost(ItemStack item) {
        if (item == null || item.getItemType() instanceof ItemAirStack) {
            return -1;
        }

        // Get damage value (metadata)
        int maxDamage = getMaxDurability(item.getItemType());
        if (maxDamage <= 0) {
            return -1; // Item doesn't have durability
        }

        int currentDamage = item.getMeta(); // Meta stores damage
        if (currentDamage <= 0) {
            return -1; // Item is not damaged
        }

        // Calculate cost: 1 experience level per 10% of max durability
        double damagePercent = (double) currentDamage / maxDamage;
        int cost = (int) Math.ceil(damagePercent * 10);

        return Math.max(1, cost); // Minimum 1 level
    }

    /**
     * Repair an item completely.
     *
     * @param item The item to repair
     * @return true if repair was successful, false otherwise
     */
    public boolean repairItem(ItemStack item) {
        if (item == null || item.getItemType() instanceof ItemAirStack) {
            return false;
        }

        int maxDamage = getMaxDurability(item.getItemType());
        if (maxDamage <= 0) {
            return false; // Item doesn't have durability
        }

        // Reset damage (metadata) to 0
        var itemType = item.getItemType();
        var count = item.getCount();
        var extraTag = item.getExtraTag();

        // Create new item with no damage
        ItemStack repairedItem = itemType.createItemStack(
                org.allaymc.api.item.type.ItemStackInitInfo.builder()
                        .count(count)
                        .meta(0) // Reset damage
                        .extraTag(extraTag)
                        .build()
        );

        // Replace the item
        return repairedItem != null;
    }

    /**
     * Get the maximum durability for an item type.
     *
     * @param itemType The item type
     * @return Maximum durability value, or 0 if not applicable
     */
    private int getMaxDurability(ItemType itemType) {
        // Return durability for common tool/armor types
        var identifier = itemType.getIdentifier();

        // Tools
        if (identifier.getPath().contains("wooden")) return 59;
        if (identifier.getPath().contains("stone")) return 131;
        if (identifier.getPath().contains("iron")) return 250;
        if (identifier.getPath().contains("golden")) return 32;
        if (identifier.getPath().contains("diamond")) return 1561;
        if (identifier.getPath().contains("netherite")) return 2031;

        // Armor
        if (identifier.getPath().contains("leather")) return 55;
        if (identifier.getPath().contains("chainmail")) return 165;
        if (identifier.getPath().contains("iron") && identifier.getPath().contains("helmet")) return 165;
        if (identifier.getPath().contains("iron") && identifier.getPath().contains("chestplate")) return 240;
        if (identifier.getPath().contains("iron") && identifier.getPath().contains("leggings")) return 225;
        if (identifier.getPath().contains("iron") && identifier.getPath().contains("boots")) return 195;
        if (identifier.getPath().contains("golden") && identifier.getPath().contains("helmet")) return 77;
        if (identifier.getPath().contains("golden") && identifier.getPath().contains("chestplate")) return 112;
        if (identifier.getPath().contains("golden") && identifier.getPath().contains("leggings")) return 105;
        if (identifier.getPath().contains("golden") && identifier.getPath().contains("boots")) return 91;
        if (identifier.getPath().contains("diamond") && identifier.getPath().contains("helmet")) return 363;
        if (identifier.getPath().contains("diamond") && identifier.getPath().contains("chestplate")) return 528;
        if (identifier.getPath().contains("diamond") && identifier.getPath().contains("leggings")) return 495;
        if (identifier.getPath().contains("diamond") && identifier.getPath().contains("boots")) return 429;
        if (identifier.getPath().contains("netherite") && identifier.getPath().contains("helmet")) return 407;
        if (identifier.getPath().contains("netherite") && identifier.getPath().contains("chestplate")) return 592;
        if (identifier.getPath().contains("netherite") && identifier.getPath().contains("leggings")) return 555;
        if (identifier.getPath().contains("netherite") && identifier.getPath().contains("boots")) return 481;

        // Shield
        if (identifier.getPath().equals("shield")) return 336;

        // Elytra
        if (identifier.getPath().equals("elytra")) return 432;

        return 0; // No durability
    }

    /**
     * Check if an item is repairable (has durability and is damaged).
     *
     * @param item The item to check
     * @return true if the item can be repaired
     */
    public boolean isRepairable(ItemStack item) {
        if (item == null || item.getItemType() instanceof ItemAirStack) {
            return false;
        }

        int maxDamage = getMaxDurability(item.getItemType());
        if (maxDamage <= 0) {
            return false; // Item doesn't have durability
        }

        int currentDamage = item.getMeta();
        return currentDamage > 0; // Item is damaged
    }

    /**
     * Start a new repair session for a player.
     *
     * @param playerUuid The player's UUID
     * @param slot The slot number being repaired
     */
    public void startSession(UUID playerUuid, int slot) {
        activeSessions.put(playerUuid, new RepairSession(slot));
    }

    /**
     * End the repair session for a player.
     *
     * @param playerUuid The player's UUID
     */
    public void endSession(UUID playerUuid) {
        activeSessions.remove(playerUuid);
    }

    /**
     * Get the active repair session for a player.
     *
     * @param playerUuid The player's UUID
     * @return The repair session, or null if none exists
     */
    public RepairSession getSession(UUID playerUuid) {
        return activeSessions.get(playerUuid);
    }

    public void shutdown() {
        activeSessions.clear();
    }

    /**
     * Represents an active repair session.
     */
    @Getter
    public static class RepairSession {
        private final int slot;
        private final long startTime;

        public RepairSession(int slot) {
            this.slot = slot;
            this.startTime = System.currentTimeMillis();
        }

        /**
         * Check if the session has expired (30 seconds timeout).
         *
         * @return true if expired
         */
        public boolean isExpired() {
            return System.currentTimeMillis() - startTime > 30000;
        }
    }
}
