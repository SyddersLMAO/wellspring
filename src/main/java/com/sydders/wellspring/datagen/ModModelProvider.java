package com.sydders.wellspring.datagen;

import com.sydders.wellspring.Wellspring;
import com.sydders.wellspring.block.ModBlocks;
import com.sydders.wellspring.item.ModItems;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.data.PackOutput;

public class ModModelProvider extends ModelProvider {
    public ModModelProvider(PackOutput output) {
        super(output, Wellspring.MODID);
    }

    @Override
    protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
        itemModels.generateFlatItem(ModItems.RUBY.get(), ModelTemplates.FLAT_ITEM);

        //blocks
        blockModels.woodProvider(ModBlocks.SIFT_LOG.get()).logWithHorizontal(ModBlocks.SIFT_LOG.get()).wood(ModBlocks.SIFT_WOOD.get());
        blockModels.woodProvider(ModBlocks.STRIPPED_SIFT_LOG.get()).logWithHorizontal(ModBlocks.STRIPPED_SIFT_LOG.get()).wood(ModBlocks.STRIPPED_SIFT_WOOD.get());
        blockModels.createTrivialCube(ModBlocks.SIFT_LEAVES.get());

        blockModels.createPlantWithDefaultItem(ModBlocks.SIFT_SAPLING.get(), ModBlocks.POTTED_SIFT_SAPLING.get(), BlockModelGenerators.PlantType.NOT_TINTED);

        blockModels.family(ModBlocks.SIFT_PLANKS.get())
                .stairs(ModBlocks.SIFT_PLANKS_STAIRS.get())
                .slab(ModBlocks.SIFT_PLANKS_SLAB.get());
    }
}
