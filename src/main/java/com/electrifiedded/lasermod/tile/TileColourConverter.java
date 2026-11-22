package com.electrifiedded.lasermod.tile;

import com.electrifiedded.archaiclib.legacy.util.LegacyFacing;
import com.electrifiedded.lasermod.api.ILaserProvider;
import com.electrifiedded.lasermod.api.ILaserReciver;
import com.electrifiedded.lasermod.api.LaserInGame;
import com.electrifiedded.lasermod.util.LaserUtil;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/** Takes a laser in at the back and re-emits it at the front in its dye colour. */
public class TileColourConverter extends TileLaserDevice implements ILaserProvider, ILaserReciver {
    public LaserInGame laser;
    public int colour = 14;

    public int facing() {
        return LaserUtil.getOrientation(getBlockMetadata());
    }

    @Override
    public void updateEntity() {
        lagReduce++;
        if (!world.isRemote && lagReduce % LaserUtil.TICK_RATE != 0) {
            if (laser != null && !LaserUtil.isValidSourceOfPowerOnSide(this, LegacyFacing.opposite(facing()))) {
                laser = null;
                sync();
            }
            scheduleSelf(4);
        }
        if (lagReduce % LaserUtil.LASER_RATE == 0 && laser != null) LaserUtil.performLaserAction(this, facing());
    }

    @Override
    public void readLegacyNBT(NBTTagCompound tag) {
        if (tag.hasKey("colour")) colour = tag.getInteger("colour") & 15;
        laser = tag.hasKey("laser") ? new LaserInGame(tag.getCompoundTag("laser")) : null;
    }

    @Override
    public void writeLegacyNBT(NBTTagCompound tag) {
        tag.setInteger("colour", colour);
        if (laser != null) tag.setTag("laser", laser.writeToNBT(new NBTTagCompound()));
    }

    @Override
    public LaserInGame getOutputLaser(int side) {
        if (laser == null) return null;
        LaserInGame out = laser.copy().setSide(LegacyFacing.opposite(side));
        out.red = (int) (LaserUtil.LASER_COLOUR_TABLE[colour][0] * 255.0F);
        out.green = (int) (LaserUtil.LASER_COLOUR_TABLE[colour][1] * 255.0F);
        out.blue = (int) (LaserUtil.LASER_COLOUR_TABLE[colour][2] * 255.0F);
        return out;
    }

    @Override
    public boolean canPassOnSide(World w, BlockPos origin, int side, LaserInGame l) {
        return side == LegacyFacing.opposite(facing());
    }

    @Override
    public void passLaser(World w, BlockPos origin, int side, LaserInGame l) {
        if (laser == null) {
            laser = l.copy();
            sync();
            scheduleSelf(4);
        }
    }

    @Override
    public void removeLasersFromSide(World w, BlockPos origin, int side) {
        if (side == LegacyFacing.opposite(facing()) && laser != null) {
            laser = null;
            sync();
            scheduleSelf(4);
        }
    }

    @Override
    public boolean isSendingSignalFromSide(World w, BlockPos asker, int side) {
        return laser != null && side == facing();
    }
}
