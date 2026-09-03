package com.sydders.wellspring.datagen;

import com.sydders.wellspring.Wellspring;
import com.sydders.wellspring.block.ModBlocks;
import com.sydders.wellspring.item.ModItems;
import com.sydders.wellspring.tags.ModTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
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
                .add(ModBlocks.SIFT_PLANKS.asItem());

        tag(ItemTags.LOGS_THAT_BURN)
                .add(ModBlocks.SIFT_LOG.asItem())
                .add(ModBlocks.SIFT_WOOD.asItem())
                .add(ModBlocks.STRIPPED_SIFT_LOG.asItem())
                .add(ModBlocks.STRIPPED_SIFT_WOOD.asItem());

        tag(ModTags.Items.SIFT_LOGS)
                .add(ModBlocks.SIFT_LOG.asItem())
                .add(ModBlocks.SIFT_WOOD.asItem())
                .add(ModBlocks.STRIPPED_SIFT_LOG.asItem())
                .add(ModBlocks.STRIPPED_SIFT_WOOD.asItem());
    }
}
