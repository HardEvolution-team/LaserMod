package com.electrifiedded.lasermod.client;

import com.electrifiedded.archaiclib.legacy.client.LegacyBeamRenderer;
import com.electrifiedded.lasermod.api.ILaserProvider;
import com.electrifiedded.lasermod.api.LaserInGame;
import com.electrifiedded.lasermod.tile.TileAdvancedLaser;
import com.electrifiedded.lasermod.tile.TileBasicLaser;
import com.electrifiedded.lasermod.tile.TileColourConverter;
import com.electrifiedded.lasermod.tile.TileLaserDevice;
import com.electrifiedded.lasermod.tile.TileReflector;
import com.electrifiedded.lasermod.util.LaserUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;

/** Draws the beams of every laser device (and the reflector frame). */
public class LaserBeamTESR extends TileEntitySpecialRenderer<TileLaserDevice> {
    public static final ResourceLocation REFLECTOR_TEXTURE = new ResourceLocation("lasermod", "textures/models/reflector.png");
    private final ModelReflector reflectorModel = new ModelReflector();

    @Override
    public void render(TileLaserDevice te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        if (te instanceof TileReflector) {
            TileReflector r = (TileReflector) te;
            GlStateManager.pushMatrix();
            bindTexture(REFLECTOR_TEXTURE);
            GlStateManager.translate(x + 0.5, y + 1.5, z + 0.5);
            GlStateManager.scale(1.0F, -1.0F, -1.0F);
            reflectorModel.render(r.closedSides);
            GlStateManager.popMatrix();
            if (r.lasers.isEmpty()) return;
            LegacyBeamRenderer.preBeam();
            for (int i = 0; i < 6; i++) if (r.isEmitting(i)) beam(r, i, r.getOutputLaser(i), x, y, z);
            LegacyBeamRenderer.postBeam();
        } else if (te instanceof TileColourConverter) {
            TileColourConverter cc = (TileColourConverter) te;
            if (cc.laser == null) return;
            single(cc, cc.facing(), cc.getOutputLaser(cc.facing()), x, y, z);
        } else if (te instanceof TileBasicLaser) {
            TileBasicLaser b = (TileBasicLaser) te;
            if (!te.getWorld().isBlockPowered(te.getPos())) return;
            LaserInGame l = b.getOutputLaser(b.facing());
            if (!(te instanceof TileAdvancedLaser)) { l.red = 255; l.green = 0; l.blue = 0; }
            single(b, b.facing(), l, x, y, z);
        }
    }

    private static void single(ILaserProvider p, int side, LaserInGame l, double x, double y, double z) {
        if (l == null) return;
        LegacyBeamRenderer.preBeam();
        beam(p, side, l, x, y, z);
        LegacyBeamRenderer.postBeam();
    }

    private static void beam(ILaserProvider p, int side, LaserInGame l, double x, double y, double z) {
        if (l == null) return;
        float a = l.shouldRenderLaser(Minecraft.getMinecraft().player);
        if (a == 0.0F) return;
        AxisAlignedBB box = LaserUtil.getLaserOutline(p, side, x, y, z);
        float r = l.red / 255.0F, g = l.green / 255.0F, b = l.blue / 255.0F;
        LegacyBeamRenderer.drawBox(box, r, g, b, a);
        LegacyBeamRenderer.drawBox(box.shrink(0.12), r, g, b, a);
    }

    @Override
    public boolean isGlobalRenderer(TileLaserDevice te) {
        return true;
    }
}
