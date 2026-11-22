package com.electrifiedded.lasermod.api;

import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

/** A laser effect. Directions use the 1.7 side numbering (= EnumFacing index). */
public interface ILaser {
    void performActionOnEntitiesBoth(List<Entity> entities, int direction);

    void performActionOnEntitiesClient(List<Entity> entities, int direction);

    void performActionOnEntitiesServer(List<Entity> entities, int direction);

    boolean shouldRenderLaser(EntityPlayer player, int direction);
}
