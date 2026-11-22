package com.electrifiedded.lasermod.block;

import com.electrifiedded.archaiclib.legacy.block.LegacyMetaBlock;
import com.electrifiedded.archaiclib.legacy.util.LegacyFacing;
import com.electrifiedded.lasermod.LaserMod;
import com.electrifiedded.lasermod.api.ILaser;
import com.electrifiedded.lasermod.api.LaserRegistry;
import com.electrifiedded.lasermod.tile.TileAdvancedLaser;
import com.electrifiedded.lasermod.tile.TileBasicLaser;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

/** Basic / advanced laser box: fires a beam out of its front while powered by redstone. */
public class BlockLaserBox extends BlockLaserDevice {
    private final boolean advanced;

    public BlockLaserBox(boolean advanced) {
        this.advanced = advanced;
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return advanced ? new TileAdvancedLaser() : new TileBasicLaser();
    }

    @Override
    protected void propagate(World world, BlockPos pos) {
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileBasicLaser) emit(world, (TileBasicLaser) te, ((TileBasicLaser) te).facing(), world.isBlockPowered(pos));
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        LegacyMetaBlock.setMeta(world, pos, LegacyFacing.determineOrientation(world, pos, placer), 2);
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hx, float hy, float hz) {
        if (!advanced) return false;
        ItemStack held = player.getHeldItem(hand);
        if (held.isEmpty()) return false;
        TileEntity te = world.getTileEntity(pos);
        if (!(te instanceof TileAdvancedLaser)) return false;
        TileAdvancedLaser laserTe = (TileAdvancedLaser) te;
        if (held.getItem() == LaserMod.screwdriver) {
            if (!world.isRemote) {
                player.sendMessage(new TextComponentString(TextFormatting.RED + String.format("Advanced Laser (%d, %d, %d)", pos.getX(), pos.getY(), pos.getZ())));
                if (laserTe.upgrades.isEmpty()) {
                    player.sendMessage(new TextComponentString(" Currently no upgrades attached to this laser."));
                } else {
                    for (ILaser l : laserTe.getOutputLaser(laserTe.facing()).getLaserType()) {
                        String name = LaserRegistry.getIdFromLaser(l);
                        if ("default".equals(name)) continue;
                        player.sendMessage(new TextComponentString(TextFormatting.GREEN + "  " + Character.toUpperCase(name.charAt(0)) + name.substring(1)));
                    }
                }
            }
            return true;
        }
        ILaser laser = LaserRegistry.getLaserFromItem(held);
        if (laser == null) return false;
        if (world.isBlockPowered(pos)) return true;
        for (ItemStack s : laserTe.upgrades) {
            if (LaserRegistry.getLaserFromItem(s) == laser) {
                if (!world.isRemote) player.sendMessage(new TextComponentString("This Laser already has this upgrade."));
                return true;
            }
        }
        if (!world.isRemote) {
            ItemStack one = held.copy();
            one.setCount(1);
            laserTe.upgrades.add(one);
            if (!player.capabilities.isCreativeMode) held.shrink(1);
            laserTe.sync();
        }
        return true;
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileBasicLaser) emit(world, (TileBasicLaser) te, ((TileBasicLaser) te).facing(), false);
        if (te instanceof TileAdvancedLaser) {
            for (ItemStack s : ((TileAdvancedLaser) te).upgrades)
                InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), s.copy());
            ((TileAdvancedLaser) te).upgrades.clear();
        }
        super.breakBlock(world, pos, state);
    }
}
