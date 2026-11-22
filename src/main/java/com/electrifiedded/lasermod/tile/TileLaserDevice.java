package com.electrifiedded.lasermod.tile;

import com.electrifiedded.archaiclib.legacy.tileentity.LegacyTileEntity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TileLaserDevice extends LegacyTileEntity {
    protected int lagReduce = -1;

    /** replaces the old per-device packets: resend the full description NBT */
    public void sync() {
        if (world != null && !world.isRemote) {
            markDirty();
            markBlockForUpdate();
        }
    }

    protected void scheduleSelf(int delay) {
        if (world != null && !world.isRemote) world.scheduleUpdate(getPos(), getBlockType(), delay);
    }

    protected boolean powered() {
        return world != null && world.isBlockPowered(getPos());
    }

    @Override
    public void onDescriptionPacket(NBTTagCompound tag) {
        if (world != null) world.markBlockRangeForRenderUpdate(getPos(), getPos());
    }

    @Override
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox() {
        return INFINITE_EXTENT_AABB;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public double getMaxRenderDistanceSquared() {
        return 65536.0;
    }
}
