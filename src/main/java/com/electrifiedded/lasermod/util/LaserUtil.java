package com.electrifiedded.lasermod.util;

import com.electrifiedded.archaiclib.legacy.util.LegacyFacing;
import com.electrifiedded.lasermod.api.ILaser;
import com.electrifiedded.lasermod.api.ILaserProvider;
import com.electrifiedded.lasermod.api.ILaserReciver;
import com.electrifiedded.lasermod.api.LaserInGame;
import com.electrifiedded.lasermod.api.LaserWhitelist;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class LaserUtil {
    public static int LASER_REACH = 64;
    public static int TICK_RATE = 10;
    public static int LASER_RATE = 2;
    public static double LASER_SIZE = 0.4;
    public static final float[][] LASER_COLOUR_TABLE = {
            {1.0F, 1.0F, 1.0F}, {0.85F, 0.5F, 0.2F}, {0.7F, 0.3F, 0.85F}, {0.4F, 0.6F, 0.85F},
            {0.9F, 0.9F, 0.2F}, {0.5F, 0.8F, 0.1F}, {0.95F, 0.5F, 0.65F}, {0.3F, 0.3F, 0.3F},
            {0.6F, 0.6F, 0.6F}, {0.3F, 0.5F, 0.6F}, {0.5F, 0.25F, 0.7F}, {0.2F, 0.3F, 0.7F},
            {0.4F, 0.3F, 0.2F}, {0.0F, 1.0F, 0.0F}, {1.0F, 0.0F, 0.0F}, {0.0F, 0.0F, 0.0F}};

    public static int getOrientation(int meta) {
        int o = meta & 7;
        return o > 5 ? 0 : o;
    }

    private static boolean outside(BlockPos p) {
        return p.getY() < 0 || p.getY() >= 256 || Math.abs(p.getX()) >= 30000000 || Math.abs(p.getZ()) >= 30000000;
    }

    public static ILaserReciver getFirstReciver(ILaserProvider provider, int meta) {
        int o = getOrientation(meta);
        World w = provider.getLaserWorld();
        for (int d = 1; d <= LASER_REACH; d++) {
            BlockPos p = LegacyFacing.offset(provider.getLaserPos(), o, d);
            if (outside(p) || !w.isBlockLoaded(p)) break;
            TileEntity te = w.getTileEntity(p);
            if (te instanceof ILaserReciver) return (ILaserReciver) te;
            if (!LaserWhitelist.canLaserPassThrought(w, p)) break;
        }
        return null;
    }

    public static boolean isValidSourceOfPowerOnSide(ILaserReciver reciver, int side) {
        World w = reciver.getLaserWorld();
        for (int d = 1; d <= LASER_REACH; d++) {
            BlockPos p = LegacyFacing.offset(reciver.getLaserPos(), side, d);
            if (outside(p) || !w.isBlockLoaded(p)) break;
            if (!LaserWhitelist.canLaserPassThrought(w, p)) {
                TileEntity te = w.getTileEntity(p);
                if (te instanceof ILaserProvider)
                    return ((ILaserProvider) te).isSendingSignalFromSide(w, reciver.getLaserPos(), LegacyFacing.opposite(side));
                break;
            }
        }
        return false;
    }

    public static void performLaserAction(ILaserProvider provider, int meta) {
        LaserInGame laser = provider.getOutputLaser(meta);
        if (laser == null) return;
        BlockPos pos = provider.getLaserPos();
        AxisAlignedBB box = getLaserOutline(provider, meta, pos.getX(), pos.getY(), pos.getZ());
        World w = provider.getLaserWorld();
        List<Entity> entities = w.getEntitiesWithinAABB(Entity.class, box);
        if (entities.isEmpty()) return;
        for (ILaser l : laser.getLaserType()) {
            l.performActionOnEntitiesBoth(entities, meta);
            if (w.isRemote) l.performActionOnEntitiesClient(entities, meta);
            else l.performActionOnEntitiesServer(entities, meta);
        }
    }

    /** Beam box from the device centre to the first blocking block; (rx,ry,rz) = device corner in the target space. */
    public static AxisAlignedBB getLaserOutline(ILaserProvider provider, int meta, double rx, double ry, double rz) {
        int o = getOrientation(meta);
        double min = 0.5 - LASER_SIZE / 2.0, max = 0.5 + LASER_SIZE / 2.0;
        double extra = 0;
        World w = provider.getLaserWorld();
        for (int d = 1; d <= LASER_REACH; d++) {
            BlockPos p = LegacyFacing.offset(provider.getLaserPos(), o, d);
            if (outside(p)) break;
            if (!LaserWhitelist.canLaserPassThrought(w, p)) {
                extra += 1.0 - max + 0.01;
                break;
            }
            extra++;
        }
        double[] e = new double[6];
        e[o] = extra;
        return new AxisAlignedBB(rx + min - e[4], ry + min - e[0], rz + min - e[2],
                rx + max + e[5], ry + max + e[1], rz + max + e[3]);
    }
}
