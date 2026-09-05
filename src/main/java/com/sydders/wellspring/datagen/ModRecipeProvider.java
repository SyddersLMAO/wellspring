package com.sydders.wellspring.datagen;

import com.sydders.wellspring.block.ModBlocks;
import com.sydders.wellspring.item.ModItems;
import com.sydders.wellspring.tags.ModTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
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
            return "Wellspring Recipes";
        }
    }

    @Override
    protected void buildRecipes() {
        shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.HARDENED_SIFT_STONE.get())
                .pattern("SSS")
                .pattern("SSS")
                .pattern("SSS")
                .define('S', ModBlocks.SIFT_STONE.get())
                .unlockedBy(getHasName(ModBlocks.SIFT_STONE.get()), has(ModBlocks.SIFT_STONE))
                .group("hardened_sift_stone")
                .save(output);

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

        shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.BAZULIUM_BLOCK.get())
                .pattern("BBB")
                .pattern("BBB")
                .pattern("BBB")
                .define('B', ModItems.BAZULIUM_INGOT.get())
                .unlockedBy(getHasName(ModItems.BAZULIUM_INGOT.get()), has(ModItems.BAZULIUM_INGOT))
                .save(output);

        shaped(RecipeCategory.COMBAT, ModItems.BAZULIUM_SWORD.get())
                .pattern("B")
                .pattern("B")
                .pattern("S")
                .define('B', ModItems.BAZULIUM_INGOT.get())
                .define('S', Items.STICK)
                .unlockedBy(getHasName(ModItems.BAZULIUM_INGOT.get()), has(ModItems.BAZULIUM_INGOT))
                .unlockedBy(getHasName(Items.STICK), has(Items.STICK))
                .save(output);
        shaped(RecipeCategory.TOOLS, ModItems.BAZULIUM_PICKAXE.get())
                .pattern("BBB")
                .pattern(" S ")
                .pattern(" S ")
                .define('B', ModItems.BAZULIUM_INGOT.get())
                .define('S', Items.STICK)
                .unlockedBy(getHasName(ModItems.BAZULIUM_INGOT.get()), has(ModItems.BAZULIUM_INGOT))
                .unlockedBy(getHasName(Items.STICK), has(Items.STICK))
                .save(output);
        shaped(RecipeCategory.TOOLS, ModItems.BAZULIUM_SHOVEL.get())
                .pattern("B")
                .pattern("S")
                .pattern("S")
                .define('B', ModItems.BAZULIUM_INGOT.get())
                .define('S', Items.STICK)
                .unlockedBy(getHasName(ModItems.BAZULIUM_INGOT.get()), has(ModItems.BAZULIUM_INGOT))
                .unlockedBy(getHasName(Items.STICK), has(Items.STICK))
                .save(output);
        shaped(RecipeCategory.TOOLS, ModItems.BAZULIUM_AXE.get())
                .pattern("BB")
                .pattern("SB")
                .pattern("S ")
                .define('B', ModItems.BAZULIUM_INGOT.get())
                .define('S', Items.STICK)
                .unlockedBy(getHasName(ModItems.BAZULIUM_INGOT.get()), has(ModItems.BAZULIUM_INGOT))
                .unlockedBy(getHasName(Items.STICK), has(Items.STICK))
                .save(output);
        shaped(RecipeCategory.TOOLS, ModItems.BAZULIUM_HOE.get())
                .pattern("BB")
                .pattern("S ")
                .pattern("S ")
                .define('B', ModItems.BAZULIUM_INGOT.get())
                .define('S', Items.STICK)
                .unlockedBy(getHasName(ModItems.BAZULIUM_INGOT.get()), has(ModItems.BAZULIUM_INGOT))
                .unlockedBy(getHasName(Items.STICK), has(Items.STICK))
                .save(output);
        shaped(RecipeCategory.COMBAT, ModItems.BAZULIUM_SPEAR.get())
                .pattern("  B")
                .pattern(" S ")
                .pattern("S  ")
                .define('B', ModItems.BAZULIUM_INGOT.get())
                .define('S', Items.STICK)
                .unlockedBy(getHasName(ModItems.BAZULIUM_INGOT.get()), has(ModItems.BAZULIUM_INGOT))
                .unlockedBy(getHasName(Items.STICK), has(Items.STICK))
                .save(output);

        shaped(RecipeCategory.COMBAT, ModItems.BAZULIUM_HELMET.get())
                .pattern("BBB")
                .pattern("B B")
                .define('B', ModItems.BAZULIUM_INGOT.get())
                .unlockedBy(getHasName(ModItems.BAZULIUM_INGOT.get()), has(ModItems.BAZULIUM_INGOT))
                .unlockedBy(getHasName(Items.STICK), has(Items.STICK))
                .save(output);
        shaped(RecipeCategory.COMBAT, ModItems.BAZULIUM_CHESTPLATE.get())
                .pattern("B B")
                .pattern("BBB")
                .pattern("BBB")
                .define('B', ModItems.BAZULIUM_INGOT.get())
                .unlockedBy(getHasName(ModItems.BAZULIUM_INGOT.get()), has(ModItems.BAZULIUM_INGOT))
                .unlockedBy(getHasName(Items.STICK), has(Items.STICK))
                .save(output);
        shaped(RecipeCategory.COMBAT, ModItems.BAZULIUM_LEGGINGS.get())
                .pattern("BBB")
                .pattern("B B")
                .pattern("B B")
                .define('B', ModItems.BAZULIUM_INGOT.get())
                .unlockedBy(getHasName(ModItems.BAZULIUM_INGOT.get()), has(ModItems.BAZULIUM_INGOT))
                .unlockedBy(getHasName(Items.STICK), has(Items.STICK))
                .save(output);
        shaped(RecipeCategory.COMBAT, ModItems.BAZULIUM_BOOTS.get())
                .pattern("B B")
                .pattern("B B")
                .define('B', ModItems.BAZULIUM_INGOT.get())
                .unlockedBy(getHasName(ModItems.BAZULIUM_INGOT.get()), has(ModItems.BAZULIUM_INGOT))
                .unlockedBy(getHasName(Items.STICK), has(Items.STICK))
                .save(output);

    }
}
