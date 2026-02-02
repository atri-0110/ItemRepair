package org.allaymc.itemrepair.command;

import org.allaymc.api.command.Command;
import org.allaymc.api.command.CommandResult;
import org.allaymc.api.command.CommandSender;
import org.allaymc.api.command.tree.CommandTree;
import org.allaymc.api.entity.interfaces.EntityPlayer;
import org.allaymc.api.item.ItemStack;
import org.allaymc.api.item.interfaces.ItemAirStack;
import org.allaymc.itemrepair.ItemRepairPlugin;
import org.allaymc.itemrepair.manager.RepairManager;

/**
 * Command handler for /repair.
 * Allows players to repair the item they're holding.
 */
public class RepairCommand extends Command {

    private final RepairManager repairManager;

    public RepairCommand(RepairManager repairManager) {
        super("repair", "Repair the item in your hand", "itemrepair.use");
        this.repairManager = repairManager;
    }

    @Override
    public void prepareCommandTree(CommandTree tree) {
        // /repair - Repair item in hand
        tree.getRoot()
                .exec(context -> {
                    if (!(context.getSender() instanceof EntityPlayer player)) {
                        ItemRepairPlugin.sendMessage(context.getSender(),
                                "§cThis command can only be used by players!");
                        return context.fail();
                    }

                    // Check permission
                    if (player.hasPermission("itemrepair.use") != org.allaymc.api.permission.Tristate.TRUE) {
                        ItemRepairPlugin.sendMessage(player,
                                "§cYou don't have permission to use this command!");
                        return context.fail();
                    }

                    // Get item in hand
                    ItemStack item = player.getContainer(org.allaymc.api.container.ContainerTypes.HAND).getItemStack(0);

                    if (item == null || item.getItemType() instanceof ItemAirStack) {
                        ItemRepairPlugin.sendMessage(player,
                                "§cYou're not holding any item!");
                        return context.fail();
                    }

                    // Check if item is repairable
                    if (!repairManager.isRepairable(item)) {
                        ItemRepairPlugin.sendMessage(player,
                                "§cThis item cannot be repaired or is not damaged!");
                        return context.fail();
                    }

                    // Calculate cost
                    int cost = repairManager.calculateRepairCost(item);
                    if (cost < 0) {
                        ItemRepairPlugin.sendMessage(player,
                                "§cThis item cannot be repaired!");
                        return context.fail();
                    }

                    // Check if player has enough XP
                    int playerLevel = player.getExperienceLevel();
                    if (playerLevel < cost) {
                        ItemRepairPlugin.sendMessage(player,
                                String.format("§cYou need §e%d §cexperience levels to repair this item (you have §e%d§c)!",
                                        cost, playerLevel));
                        return context.fail();
                    }

                    // Repair the item
                    ItemStack repairedItem = item.getItemType().createItemStack(
                            org.allaymc.api.item.type.ItemStackInitInfo.builder()
                                    .count(item.getCount())
                                    .meta(0) // Reset damage
                                    .extraTag(item.getExtraTag())
                                    .build()
                    );

                    if (repairedItem != null) {
                        // Remove XP
                        player.setExperienceLevel(playerLevel - cost);

                        // Replace item in hand
                        player.getContainer(org.allaymc.api.container.ContainerTypes.HAND).setItemStack(0, repairedItem);

                        ItemRepairPlugin.sendMessage(player,
                                String.format("§aItem repaired! §e%d §aexperience levels spent.", cost));
                        return context.success();
                    }

                    ItemRepairPlugin.sendMessage(player, "§cFailed to repair item!");
                    return context.fail();
                });

        // /repair check - Check repair cost without repairing
        tree.getRoot()
                .key("check")
                .exec(context -> {
                    if (!(context.getSender() instanceof EntityPlayer player)) {
                        ItemRepairPlugin.sendMessage(context.getSender(),
                                "§cThis command can only be used by players!");
                        return context.fail();
                    }

                    ItemStack item = player.getContainer(org.allaymc.api.container.ContainerTypes.HAND).getItemStack(0);

                    if (item == null || item.getItemType() instanceof ItemAirStack) {
                        ItemRepairPlugin.sendMessage(player,
                                "§cYou're not holding any item!");
                        return context.fail();
                    }

                    if (!repairManager.isRepairable(item)) {
                        ItemRepairPlugin.sendMessage(player,
                                "§cThis item cannot be repaired or is not damaged!");
                        return context.fail();
                    }

                    int cost = repairManager.calculateRepairCost(item);
                    int playerLevel = player.getExperienceLevel();

                    if (cost < 0) {
                        ItemRepairPlugin.sendMessage(player, "§cThis item cannot be repaired!");
                        return context.fail();
                    }

                    String statusColor = playerLevel >= cost ? "§a" : "§c";
                    ItemRepairPlugin.sendMessage(player,
                            String.format("§eRepair Cost: §f%d §eexperience levels %s(You have: §f%d§s)",
                                    cost, statusColor, playerLevel));
                    return context.success();
                });
    }
}
