package com.sydders.wellspring.datagen;

import com.sydders.wellspring.block.ModBlocks;
import com.sydders.wellspring.tags.ModTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;

public class ModRecipeProvider extends RecipeProvider {
    protected ModRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
        super(registries, output);
    }

    @Override
    protected void buildRecipes() {
        woodFromLogs(ModBlocks.SIFT_WOOD.get(), ModBlocks.SIFT_LOG);
        woodFromLogs(ModBlocks.STRIPPED_SIFT_WOOD.get(), ModBlocks.STRIPPED_SIFT_LOG);
        planksFromLog(ModBlocks.SIFT_PLANKS, ModTags.Items.SIFT_LOGS, 4);
    }
}
