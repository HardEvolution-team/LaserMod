package com.electrifiedded.lasermod.block;

import com.electrifiedded.archaiclib.legacy.block.LegacyMetaBlock;
import com.electrifiedded.archaiclib.legacy.util.LegacyFacing;
import com.electrifiedded.lasermod.api.ILaserProvider;
import com.electrifiedded.lasermod.api.ILaserReciver;
import com.electrifiedded.lasermod.api.LaserInGame;
import com.electrifiedded.lasermod.util.LaserUtil;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/** Common base: a meta block with a tile entity that re-evaluates its beams on add / neighbour change / scheduled tick. */
public abstract class BlockLaserDevice extends LegacyMetaBlock implements ITileEntityProvider {
    protected BlockLaserDevice() {
        super(Material.ROCK);
        setHardness(1.0F);
        setSoundType(SoundType.STONE);
    }

    /** server side: push / withdraw this device's beams */
    protected abstract void propagate(World world, BlockPos pos);

    @Override
    public void onBlockAdded(World world, BlockPos pos, IBlockState state) {
        super.onBlockAdded(world, pos, state);
        if (!world.isRemote) propagate(world, pos);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos from) {
        if (!world.isRemote) propagate(world, pos);
    }

    @Override
    public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
        if (!world.isRemote) propagate(world, pos);
    }

    /** Send ({@code on}) or withdraw the provider's beam on {@code side} to the first receiver in line. */
    public static void emit(World world, ILaserProvider provider, int side, boolean on) {
        ILaserReciver r = LaserUtil.getFirstReciver(provider, side);
        if (r == null) return;
        int opp = LegacyFacing.opposite(LaserUtil.getOrientation(side));
        if (!on) {
            r.removeLasersFromSide(world, provider.getLaserPos(), opp);
        } else {
            LaserInGame l = provider.getOutputLaser(side);
            if (l != null && r.canPassOnSide(world, provider.getLaserPos(), opp, l)) r.passLaser(world, provider.getLaserPos(), opp, l);
        }
    }
}
