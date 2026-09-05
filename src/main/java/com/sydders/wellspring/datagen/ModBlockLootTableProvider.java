package com.sydders.wellspring.datagen;

import com.sydders.wellspring.block.ModBlocks;
import com.sydders.wellspring.item.ModItems;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

import java.util.Set;

public class ModBlockLootTableProvider extends BlockLootSubProvider {
    public ModBlockLootTableProvider(HolderLookup.Provider registries) {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags(), registries);
    }

    @Override
    protected void generate() {
        dropSelf(ModBlocks.RED_SCULK.get());
        dropSelf(ModBlocks.SIFT_STONE.get());
        dropSelf(ModBlocks.HARDENED_SIFT_STONE.get());

        add(ModBlocks.SIFT_GRASS_BLOCK.get(),
                createSilkTouchDispatchTable(
                        ModBlocks.SIFT_GRASS_BLOCK.get(),
                        LootItem.lootTableItem(ModBlocks.SIFT_STONE.get())
                )
        );

        dropSelf(ModBlocks.SIFT_LOG.get());
        dropSelf(ModBlocks.SIFT_WOOD.get());
        dropSelf(ModBlocks.STRIPPED_SIFT_LOG.get());
        dropSelf(ModBlocks.STRIPPED_SIFT_WOOD.get());
        dropSelf(ModBlocks.SIFT_PLANKS.get());
        dropSelf(ModBlocks.SIFT_SAPLING.get());

        dropSelf(ModBlocks.SIFT_PLANKS_STAIRS.get());
        add(ModBlocks.SIFT_PLANKS_SLAB.get(), this::createSlabItemTable);
        dropSelf(ModBlocks.SIFT_PLANKS_PRESSURE_PLATE.get());
        dropSelf(ModBlocks.SIFT_PLANKS_BUTTON.get());
        dropSelf(ModBlocks.SIFT_PLANKS_FENCE.get());
        dropSelf(ModBlocks.SIFT_PLANKS_FENCE_GATE.get());
        dropSelf(ModBlocks.SIFT_PLANKS_TRAPDOOR.get());

        add(ModBlocks.SIFT_PLANKS_DOOR.get(), this::createDoorTable);

        add(ModBlocks.SIFT_LEAVES.get(), block -> createLeavesDrops(block, ModBlocks.SIFT_SAPLING.get(), NORMAL_LEAVES_SAPLING_CHANCES));
        add(ModBlocks.POTTED_SIFT_SAPLING.get(), createPotFlowerItemTable((ModBlocks.SIFT_SAPLING)));

        add(ModBlocks.BAZULIUM_ORE.get(),
                createOreDrop(ModBlocks.BAZULIUM_ORE.get(), ModItems.RAW_BAZULIUM.get()));
        add(ModBlocks.HARDENED_BAZULIUM_ORE.get(),
                createOreDrop(ModBlocks.HARDENED_BAZULIUM_ORE.get(), ModItems.RAW_BAZULIUM.get()));
        dropSelf(ModBlocks.BAZULIUM_BLOCK.get());
    }

    protected LootTable.Builder createOreDrops(Block block, Item item, float minDrops, float maxDrops) {
        HolderLookup.RegistryLookup<Enchantment> enchantments = this.registries.lookupOrThrow(Registries.ENCHANTMENT);
        return this.createSilkTouchDispatchTable(block, this.applyExplosionDecay(block,
                LootItem.lootTableItem(item)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(minDrops, maxDrops)))
                        .apply(ApplyBonusCount.addOreBonusCount(enchantments.getOrThrow(Enchantments.FORTUNE)))));
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return ModBlocks.BLOCKS.getEntries().stream().map(Holder::value)::iterator;
    }
}
