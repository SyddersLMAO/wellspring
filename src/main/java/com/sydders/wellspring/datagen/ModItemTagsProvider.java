package com.sydders.wellspring.datagen;

import com.sydders.wellspring.Wellspring;
import com.sydders.wellspring.block.ModBlocks;
import com.sydders.wellspring.item.ModItems;
import com.sydders.wellspring.tags.ModTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.ItemTags;
import net.neoforged.neoforge.common.data.ItemTagsProvider;

import java.util.concurrent.CompletableFuture;

public class ModItemTagsProvider extends ItemTagsProvider {
    public ModItemTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider, Wellspring.MODID);
    }

    @Override
    protected void addTags(HolderLookup.Provider registries) {
        tag(ItemTags.PLANKS)
                .add(ModBlocks.SIFT_PLANKS.asItem())
                .add(ModBlocks.WITHERED_PLANKS.asItem());

        tag(ItemTags.WOODEN_SLABS)
                .add(ModBlocks.SIFT_PLANKS_SLAB.asItem())
                .add(ModBlocks.WITHERED_SLAB.asItem());

        tag(ItemTags.WOODEN_BUTTONS)
                .add(ModBlocks.SIFT_PLANKS_BUTTON.asItem())
                .add(ModBlocks.WITHERED_BUTTON.asItem());

        tag(ItemTags.WOODEN_DOORS)
                .add(ModBlocks.SIFT_PLANKS_DOOR.asItem())
                .add(ModBlocks.WITHERED_DOOR.asItem());

        tag(ItemTags.WOODEN_FENCES)
                .add(ModBlocks.SIFT_PLANKS_FENCE.asItem())
                .add(ModBlocks.WITHERED_FENCE.asItem());

        tag(ItemTags.WOODEN_PRESSURE_PLATES)
                .add(ModBlocks.SIFT_PLANKS_PRESSURE_PLATE.asItem())
                .add(ModBlocks.WITHERED_PRESSURE_PLATE.asItem());

        tag(ItemTags.WOODEN_STAIRS)
                .add(ModBlocks.SIFT_PLANKS_STAIRS.asItem())
                .add(ModBlocks.WITHERED_STAIRS.asItem());

        tag(ItemTags.WOODEN_TRAPDOORS)
                .add(ModBlocks.SIFT_PLANKS_TRAPDOOR.asItem())
                .add(ModBlocks.WITHERED_TRAPDOOR.asItem());

        tag(ItemTags.STONE_TOOL_MATERIALS)
                .add(ModBlocks.SIFT_STONE.asItem())
                .add(ModBlocks.HARDENED_SIFT_STONE.asItem());

        tag(ItemTags.STONE_CRAFTING_MATERIALS)
                .add(ModBlocks.SIFT_STONE.asItem())
                .add(ModBlocks.HARDENED_SIFT_STONE.asItem());

        tag(ItemTags.LOGS_THAT_BURN)
                .add(ModBlocks.SIFT_LOG.asItem())
                .add(ModBlocks.SIFT_WOOD.asItem())
                .add(ModBlocks.STRIPPED_SIFT_LOG.asItem())
                .add(ModBlocks.STRIPPED_SIFT_WOOD.asItem())
                .add(ModBlocks.WITHERED_LOG.asItem())
                .add(ModBlocks.WITHERED_WOOD.asItem())
                .add(ModBlocks.STRIPPED_WITHERED_LOG.asItem())
                .add(ModBlocks.STRIPPED_WITHERED_WOOD.asItem());

        tag(ModTags.Items.SIFT_LOGS)
                .add(ModBlocks.SIFT_LOG.asItem())
                .add(ModBlocks.SIFT_WOOD.asItem())
                .add(ModBlocks.STRIPPED_SIFT_LOG.asItem())
                .add(ModBlocks.STRIPPED_SIFT_WOOD.asItem());

        tag(ModTags.Items.WITHERED_LOGS)
                .add(ModBlocks.WITHERED_LOG.asItem())
                .add(ModBlocks.WITHERED_WOOD.asItem())
                .add(ModBlocks.STRIPPED_WITHERED_LOG.asItem())
                .add(ModBlocks.STRIPPED_WITHERED_WOOD.asItem());

        tag(ModTags.Items.BAZULIUM_REPAIRABLE)
                .add(ModItems.BAZULIUM_INGOT.get());

        tag(ItemTags.SWORDS).add(ModItems.BAZULIUM_SWORD.get());
        tag(ItemTags.PICKAXES).add(ModItems.BAZULIUM_PICKAXE.get());
        tag(ItemTags.SHOVELS).add(ModItems.BAZULIUM_SHOVEL.get());
        tag(ItemTags.AXES).add(ModItems.BAZULIUM_AXE.get());
        tag(ItemTags.HOES).add(ModItems.BAZULIUM_HOE.get());
        tag(ItemTags.SPEARS).add(ModItems.BAZULIUM_SPEAR.get());

        tag(ItemTags.HEAD_ARMOR).add(ModItems.BAZULIUM_HELMET.get());
        tag(ItemTags.CHEST_ARMOR).add(ModItems.BAZULIUM_CHESTPLATE.get());
        tag(ItemTags.LEG_ARMOR).add(ModItems.BAZULIUM_LEGGINGS.get());
        tag(ItemTags.FOOT_ARMOR).add(ModItems.BAZULIUM_BOOTS.get());
    }
}
