package com.electrifiedded.lasermod.tile;

import com.electrifiedded.archaiclib.legacy.util.LegacyFacing;
import com.electrifiedded.lasermod.api.ILaser;
import com.electrifiedded.lasermod.api.ILaserProvider;
import com.electrifiedded.lasermod.api.ILaserReciver;
import com.electrifiedded.lasermod.api.LaserInGame;
import com.electrifiedded.lasermod.util.LaserUtil;
import java.util.ArrayList;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/** Takes lasers in on any open side and re-emits the mix on every other open side. */
public class TileReflector extends TileLaserDevice implements ILaserProvider, ILaserReciver {
    public final boolean[] closedSides = {true, true, true, true, true, true};
    public final ArrayList<LaserInGame> lasers = new ArrayList<>();

    @Override
    public void updateEntity() {
        lagReduce++;
        if (!world.isRemote && lagReduce % LaserUtil.TICK_RATE != 0) {
            boolean changed = false;
            for (int i = 0; i < 6; i++)
                if (!LaserUtil.isValidSourceOfPowerOnSide(this, i) && removeAllLasersFromSide(i)) changed = true;
            if (changed) sync();
            scheduleSelf(1);
        }
        if (lagReduce % LaserUtil.LASER_RATE == 0 && !lasers.isEmpty()) {
            for (int i = 0; i < 6; i++)
                if (isEmitting(i)) LaserUtil.performLaserAction(this, i);
        }
    }

    public boolean isEmitting(int side) {
        return !closedSides[side] && !containsInputSide(side) && !lasers.isEmpty();
    }

    @Override
    public void readLegacyNBT(NBTTagCompound tag) {
        int[] close = tag.getIntArray("closeSides");
        for (int i = 0; i < close.length && i < 6; i++) closedSides[i] = close[i] == 1;
        lasers.clear();
        int n = tag.getInteger("laserCount");
        for (int i = 0; i < n; i++) lasers.add(new LaserInGame(tag.getCompoundTag("laser" + i)));
    }

    @Override
    public void writeLegacyNBT(NBTTagCompound tag) {
        int[] close = new int[6];
        for (int i = 0; i < 6; i++) close[i] = closedSides[i] ? 1 : 0;
        tag.setIntArray("closeSides", close);
        tag.setInteger("laserCount", lasers.size());
        for (int i = 0; i < lasers.size(); i++) tag.setTag("laser" + i, lasers.get(i).writeToNBT(new NBTTagCompound()));
    }

    public void addLaser(LaserInGame laser, int side) {
        int i = getIndexOfLaserSide(side);
        if (i == -1) lasers.add(laser);
        else lasers.set(i, laser);
    }

    public boolean containsInputSide(int side) {
        return getIndexOfLaserSide(side) != -1;
    }

    public int getIndexOfLaserSide(int side) {
        for (int i = 0; i < lasers.size(); i++) if (lasers.get(i).getSide() == side) return i;
        return -1;
    }

    public LaserInGame getLaserFromSide(int side) {
        int i = getIndexOfLaserSide(side);
        return i == -1 ? null : lasers.get(i);
    }

    public boolean removeAllLasersFromSide(int side) {
        return lasers.removeIf(l -> l.getSide() == side);
    }

    @Override
    public LaserInGame getOutputLaser(int side) {
        if (lasers.isEmpty()) return null;
        ArrayList<ILaser> types = new ArrayList<>();
        for (LaserInGame lig : lasers)
            for (ILaser l : lig.getLaserType()) if (!types.contains(l)) types.add(l);
        LaserInGame out = new LaserInGame(types);
        int r = lasers.get(0).red, g = lasers.get(0).green, b = lasers.get(0).blue;
        double power = lasers.get(0).getStrength();
        for (int i = 1; i < lasers.size(); i++) {
            LaserInGame l = lasers.get(i);
            r = r * l.red / 255;
            g = g * l.green / 255;
            b = b * l.blue / 255;
            power += l.getStrength();
        }
        out.red = r;
        out.green = g;
        out.blue = b;
        out.setSide(LegacyFacing.opposite(side));
        out.setStrength(power);
        return out;
    }

    @Override
    public boolean canPassOnSide(World w, BlockPos origin, int side, LaserInGame laser) {
        return !closedSides[side] && (!containsInputSide(side) || !laser.equals(getLaserFromSide(side)));
    }

    @Override
    public void passLaser(World w, BlockPos origin, int side, LaserInGame laser) {
        addLaser(laser, side);
        sync();
        scheduleSelf(4);
    }

    @Override
    public void removeLasersFromSide(World w, BlockPos origin, int side) {
        if (removeAllLasersFromSide(side)) {
            sync();
            scheduleSelf(4);
        }
    }

    @Override
    public boolean isSendingSignalFromSide(World w, BlockPos asker, int side) {
        return !closedSides[side] && !lasers.isEmpty();
    }
}
