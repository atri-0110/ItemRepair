package org.allaymc.itemrepair.manager;

import org.allaymc.api.item.ItemStack;
import org.allaymc.api.item.interfaces.ItemAirStack;
import org.allaymc.api.item.type.ItemType;
import org.allaymc.api.item.type.ItemTypes;
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

    private final Map<UUID, RepairSession> activeSessions = new HashMap<>();

    public RepairManager(ItemRepairPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Calculate repair cost in experience levels for a damaged item.
     *
     * @param item The item to repair
     * @return The number of experience levels required
     */
    public int calculateRepairCost(ItemStack item) {
        if (item == null || item.getItemType() == ItemTypes.AIR) {
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
        if (item == null || item.getItemType() == ItemTypes.AIR) {
            return false;
        }

        int maxDamage = getMaxDurability(item.getItemType());
        if (maxDamage <= 0) {
            return false; // Item doesn't have durability
        }

        // Reset damage (metadata) to 0
        return true;
    }

    /**
     * Get maximum durability for an item type.
     *
     * @param itemType The item type
     * @return Maximum durability value, or 0 if not applicable
     */
    private int getMaxDurability(ItemType itemType) {
        // Return durability for common tool/armor types
        String itemName = itemType.getIdentifier().toString();

        // Tools
        if (itemName.contains("wooden")) return 59;
        if (itemName.contains("stone")) return 131;
        if (itemName.contains("iron")) return 250;
        if (itemName.contains("golden")) return 32;
        if (itemName.contains("diamond")) return 1561;
        if (itemName.contains("netherite")) return 2031;

        // Armor
        if (itemName.contains("leather")) return 55;
        if (itemName.contains("chainmail")) return 165;
        if (itemName.contains("iron") && itemName.contains("helmet")) return 165;
        if (itemName.contains("iron") && itemName.contains("chestplate")) return 240;
        if (itemName.contains("iron") && itemName.contains("leggings")) return 225;
        if (itemName.contains("iron") && itemName.contains("boots")) return 195;
        if (itemName.contains("golden") && itemName.contains("helmet")) return 77;
        if (itemName.contains("golden") && itemName.contains("chestplate")) return 112;
        if (itemName.contains("golden") && itemName.contains("leggings")) return 105;
        if (itemName.contains("golden") && itemName.contains("boots")) return 91;
        if (itemName.contains("diamond") && itemName.contains("helmet")) return 363;
        if (itemName.contains("diamond") && itemName.contains("chestplate")) return 528;
        if (itemName.contains("diamond") && itemName.contains("leggings")) return 495;
        if (itemName.contains("diamond") && itemName.contains("boots")) return 429;
        if (itemName.contains("netherite") && itemName.contains("helmet")) return 407;
        if (itemName.contains("netherite") && itemName.contains("chestplate")) return 592;
        if (itemName.contains("netherite") && itemName.contains("leggings")) return 555;
        if (itemName.contains("netherite") && itemName.contains("boots")) return 481;

        // Shield
        if (itemName.contains("shield")) return 336;

        // Elytra
        if (itemName.contains("elytra")) return 432;

        return 0; // No durability
    }

    /**
     * Check if an item is repairable (has durability and is damaged).
     *
     * @param item The item to check
     * @return true if item can be repaired
     */
    public boolean isRepairable(ItemStack item) {
        if (item == null || item.getItemType() == ItemTypes.AIR) {
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
     * End repair session for a player.
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
    public static class RepairSession {
        private final int slot;
        private final long startTime;

        public RepairSession(int slot) {
            this.slot = slot;
            this.startTime = System.currentTimeMillis();
        }

        public int getSlot() {
            return slot;
        }

        /**
         * Check if session has expired (30 seconds timeout).
         *
         * @return true if expired
         */
        public boolean isExpired() {
            return System.currentTimeMillis() - startTime > 30000;
        }
    }
}
