package com.electrifiedded.lasermod.client;

import com.electrifiedded.lasermod.CommonProxy;
import com.electrifiedded.lasermod.LaserMod;
import com.electrifiedded.lasermod.tile.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.client.registry.ClientRegistry;

public class ClientProxy extends CommonProxy {
    @Override
    public void preInit() {
        LaserBeamTESR tesr = new LaserBeamTESR();
        ClientRegistry.bindTileEntitySpecialRenderer(TileBasicLaser.class, tesr);
        ClientRegistry.bindTileEntitySpecialRenderer(TileAdvancedLaser.class, tesr);
        ClientRegistry.bindTileEntitySpecialRenderer(TileColourConverter.class, tesr);
        ClientRegistry.bindTileEntitySpecialRenderer(TileReflector.class, tesr);

        final ModelReflector model = new ModelReflector();
        Item.getItemFromBlock(LaserMod.reflector).setTileEntityItemStackRenderer(new TileEntityItemStackRenderer() {
            @Override
            public void renderByItem(ItemStack stack) {
                Minecraft.getMinecraft().getTextureManager().bindTexture(LaserBeamTESR.REFLECTOR_TEXTURE);
                GlStateManager.pushMatrix();
                GlStateManager.translate(0.5, 1.5, 0.5);
                GlStateManager.scale(1.0F, -1.0F, -1.0F);
                model.render(null);
                GlStateManager.popMatrix();
            }
        });
    }
}
