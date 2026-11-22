package com.electrifiedded.lasermod.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

/** Laser upgrades by meta; only fire(0), invisible(3), push(5), pull(6), damage(7) are functional (as in the original). */
public class ItemUpgrades extends Item {
    public static final String[] NAMES = {"fire", "water", "ice", "invisable", "mine", "push", "pull", "damage"};
    private static final int[] SHOWN = {0, 3, 5, 6, 7};

    public ItemUpgrades() {
        setHasSubtypes(true);
        setMaxDamage(0);
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (isInCreativeTab(tab)) for (int m : SHOWN) items.add(new ItemStack(this, 1, m));
    }

    @Override
    public String getTranslationKey(ItemStack stack) {
        return super.getTranslationKey() + "." + stack.getMetadata();
    }
}
