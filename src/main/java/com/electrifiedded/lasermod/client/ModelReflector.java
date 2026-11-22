package com.electrifiedded.lasermod.client;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;

/** Reflector frame: four corner columns, eight edge bits and six face blockers (shown while the side is closed). */
public class ModelReflector extends ModelBase {
    private final ModelRenderer[] frame;
    /** indexed by side: down, up, north, south, west, east */
    private final ModelRenderer[] blockers;

    public ModelReflector() {
        textureWidth = 128;
        textureHeight = 128;
        frame = new ModelRenderer[]{
                box(75, 42, 0, 0, 0, 6, 16, 6, -8, 8, -8),
                box(25, 42, 0, 0, 0, 6, 16, 6, 2, 8, 2),
                box(0, 42, 0, 0, 0, 6, 16, 6, 2, 8, -8),
                box(50, 42, 0, 0, 0, 6, 16, 6, -8, 8, 2),
                box(21, 69, 0, 0, 0, 4, 6, 6, -2, 8, 2),
                box(42, 69, 0, 0, 0, 4, 6, 6, -2, 8, -8),
                box(0, 69, -1, 0, -2, 6, 6, 4, 3, 8, 0),
                box(63, 69, 0, 0, 0, 6, 6, 4, -8, 8, -2),
                box(105, 69, 0, 0, 0, 4, 6, 6, -2, 18, 2),
                box(84, 69, 0, 0, 0, 6, 6, 4, -8, 18, -2),
                box(21, 82, 0, 0, 0, 4, 6, 6, -2, 18, -8),
                box(0, 82, 0, 0, 0, 6, 6, 4, 2, 18, -2)};
        blockers = new ModelRenderer[]{
                box(0, 0, 0, 0, 0, 4, 1, 4, -2, 23, -2),
                box(0, 6, 0, 0, 0, 4, 1, 4, -2, 8, -2),
                box(17, 0, 0, 0, 0, 4, 4, 1, -2, 14, 7),
                box(17, 6, 0, 0, 0, 4, 4, 1, -2, 14, -8),
                box(28, 0, 0, 0, 0, 1, 4, 4, -8, 14, -2),
                box(28, 9, 0, 0, 0, 1, 4, 4, 7, 14, -2)};
    }

    private ModelRenderer box(int u, int v, float ox, float oy, float oz, int w, int h, int d, float px, float py, float pz) {
        ModelRenderer r = new ModelRenderer(this, u, v);
        r.addBox(ox, oy, oz, w, h, d);
        r.setRotationPoint(px, py, pz);
        r.setTextureSize(128, 128);
        r.mirror = true;
        return r;
    }

    public void render(boolean[] closedSides) {
        for (ModelRenderer r : frame) r.render(0.0625F);
        for (int i = 0; i < 6; i++) if (closedSides == null || closedSides[i]) blockers[i].render(0.0625F);
    }
}
