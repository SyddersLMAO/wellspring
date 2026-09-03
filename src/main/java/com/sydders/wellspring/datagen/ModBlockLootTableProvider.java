package com.sydders.wellspring.datagen;

import com.sydders.wellspring.block.ModBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.Item;

import java.util.Set;

public class ModBlockLootTableProvider extends BlockLootSubProvider {
    protected ModBlockLootTableProvider(Set<Item> explosionResistant, FeatureFlagSet enabledFeatures, HolderLookup.Provider registries) {
        super(explosionResistant, enabledFeatures, registries);
    }

    @Override
    protected void generate() {
        dropSelf(ModBlocks.SIFT_LOG.get());
        dropSelf(ModBlocks.SIFT_WOOD.get());
        dropSelf(ModBlocks.STRIPPED_SIFT_LOG.get());
        dropSelf(ModBlocks.STRIPPED_SIFT_WOOD.get());
        dropSelf(ModBlocks.SIFT_PLANKS.get());
        add(ModBlocks.SIFT_LEAVES.get(), block -> createLeavesDrops(block, ModBlocks.SIFT_LEAVES.get(), NORMAL_LEAVES_SAPLING_CHANCES));
    }
}
