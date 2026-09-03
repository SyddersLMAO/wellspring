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
                .add(ModBlocks.SIFT_PLANKS_SLAB.get());

        tag(BlockTags.LEAVES)
                .add(ModBlocks.SIFT_LEAVES.get());
        tag(BlockTags.PLANKS)
                .add(ModBlocks.SIFT_PLANKS.get());
        tag(BlockTags.LOGS_THAT_BURN)
                .add(ModBlocks.SIFT_LOG.get())
                .add(ModBlocks.SIFT_WOOD.get())
                .add(ModBlocks.STRIPPED_SIFT_LOG.get())
                .add(ModBlocks.STRIPPED_SIFT_WOOD.get());
        tag(BlockTags.FLOWER_POTS)
                .add(ModBlocks.POTTED_SIFT_SAPLING.get());
    }
}
