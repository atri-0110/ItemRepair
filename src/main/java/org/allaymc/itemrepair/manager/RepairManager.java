package org.allaymc.itemrepair.manager;

import org.allaymc.api.item.ItemStack;
import org.allaymc.api.item.type.ItemTypes;

/**
 * Manager for handling item repair operations.
 * Calculates repair costs and applies repairs to damaged items.
 */
public class RepairManager {

    public RepairManager() {
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

        // Get damage value using the proper API
        int maxDamage = item.getMaxDamage();
        if (maxDamage <= 0) {
            return -1; // Item doesn't have durability
        }

        int currentDamage = item.getDamage();
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

        int maxDamage = item.getMaxDamage();
        if (maxDamage <= 0) {
            return false; // Item doesn't have durability
        }

        // Reset damage to 0 using the proper API
        item.setDamage(0);
        return true;
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

        int maxDamage = item.getMaxDamage();
        if (maxDamage <= 0) {
            return false; // Item doesn't have durability
        }

        int currentDamage = item.getDamage();
        return currentDamage > 0; // Item is damaged
    }
}
