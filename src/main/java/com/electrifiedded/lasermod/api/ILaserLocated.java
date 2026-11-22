package com.electrifiedded.lasermod.api;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Position/world access for laser tiles. NOT named getPos/getWorld: TileEntity's methods are obfuscated (func_*) at
 * runtime, so an interface method with the deobf name is never implemented by the tile (AbstractMethodError).
 */
public interface ILaserLocated {
    default BlockPos getLaserPos() { return ((TileEntity) this).getPos(); }

    default World getLaserWorld() { return ((TileEntity) this).getWorld(); }
}
