package com.electrifiedded.lasermod.api;

import com.electrifiedded.lasermod.LaserMod;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraftforge.common.util.Constants;

/** A laser beam travelling between devices: strength, side it enters from, colour and effects. */
public class LaserInGame {
    private double strength = 100.0;
    private final ArrayList<ILaser> laserType = new ArrayList<>();
    private int side = -1;
    public int red = 255, green = 0, blue = 0;

    public LaserInGame() {}

    public LaserInGame(NBTTagCompound tag) { readFromNBT(tag); }

    public LaserInGame(ILaser laser) { addLaserType(laser); }

    public LaserInGame(List<ILaser> lasers) { laserType.addAll(lasers); }

    public LaserInGame setStrength(double s) { strength = Math.max(0, s); return this; }

    public LaserInGame setSide(int s) { side = s; return this; }

    public LaserInGame setLaserType(ILaser laser) { laserType.clear(); return addLaserType(laser); }

    public LaserInGame addLaserType(ILaser laser) { if (laser != null) laserType.add(laser); return this; }

    public LaserInGame setLaserType(String id) { return setLaserType(LaserRegistry.getLaserFromId(id)); }

    public LaserInGame addLaserType(String id) { return addLaserType(LaserRegistry.getLaserFromId(id)); }

    public double getStrength() { return strength; }

    public ArrayList<ILaser> getLaserType() { return laserType; }

    public int getSide() { return side; }

    public int laserCount() { return laserType.size(); }

    /** @return render alpha: 0.4 visible, 0.1 invisible laser seen through the goggles, 0 hidden */
    public float shouldRenderLaser(EntityPlayer player) {
        for (ILaser laser : laserType) {
            if (!laser.shouldRenderLaser(player, side)) {
                return player != null && player.getItemStackFromSlot(EntityEquipmentSlot.HEAD).getItem() == LaserMod.laserSeekingGoggles ? 0.1F : 0.0F;
            }
        }
        return 0.4F;
    }

    public LaserInGame readFromNBT(NBTTagCompound tag) {
        strength = tag.getDouble("strength");
        side = tag.getInteger("side");
        red = tag.getInteger("red");
        green = tag.getInteger("green");
        blue = tag.getInteger("blue");
        laserType.clear();
        NBTTagList list = tag.getTagList("laserTypes", Constants.NBT.TAG_STRING);
        for (int i = 0; i < list.tagCount(); i++) addLaserType(list.getStringTagAt(i));
        return this;
    }

    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        tag.setDouble("strength", strength);
        tag.setInteger("side", side);
        tag.setInteger("red", red);
        tag.setInteger("green", green);
        tag.setInteger("blue", blue);
        NBTTagList list = new NBTTagList();
        for (ILaser laser : laserType) list.appendTag(new NBTTagString(LaserRegistry.getIdFromLaser(laser)));
        tag.setTag("laserTypes", list);
        return tag;
    }

    public LaserInGame copy() {
        LaserInGame l = new LaserInGame(laserType);
        l.side = side;
        l.strength = strength;
        l.red = red;
        l.green = green;
        l.blue = blue;
        return l;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof LaserInGame)) return false;
        LaserInGame l = (LaserInGame) obj;
        return red == l.red && green == l.green && blue == l.blue;
    }

    @Override
    public int hashCode() { return (red << 16) | (green << 8) | blue; }
}
