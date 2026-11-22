package com.electrifiedded.lasermod.tile;

import com.electrifiedded.archaiclib.legacy.util.LegacyFacing;
import com.electrifiedded.lasermod.api.ILaserProvider;
import com.electrifiedded.lasermod.api.LaserInGame;
import com.electrifiedded.lasermod.api.LaserRegistry;
import com.electrifiedded.lasermod.util.LaserUtil;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TileBasicLaser extends TileLaserDevice implements ILaserProvider {
    @Override
    public void updateEntity() {
        lagReduce++;
        if (lagReduce % LaserUtil.TICK_RATE == 0) scheduleSelf(1);
    }

    public int facing() {
        return LaserUtil.getOrientation(getBlockMetadata());
    }

    @Override
    public LaserInGame getOutputLaser(int side) {
        return new LaserInGame(LaserRegistry.getLaserFromId("default")).setSide(LegacyFacing.opposite(side));
    }

    @Override
    public boolean isSendingSignalFromSide(World w, BlockPos asker, int side) {
        return powered() && facing() == side;
    }
}
