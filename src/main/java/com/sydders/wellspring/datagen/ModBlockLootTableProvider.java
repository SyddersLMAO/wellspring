package com.sydders.wellspring.datagen;

import com.sydders.wellspring.block.ModBlocks;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.Set;

public class ModBlockLootTableProvider extends BlockLootSubProvider {
    public ModBlockLootTableProvider(HolderLookup.Provider registries) {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags(), registries);
    }

    @Override
    protected void generate() {
        dropSelf(ModBlocks.SIFT_LOG.get());
        dropSelf(ModBlocks.SIFT_WOOD.get());
        dropSelf(ModBlocks.STRIPPED_SIFT_LOG.get());
        dropSelf(ModBlocks.STRIPPED_SIFT_WOOD.get());
        dropSelf(ModBlocks.SIFT_PLANKS.get());
        dropSelf(ModBlocks.SIFT_SAPLING.get());

        dropSelf(ModBlocks.SIFT_PLANKS_STAIRS.get());
        add(ModBlocks.SIFT_PLANKS_SLAB.get(), this::createSlabItemTable);

        add(ModBlocks.SIFT_LEAVES.get(), block -> createLeavesDrops(block, ModBlocks.SIFT_SAPLING.get(), NORMAL_LEAVES_SAPLING_CHANCES));
        add(ModBlocks.POTTED_SIFT_SAPLING.get(), createPotFlowerItemTable((ModBlocks.SIFT_SAPLING)));
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return ModBlocks.BLOCKS.getEntries().stream().map(Holder::value)::iterator;
    }
}
