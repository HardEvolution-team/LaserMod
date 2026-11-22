package com.electrifiedded.lasermod.api;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class LaserRegistry {
    private static final Map<String, ILaser> MAPPINGS = new LinkedHashMap<>();
    private static final Map<String, ILaser> ITEMS = new HashMap<>();

    public static void registerLaser(String id, ILaser laser) {
        MAPPINGS.putIfAbsent(id, laser);
    }

    private static String key(Item item, int meta) {
        return item.getRegistryName() + "@" + meta;
    }

    public static void registerItemToLaser(Item item, int meta, ILaser laser) {
        ITEMS.putIfAbsent(key(item, meta), laser);
    }

    public static ILaser getLaserFromItem(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return null;
        return ITEMS.get(key(stack.getItem(), stack.getMetadata()));
    }

    public static ILaser getLaserFromId(String id) {
        return MAPPINGS.get(id);
    }

    public static String getIdFromLaser(ILaser laser) {
        for (Map.Entry<String, ILaser> e : MAPPINGS.entrySet()) if (e.getValue() == laser) return e.getKey();
        return "default";
    }
}
