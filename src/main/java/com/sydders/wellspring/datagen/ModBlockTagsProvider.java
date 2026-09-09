package com.sydders.wellspring.datagen;

import com.sydders.wellspring.Wellspring;
import com.sydders.wellspring.block.ModBlocks;
import com.sydders.wellspring.tags.ModTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;

import java.util.concurrent.CompletableFuture;

public class ModBlockTagsProvider extends BlockTagsProvider {
    public ModBlockTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider, Wellspring.MODID);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(BlockTags.MINEABLE_WITH_AXE)
                .add(ModBlocks.SIFT_PLANKS_STAIRS.get())
                .add(ModBlocks.SIFT_PLANKS_SLAB.get())
                .add(ModBlocks.SIFT_PLANKS_PRESSURE_PLATE.get())
                .add(ModBlocks.SIFT_PLANKS_BUTTON.get());

        tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(ModBlocks.SIFT_STONE.get())
                .add(ModBlocks.HARDENED_SIFT_STONE.get())
                .add(ModBlocks.SIFT_GRASS_BLOCK.get())
                .add(ModBlocks.BAZULIUM_ORE.get())
                .add(ModBlocks.HARDENED_BAZULIUM_ORE.get());

        tag(BlockTags.MINEABLE_WITH_HOE)
                .add(ModBlocks.RED_SCULK.get());

        tag(BlockTags.NEEDS_DIAMOND_TOOL)
                .add(ModBlocks.BAZULIUM_ORE.get())
                .add(ModBlocks.HARDENED_BAZULIUM_ORE.get());

        tag(BlockTags.DIRT)
                .add(ModBlocks.SIFT_GRASS_BLOCK.get());

        tag(BlockTags.ANIMALS_SPAWNABLE_ON)
                .add(ModBlocks.SIFT_GRASS_BLOCK.get());

        tag(ModTags.Blocks.BLUB_SPAWNABLE_ON)
                .add(ModBlocks.SIFT_GRASS_BLOCK.get())
                .add(ModBlocks.SIFT_STONE.get())
                .add(ModBlocks.HARDENED_SIFT_STONE.get());

        tag(BlockTags.STAIRS)
                .add(ModBlocks.SIFT_PLANKS_STAIRS.get())
                .add(ModBlocks.WITHERED_STAIRS.get());

        tag(BlockTags.SLABS)
                .add(ModBlocks.SIFT_PLANKS_SLAB.get())
                .add(ModBlocks.WITHERED_SLAB.get());

        tag(BlockTags.PRESSURE_PLATES)
                .add(ModBlocks.SIFT_PLANKS_PRESSURE_PLATE.get())
                .add(ModBlocks.WITHERED_PRESSURE_PLATE.get());

        tag(BlockTags.BUTTONS)
                .add(ModBlocks.SIFT_PLANKS_BUTTON.get())
                .add(ModBlocks.WITHERED_BUTTON.get());

        tag(BlockTags.FENCES)
                .add(ModBlocks.SIFT_PLANKS_FENCE.get())
                .add(ModBlocks.WITHERED_FENCE.get());

        tag(BlockTags.FENCE_GATES)
                .add(ModBlocks.SIFT_PLANKS_FENCE_GATE.get())
                .add(ModBlocks.WITHERED_FENCE_GATE.get());

        tag(BlockTags.DOORS)
                .add(ModBlocks.SIFT_PLANKS_DOOR.get())
                .add(ModBlocks.WITHERED_DOOR.get());

        tag(BlockTags.TRAPDOORS)
                .add(ModBlocks.SIFT_PLANKS_TRAPDOOR.get())
                .add(ModBlocks.WITHERED_TRAPDOOR.get());

        tag(BlockTags.LEAVES)
                .add(ModBlocks.SIFT_LEAVES.get())
                .add(ModBlocks.WITHERED_LEAVES.get());
        tag(BlockTags.PLANKS)
                .add(ModBlocks.SIFT_PLANKS.get())
                .add(ModBlocks.WITHERED_PLANKS.get());
        tag(BlockTags.LOGS_THAT_BURN)
                .add(ModBlocks.SIFT_LOG.get())
                .add(ModBlocks.SIFT_WOOD.get())
                .add(ModBlocks.STRIPPED_SIFT_LOG.get())
                .add(ModBlocks.STRIPPED_SIFT_WOOD.get())
                .add(ModBlocks.WITHERED_LOG.get())
                .add(ModBlocks.WITHERED_WOOD.get())
                .add(ModBlocks.STRIPPED_WITHERED_LOG.get())
                .add(ModBlocks.STRIPPED_WITHERED_WOOD.get());
        tag(BlockTags.FLOWER_POTS)
                .add(ModBlocks.POTTED_SIFT_SAPLING.get())
                .add(ModBlocks.POTTED_WITHERED_SAPLING.get());

        tag(ModTags.Blocks.NEEDS_BAZULIUM_TOOL)
                .addTag(BlockTags.NEEDS_DIAMOND_TOOL);

        tag(ModTags.Blocks.INCORRECT_FOR_BAZULIUM_TOOL)
                .addTag(BlockTags.INCORRECT_FOR_DIAMOND_TOOL)
                .remove(ModTags.Blocks.NEEDS_BAZULIUM_TOOL);
    }
}
