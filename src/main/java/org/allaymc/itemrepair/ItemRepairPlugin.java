package org.allaymc.itemrepair;

import lombok.Getter;
import org.allaymc.api.command.CommandResult;
import org.allaymc.api.command.CommandSender;
import org.allaymc.api.command.tree.CommandTree;
import org.allaymc.api.eventbus.EventHandler;
import org.allaymc.api.eventbus.event.player.PlayerInteractEvent;
import org.allaymc.api.i18n.I18n;
import org.allaymc.api.i18n.TrKeys;
import org.allaymc.api.plugin.AllayPlugin;
import org.allaymc.api.plugin.container.PluginContainer;
import org.allaymc.api.registry.Registries;
import org.allaymc.api.server.Server;
import org.allaymc.api.utils.Identifier;
import org.allaymc.itemrepair.command.RepairCommand;
import org.allaymc.itemrepair.manager.RepairManager;

/**
 * Main plugin class for ItemRepair plugin.
 * Allows players to repair damaged items using experience levels.
 */
public class ItemRepairPlugin extends AllayPlugin {

    @Getter
    private static ItemRepairPlugin instance;

    @Getter
    private RepairManager repairManager;

    @Override
    public void onEnable() {
        instance = this;

        // Initialize repair manager
        this.repairManager = new RepairManager(this);

        // Register commands
        Registries.COMMANDS.register(new RepairCommand(repairManager));

        // Log startup
        getLog().info("ItemRepair plugin enabled! Use /repair to repair your items.");
    }

    @Override
    public void onDisable() {
        if (repairManager != null) {
            repairManager.shutdown();
        }
        getLog().info("ItemRepair plugin disabled!");
    }

    /**
     * Send a message to a command sender with proper translation support.
     */
    public static void sendMessage(CommandSender sender, String message) {
        sender.sendMessage(I18n.get().tr(TrKeys.M_CHAT_TYPE_TEXT,
                sender instanceof org.allaymc.api.entity.interfaces.EntityPlayer player
                        ? player.getDisplayName()
                        : "Console",
                message));
    }
}
