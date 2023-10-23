package com.crabcode.factory.util;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class InventoryUtil {

    public static boolean isValid(ItemStack item) {
        return item != null && item.getType() != Material.AIR;
    }
}
