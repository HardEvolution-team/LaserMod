package com.electrifiedded.lasermod.tile;

import com.electrifiedded.archaiclib.legacy.block.LegacyMetaBlock;
import com.electrifiedded.lasermod.api.ILaserReciver;
import com.electrifiedded.lasermod.api.LaserInGame;
import com.electrifiedded.lasermod.util.LaserUtil;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/** meta 1 = hit by a laser (emits redstone), 0 = off */
public class TileLaserDetector extends TileLaserDevice implements ILaserReciver {
    @Override
    public void updateEntity() {
        lagReduce++;
        if (!world.isRemote && lagReduce % LaserUtil.TICK_RATE == 0) checkStillLit();
    }

    public void checkStillLit() {
        if (getBlockMetadata() != 1) return;
        for (int i = 0; i < 6; i++) if (LaserUtil.isValidSourceOfPowerOnSide(this, i)) return;
        LegacyMetaBlock.setMeta(world, getPos(), 0, 3);
    }

    @Override
    public boolean canPassOnSide(World w, BlockPos origin, int side, LaserInGame laser) {
        return getBlockMetadata() != 1;
    }

    @Override
    public void passLaser(World w, BlockPos origin, int side, LaserInGame laser) {
        if (!w.isRemote) LegacyMetaBlock.setMeta(w, getPos(), 1, 3);
    }

    @Override
    public void removeLasersFromSide(World w, BlockPos origin, int side) {
        scheduleSelf(4);
    }
}
