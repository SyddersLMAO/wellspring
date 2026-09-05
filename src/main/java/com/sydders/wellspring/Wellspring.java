package com.sydders.wellspring;

import com.sydders.wellspring.block.ModBlocks;
import com.sydders.wellspring.entity.ModEntities;
import com.sydders.wellspring.entity.ModEntityAttributes;
import com.sydders.wellspring.item.ModItems;
import com.sydders.wellspring.sound.ModSounds;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(Wellspring.MODID)
public class Wellspring {
    // Define mod id in a common place for everything to reference
    public static final String MODID = "wellspring";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    // The constructor for the mod class is the first code that is run when your mod is loaded.
    // FML will recognize some parameter types like IEventBus or ModContainer and pass them in automatically.
    public Wellspring(IEventBus modEventBus, ModContainer modContainer) {
        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        // Register Items
        ModItems.register(modEventBus);

        // Register Blocks
        ModBlocks.register(modEventBus);

        // Register Entities
        ModEntities.ENTITY_TYPES.register(modEventBus);
        modEventBus.addListener(
                ModEntityAttributes::registerAttributes
        );

        // Register Sounds
        ModSounds.register(modEventBus);

        // Register ourselves for server and other game events we are interested in.
        // Note that this is necessary if and only if we want *this* class (Wellspring) to respond directly to events.
        // Do not add this line if there are no @SubscribeEvent-annotated functions in this class, like onServerStarting() below.
        NeoForge.EVENT_BUS.register(this);

        // Register the item to a creative tab
        modEventBus.addListener(this::addCreative);

        // Register our mod's ModConfigSpec so that FML can create and load the config file for us
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(FMLCommonSetupEvent event) {

    }

    // Add the example block item to the building blocks tab
    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.INGREDIENTS) {
            event.accept(ModItems.RUBY);

            event.accept(ModItems.RAW_BAZULIUM);
            event.accept(ModItems.BAZULIUM_INGOT);
            event.accept(ModItems.BAZULIUM_NUGGET);
        }

        if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS) {
            event.accept(ModBlocks.SIFT_LOG);
            event.accept(ModBlocks.SIFT_WOOD);
            event.accept(ModBlocks.STRIPPED_SIFT_LOG);
            event.accept(ModBlocks.STRIPPED_SIFT_WOOD);
            event.accept(ModBlocks.SIFT_PLANKS);
            event.accept(ModBlocks.SIFT_PLANKS_STAIRS);
            event.accept(ModBlocks.SIFT_PLANKS_SLAB);
            event.accept(ModBlocks.SIFT_LEAVES);
            event.accept(ModBlocks.SIFT_PLANKS_FENCE);
            event.accept(ModBlocks.SIFT_PLANKS_FENCE_GATE);
            event.accept(ModBlocks.SIFT_PLANKS_DOOR);
            event.accept(ModBlocks.SIFT_PLANKS_TRAPDOOR);
            event.accept(ModBlocks.BAZULIUM_ORE);
            event.accept(ModBlocks.HARDENED_BAZULIUM_ORE);
            event.accept(ModBlocks.BAZULIUM_BLOCK);
        }

        if (event.getTabKey() == CreativeModeTabs.NATURAL_BLOCKS) {
            event.accept(ModBlocks.SIFT_SAPLING);
            event.accept(ModBlocks.BAZULIUM_ORE);
            event.accept(ModBlocks.HARDENED_BAZULIUM_ORE);
        }

        if (event.getTabKey() == CreativeModeTabs.REDSTONE_BLOCKS) {
            event.accept(ModBlocks.SIFT_PLANKS_PRESSURE_PLATE);
            event.accept(ModBlocks.SIFT_PLANKS_BUTTON);
        }

        if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
            event.accept(ModItems.WARDEN_KEY);
            event.accept(ModItems.BAZULIUM_PICKAXE);
            event.accept(ModItems.BAZULIUM_AXE);
            event.accept(ModItems.BAZULIUM_SHOVEL);
            event.accept(ModItems.BAZULIUM_HOE);
        }

        if (event.getTabKey() == CreativeModeTabs.COMBAT) {
            event.accept(ModItems.BAZULIUM_SWORD);
            event.accept(ModItems.BAZULIUM_SPEAR);
            event.accept(ModItems.BAZULIUM_AXE);
            event.accept(ModItems.BAZULIUM_HELMET);
            event.accept(ModItems.BAZULIUM_CHESTPLATE);
            event.accept(ModItems.BAZULIUM_LEGGINGS);
            event.accept(ModItems.BAZULIUM_BOOTS);
        }
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {

    }
}
