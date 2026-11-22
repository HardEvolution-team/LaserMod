package com.electrifiedded.lasermod.api;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface ILaserReciver extends ILaserLocated {
    boolean canPassOnSide(World world, BlockPos origin, int side, LaserInGame laser);

    void passLaser(World world, BlockPos origin, int side, LaserInGame laser);

    void removeLasersFromSide(World world, BlockPos origin, int side);
}
