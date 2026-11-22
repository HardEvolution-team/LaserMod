package com.electrifiedded.lasermod.block;

import com.electrifiedded.lasermod.LaserMod;
import com.electrifiedded.lasermod.tile.TileReflector;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/** Redirects lasers; right-click a face with the screwdriver to open / close it. */
public class BlockReflector extends BlockLaserDevice {
    public BlockReflector() {
        setLegacyRenderType(-1);
        setLegacyFullCube(false);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileReflector();
    }

    @Override
    protected void propagate(World world, BlockPos pos) {
        TileEntity te = world.getTileEntity(pos);
        if (!(te instanceof TileReflector)) return;
        TileReflector r = (TileReflector) te;
        for (int i = 0; i < 6; i++) {
            if (r.closedSides[i] || r.lasers.isEmpty()) emit(world, r, i, false);
            else if (!r.containsInputSide(i)) emit(world, r, i, true);
        }
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hx, float hy, float hz) {
        if (player.getHeldItem(hand).getItem() != LaserMod.screwdriver) return false;
        if (world.isRemote) return true;
        TileEntity te = world.getTileEntity(pos);
        if (!(te instanceof TileReflector)) return false;
        TileReflector r = (TileReflector) te;
        int s = side.getIndex();
        r.closedSides[s] = !r.closedSides[s];
        if (r.closedSides[s]) r.removeAllLasersFromSide(s);
        r.sync();
        propagate(world, pos);
        return true;
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileReflector) for (int i = 0; i < 6; i++) emit(world, (TileReflector) te, i, false);
        super.breakBlock(world, pos, state);
    }
}
