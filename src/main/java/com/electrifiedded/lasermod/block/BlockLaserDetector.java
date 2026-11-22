package com.electrifiedded.lasermod.block;

import com.electrifiedded.lasermod.tile.TileLaserDetector;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

/** Emits a redstone signal while a laser hits it. */
public class BlockLaserDetector extends BlockLaserDevice {
    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileLaserDetector();
    }

    @Override
    protected void propagate(World world, BlockPos pos) {
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileLaserDetector) ((TileLaserDetector) te).checkStillLit();
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean canProvidePower(IBlockState state) {
        return true;
    }

    @Override
    @SuppressWarnings("deprecation")
    public int getWeakPower(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
        return meta(state) != 0 ? 15 : 0;
    }
}
