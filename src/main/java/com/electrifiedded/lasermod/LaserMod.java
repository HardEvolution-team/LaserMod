package com.electrifiedded.lasermod;

import com.electrifiedded.archaiclib.legacy.registry.LegacyGameRegistry;
import com.electrifiedded.archaiclib.original.OriginalModAssets;
import com.electrifiedded.lasermod.api.LaserRegistry;
import com.electrifiedded.lasermod.block.BlockColourConverter;
import com.electrifiedded.lasermod.block.BlockLaserBox;
import com.electrifiedded.lasermod.block.BlockLaserDetector;
import com.electrifiedded.lasermod.block.BlockReflector;
import com.electrifiedded.lasermod.item.ItemLaserSeekingGoggles;
import com.electrifiedded.lasermod.item.ItemUpgrades;
import com.electrifiedded.lasermod.laser.Lasers;
import com.electrifiedded.lasermod.tile.*;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

@Mod(modid = Reference.MOD_ID, name = Reference.MOD_NAME, version = Reference.VERSION, dependencies = "required-after:archaiclib@[1.28.0,)")
public class LaserMod {
    public static final String ORIGINAL_NAME = "laser_mod_1.7.10 - Laser Mod v1.1.0a for 1.7.10 (by ProPercivalalb)";
    public static final String ORIGINAL_URL = "https://www.curseforge.com/minecraft/mc-mods (search: Laser Mod ProPercivalalb)";

    @Mod.Instance(Reference.MOD_ID)
    public static LaserMod instance;

    @SidedProxy(clientSide = "com.electrifiedded.lasermod.client.ClientProxy", serverSide = "com.electrifiedded.lasermod.CommonProxy")
    public static CommonProxy proxy;

    public static Block basicLaser, advancedLaser, laserDetector, reflector, colourConverter;
    public static Item laserCrystal, laserSeekingGoggles, screwdriver, upgrades;

    public static final CreativeTabs TAB = new CreativeTabs("tabLaser") {
        @Override
        public ItemStack createIcon() {
            return new ItemStack(screwdriver);
        }
    };

    @EventHandler
    public void preInit(FMLPreInitializationEvent e) {
        OriginalModAssets.require(Reference.MOD_ID, Reference.MOD_ID, ORIGINAL_NAME, ORIGINAL_URL, Reference.MOD_ID);

        basicLaser = block(new BlockLaserBox(false).setTranslationKey("lasermod.basicLaser"), "basiclaser");
        advancedLaser = block(new BlockLaserBox(true).setTranslationKey("lasermod.advancedLaser"), "advancedlaser");
        laserDetector = block(new BlockLaserDetector().setTranslationKey("lasermod.detector"), "detector");
        reflector = block(new BlockReflector().setTranslationKey("lasermod.reflector"), "reflector");
        colourConverter = block(new BlockColourConverter().setTranslationKey("lasermod.colorconverter"), "colorconverter");
        tile(TileBasicLaser.class, "basiclaser");
        tile(TileAdvancedLaser.class, "advancedlaser");
        tile(TileLaserDetector.class, "detector");
        tile(TileReflector.class, "reflector");
        tile(TileColourConverter.class, "colourconverter");

        laserCrystal = item(new Item().setTranslationKey("lasermod.laserCrystal"), "lasercrystal");
        laserSeekingGoggles = item(new ItemLaserSeekingGoggles().setTranslationKey("lasermod.laserSeekingGoogles"), "laserseekinggoogles");
        screwdriver = item(new Item().setMaxStackSize(1).setTranslationKey("lasermod.screwdriver"), "screwdriver");
        upgrades = LegacyGameRegistry.registerItem(new ItemUpgrades().setTranslationKey("lasermod.upgrade").setCreativeTab(TAB), "upgrades");
        for (int i = 0; i < ItemUpgrades.NAMES.length; i++)
            LegacyGameRegistry.registerItemModel(upgrades, i, Reference.MOD_ID + ":upgrade_" + ItemUpgrades.NAMES[i], "inventory");

        Lasers.registerDefaults();
        proxy.preInit();
    }

    @EventHandler
    public void init(FMLInitializationEvent e) {
        LaserRegistry.registerItemToLaser(upgrades, 0, LaserRegistry.getLaserFromId("fire"));
        LaserRegistry.registerItemToLaser(upgrades, 3, LaserRegistry.getLaserFromId("invisible"));
        LaserRegistry.registerItemToLaser(upgrades, 5, LaserRegistry.getLaserFromId("push"));
        LaserRegistry.registerItemToLaser(upgrades, 6, LaserRegistry.getLaserFromId("pull"));
        LaserRegistry.registerItemToLaser(upgrades, 7, LaserRegistry.getLaserFromId("damage"));
    }

    private static Block block(Block b, String name) {
        b.setCreativeTab(TAB);
        LegacyGameRegistry.registerBlock(b, name);
        LegacyGameRegistry.registerItemModel(b, 0, Reference.MOD_ID + ":" + name, "inventory");
        return b;
    }

    private static Item item(Item i, String name) {
        i.setCreativeTab(TAB);
        LegacyGameRegistry.registerItem(i, name);
        LegacyGameRegistry.registerItemModel(i, 0, Reference.MOD_ID + ":" + name, "inventory");
        return i;
    }

    private static void tile(Class<? extends net.minecraft.tileentity.TileEntity> c, String name) {
        GameRegistry.registerTileEntity(c, new ResourceLocation(Reference.MOD_ID, name));
    }
}
