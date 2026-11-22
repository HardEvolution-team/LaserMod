package com.electrifiedded.lasermod.api;

import java.util.HashSet;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/** Blocks a laser beam passes through. */
public class LaserWhitelist {
    public static final int ALL_METADATA = -1;
    private static final Set<String> WHITELIST = new HashSet<>();

    public static void addToWhiteList(Block block) { addToWhiteList(block, ALL_METADATA); }

    public static void addToWhiteList(Block block, int meta) { addToWhiteList(String.valueOf(block.getRegistryName()), meta); }

    public static void addToWhiteList(String blockName, int meta) {
        if (meta == ALL_METADATA) for (int i = 0; i < 16; i++) WHITELIST.add(blockName + "@" + i);
        else WHITELIST.add(blockName + "@" + meta);
    }

    public static boolean canLaserPassThrought(World world, BlockPos pos) {
        return world.isBlockLoaded(pos) && canLaserPassThrought(world.getBlockState(pos));
    }

    public static boolean canLaserPassThrought(IBlockState state) {
        Block b = state.getBlock();
        return WHITELIST.contains(b.getRegistryName() + "@" + b.getMetaFromState(state));
    }

    static {
        addToWhiteList(Blocks.AIR);
        addToWhiteList(Blocks.ICE);
        addToWhiteList(Blocks.TALLGRASS);
        addToWhiteList(Blocks.DOUBLE_PLANT);
        addToWhiteList(Blocks.LEAVES);
        addToWhiteList(Blocks.LEVER);
        addToWhiteList(Blocks.TORCH);
        addToWhiteList(Blocks.REDSTONE_TORCH);
        addToWhiteList(Blocks.UNLIT_REDSTONE_TORCH);
        addToWhiteList(Blocks.REDSTONE_WIRE);
        addToWhiteList(Blocks.GLASS);
        addToWhiteList(Blocks.GLASS_PANE);
    }
}
