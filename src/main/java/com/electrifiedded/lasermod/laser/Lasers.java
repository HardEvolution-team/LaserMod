package com.electrifiedded.lasermod.laser;

import com.electrifiedded.lasermod.api.ILaser;
import com.electrifiedded.lasermod.api.LaserRegistry;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;

/** The built-in laser effects. */
public final class Lasers {
    private Lasers() {}

    public static final DamageSource LASER_DAMAGE = new DamageSource("lasermod.damage").setDamageBypassesArmor();

    public static class Basic implements ILaser {
        @Override public void performActionOnEntitiesBoth(List<Entity> e, int d) {}
        @Override public void performActionOnEntitiesClient(List<Entity> e, int d) {}
        @Override public void performActionOnEntitiesServer(List<Entity> e, int d) {}
        @Override public boolean shouldRenderLaser(EntityPlayer p, int d) { return true; }
    }

    public static class Fire extends Basic {
        @Override public void performActionOnEntitiesServer(List<Entity> list, int d) { for (Entity e : list) e.setFire(4); }
    }

    public static class Damage extends Basic {
        @Override public void performActionOnEntitiesServer(List<Entity> list, int d) { for (Entity e : list) e.attackEntityFrom(LASER_DAMAGE, 3.0F); }
    }

    public static class Invisible extends Basic {
        @Override public boolean shouldRenderLaser(EntityPlayer p, int d) { return false; }
    }

    /** Push moves entities along the beam (away from the source), pull towards it. */
    public static class Push extends Basic {
        private final boolean pull;

        public Push(boolean pull) { this.pull = pull; }

        @Override
        public void performActionOnEntitiesBoth(List<Entity> list, int direction) {
            int dir = pull ? direction ^ 1 : direction;
            for (Entity e : list) {
                double speed = 0.18, max = 0.3;
                if (e instanceof EntityItem) max *= 2.34;
                if (e instanceof EntityMinecart) speed *= 0.5;
                if (e instanceof EntityFallingBlock && dir == 1) speed = 0;
                switch (dir) {
                    case 0: if (e.motionY > -max) e.motionY -= speed; break;
                    case 1: if (e.motionY < max * 0.5) e.motionY += speed; break;
                    case 2: if (e.motionZ > -max) e.motionZ -= speed; break;
                    case 3: if (e.motionZ < max) e.motionZ += speed; break;
                    case 4: if (e.motionX > -max) e.motionX -= speed; break;
                    case 5: if (e.motionX < max) e.motionX += speed; break;
                    default: break;
                }
                e.velocityChanged = true;
            }
        }
    }

    public static void registerDefaults() {
        LaserRegistry.registerLaser("default", new Basic());
        LaserRegistry.registerLaser("fire", new Fire());
        LaserRegistry.registerLaser("invisible", new Invisible());
        LaserRegistry.registerLaser("push", new Push(false));
        LaserRegistry.registerLaser("pull", new Push(true));
        LaserRegistry.registerLaser("damage", new Damage());
    }
}
