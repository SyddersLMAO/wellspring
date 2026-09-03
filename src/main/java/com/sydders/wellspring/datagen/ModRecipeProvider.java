package com.sydders.wellspring.datagen;

import com.sydders.wellspring.block.ModBlocks;
import com.sydders.wellspring.tags.ModTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.concurrent.CompletableFuture;

public class ModRecipeProvider extends RecipeProvider {
    public ModRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
        super(registries, output);
    }

    public static class Runner extends RecipeProvider.Runner {
        public Runner(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> registries) {
            super(packOutput, registries);
        }

        @Override
        protected RecipeProvider createRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
            return new ModRecipeProvider(registries, output);
        }

        @Override
        public String getName() {
            return "TutorialMod Recipes";
        }
    }

    @Override
    protected void buildRecipes() {
        woodFromLogs(ModBlocks.SIFT_WOOD.get(), ModBlocks.SIFT_LOG);
        woodFromLogs(ModBlocks.STRIPPED_SIFT_WOOD.get(), ModBlocks.STRIPPED_SIFT_LOG);
        planksFromLog(ModBlocks.SIFT_PLANKS, ModTags.Items.SIFT_LOGS, 4);

        stairBuilder(ModBlocks.SIFT_PLANKS_STAIRS.get(), Ingredient.of(ModBlocks.SIFT_PLANKS))
                .unlockedBy(getHasName(ModBlocks.SIFT_PLANKS.get()), has(ModBlocks.SIFT_PLANKS))
                .group("wooden_stairs").save(output);
        slabBuilder(RecipeCategory.BUILDING_BLOCKS, ModBlocks.SIFT_PLANKS_SLAB.get(), Ingredient.of(ModBlocks.SIFT_PLANKS))
                .unlockedBy(getHasName(ModBlocks.SIFT_PLANKS.get()), has(ModBlocks.SIFT_PLANKS))
                .group("wooden_slab").save(output);
        pressurePlateBuilder(RecipeCategory.REDSTONE, ModBlocks.SIFT_PLANKS_PRESSURE_PLATE.get(), Ingredient.of(ModBlocks.SIFT_PLANKS))
                .unlockedBy(getHasName(ModBlocks.SIFT_PLANKS.get()), has(ModBlocks.SIFT_PLANKS))
                .group("wooden_pressure_plate").save(output);
        buttonBuilder(ModBlocks.SIFT_PLANKS_BUTTON.get(), Ingredient.of(ModBlocks.SIFT_PLANKS.get()))
                .unlockedBy(getHasName(ModBlocks.SIFT_PLANKS.get()), has(ModBlocks.SIFT_PLANKS))
                .group("wooden_button").save(output);
        fenceBuilder(ModBlocks.SIFT_PLANKS_FENCE.get(), Ingredient.of(ModBlocks.SIFT_PLANKS))
                .unlockedBy(getHasName(ModBlocks.SIFT_PLANKS.get()), has(ModBlocks.SIFT_PLANKS))
                .group("wooden_fence").save(output);
        fenceGateBuilder(ModBlocks.SIFT_PLANKS_FENCE_GATE.get(), Ingredient.of(ModBlocks.SIFT_PLANKS))
                .unlockedBy(getHasName(ModBlocks.SIFT_PLANKS.get()), has(ModBlocks.SIFT_PLANKS))
                .group("wooden_fence_gate").save(output);
        doorBuilder(ModBlocks.SIFT_PLANKS_DOOR.get(), Ingredient.of(ModBlocks.SIFT_PLANKS))
                .unlockedBy(getHasName(ModBlocks.SIFT_PLANKS.get()), has(ModBlocks.SIFT_PLANKS))
                .group("wooden_door").save(output);
        trapdoorBuilder(ModBlocks.SIFT_PLANKS_TRAPDOOR.get(), Ingredient.of(ModBlocks.SIFT_PLANKS))
                .unlockedBy(getHasName(ModBlocks.SIFT_PLANKS.get()), has(ModBlocks.SIFT_PLANKS))
                .group("wooden_trapdoor").save(output);
    }
}
