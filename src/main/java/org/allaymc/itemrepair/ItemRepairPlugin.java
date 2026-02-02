package org.allaymc.itemrepair;

import org.allaymc.api.command.CommandSender;
import org.allaymc.api.plugin.Plugin;
import org.allaymc.api.registry.Registries;
import org.allaymc.itemrepair.command.RepairCommand;
import org.allaymc.itemrepair.manager.RepairManager;

/**
 * Main plugin class for ItemRepair plugin.
 * Allows players to repair damaged items using experience levels.
 */
public class ItemRepairPlugin extends Plugin {

    private static ItemRepairPlugin instance;
    private RepairManager repairManager;

    @Override
    public void onLoad() {
        instance = this;
        this.pluginLogger.info("ItemRepair is loading...");
        this.repairManager = new RepairManager(this);
    }

    @Override
    public void onEnable() {
        this.pluginLogger.info("ItemRepair is enabling...");
        // Register commands
        Registries.COMMANDS.register(new RepairCommand(repairManager));
        this.pluginLogger.info("ItemRepair enabled successfully! Use /repair to repair your items.");
    }

    @Override
    public void onDisable() {
        this.pluginLogger.info("ItemRepair is disabling...");
        if (repairManager != null) {
            repairManager.shutdown();
        }
        this.pluginLogger.info("ItemRepair disabled!");
    }

    public static ItemRepairPlugin getInstance() {
        return instance;
    }

    public RepairManager getRepairManager() {
        return repairManager;
    }

    /**
     * Send a message to a command sender.
     */
    public static void sendMessage(CommandSender sender, String message) {
        sender.sendMessage(message);
    }
}
