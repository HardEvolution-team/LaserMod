package com.electrifiedded.lasermod.block;

import com.electrifiedded.archaiclib.legacy.block.LegacyMetaBlock;
import com.electrifiedded.archaiclib.legacy.util.LegacyFacing;
import com.electrifiedded.lasermod.tile.TileColourConverter;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

/** Recolours a laser entering at its back; right-click with dye to set the colour. */
public class BlockColourConverter extends BlockLaserDevice {
    /** render-only: dye colour of the tile entity (front texture) */
    public static final PropertyInteger COLOUR = PropertyInteger.create("colour", 0, 15);

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, META, COLOUR);
    }

    @Override
    @SuppressWarnings("deprecation")
    public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
        TileEntity te = world.getTileEntity(pos);
        return state.withProperty(COLOUR, te instanceof TileColourConverter ? ((TileColourConverter) te).colour : 14);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileColourConverter();
    }

    @Override
    protected void propagate(World world, BlockPos pos) {
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileColourConverter) {
            TileColourConverter cc = (TileColourConverter) te;
            emit(world, cc, cc.facing(), cc.laser != null);
        }
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        LegacyMetaBlock.setMeta(world, pos, LegacyFacing.opposite(LegacyFacing.determineOrientation(world, pos, placer)), 2);
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hx, float hy, float hz) {
        ItemStack held = player.getHeldItem(hand);
        if (held.getItem() != Items.DYE) return false;
        if (world.isRemote) return true;
        TileEntity te = world.getTileEntity(pos);
        if (!(te instanceof TileColourConverter)) return false;
        TileColourConverter cc = (TileColourConverter) te;
        int colour = Math.max(0, Math.min(15, 15 - held.getMetadata()));
        if (colour != cc.colour) {
            cc.colour = colour;
            if (!player.capabilities.isCreativeMode) held.shrink(1);
            cc.sync();
            world.scheduleUpdate(pos, this, 4);
        }
        return true;
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileColourConverter) emit(world, (TileColourConverter) te, ((TileColourConverter) te).facing(), false);
        super.breakBlock(world, pos, state);
    }
}
