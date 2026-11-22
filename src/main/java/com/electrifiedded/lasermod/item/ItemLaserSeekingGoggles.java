package com.electrifiedded.lasermod.item;

import net.minecraft.entity.Entity;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;

/** Helmet that lets the wearer faintly see invisible lasers. */
public class ItemLaserSeekingGoggles extends ItemArmor {
    public ItemLaserSeekingGoggles() {
        super(ArmorMaterial.CHAIN, 0, EntityEquipmentSlot.HEAD);
    }

    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
        return "lasermod:textures/armor/lasergoogles.png";
    }
}
