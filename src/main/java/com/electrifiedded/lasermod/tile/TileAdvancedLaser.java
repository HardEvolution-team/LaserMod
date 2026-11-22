package com.electrifiedded.lasermod.tile;

import com.electrifiedded.lasermod.api.ILaser;
import com.electrifiedded.lasermod.api.LaserInGame;
import com.electrifiedded.lasermod.api.LaserRegistry;
import com.electrifiedded.lasermod.util.LaserUtil;
import java.util.ArrayList;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;

/** Laser box that accepts upgrades; fires its effects while powered. */
public class TileAdvancedLaser extends TileBasicLaser {
    public final ArrayList<ItemStack> upgrades = new ArrayList<>();

    @Override
    public void updateEntity() {
        super.updateEntity();
        if (lagReduce % LaserUtil.LASER_RATE == 0 && powered()) LaserUtil.performLaserAction(this, facing());
    }

    @Override
    public void readLegacyNBT(NBTTagCompound tag) {
        upgrades.clear();
        NBTTagList list = tag.getTagList("upgrades", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < list.tagCount(); i++) {
            ItemStack s = new ItemStack(list.getCompoundTagAt(i));
            if (!s.isEmpty()) upgrades.add(s);
        }
    }

    @Override
    public void writeLegacyNBT(NBTTagCompound tag) {
        NBTTagList list = new NBTTagList();
        for (ItemStack s : upgrades) list.appendTag(s.writeToNBT(new NBTTagCompound()));
        tag.setTag("upgrades", list);
    }

    @Override
    public LaserInGame getOutputLaser(int side) {
        LaserInGame laser = super.getOutputLaser(side);
        for (ItemStack s : upgrades) {
            ILaser l = LaserRegistry.getLaserFromItem(s);
            if (l != null) laser.addLaserType(l);
        }
        return laser;
    }
}
