package com.electrifiedded.lasermod.api;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface ILaserProvider extends ILaserLocated {
    LaserInGame getOutputLaser(int side);

    boolean isSendingSignalFromSide(World world, BlockPos asker, int side);
}
