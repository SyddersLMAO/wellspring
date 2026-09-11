package com.sydders.wellspring.worldgen;

import com.sydders.wellspring.Wellspring;
import com.sydders.wellspring.block.ModBlocks;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.placement.OrePlacements;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.data.worldgen.placement.VegetationPlacements;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.*;

import java.util.List;

public class ModPlacedFeatures {
    public static final ResourceKey<PlacedFeature> SIFT_PLACED_KEY = registerKey("sift_placed");
    public static final ResourceKey<PlacedFeature> WITHERED_PLACED_KEY = registerKey("withered_placed");
    public static final ResourceKey<PlacedFeature> SIFT_SPARSE_PLACED_KEY = registerKey("sift_sparse_placed");

    public static final ResourceKey<PlacedFeature> BAZULIUM_ORE_PLACED_KEY = registerKey("bazulium_ore_placed");

    public static final ResourceKey<PlacedFeature> WITHER_ROSE_PLACED_KEY = registerKey("wither_rose_placed");

    public static final ResourceKey<PlacedFeature> SIFT_GRASS_PLACED_KEY = registerKey("sift_grass_placed");
    public static final ResourceKey<PlacedFeature> TALL_SIFT_GRASS_PLACED_KEY = registerKey("tall_sift_grass_placed");

    public static void bootstrap(BootstrapContext<PlacedFeature> context) {
        var configuredFeatures = context.lookup(Registries.CONFIGURED_FEATURE);

        register(context, SIFT_PLACED_KEY, configuredFeatures.getOrThrow(ModConfiguredFeatures.SIFT_KEY),
                VegetationPlacements.treePlacement(PlacementUtils.countExtra(5, 0.1f, 1),
                        ModBlocks.SIFT_SAPLING.get()));

        register(context, SIFT_SPARSE_PLACED_KEY, configuredFeatures.getOrThrow(ModConfiguredFeatures.SIFT_KEY),
                VegetationPlacements.treePlacement(RarityFilter.onAverageOnceEvery(6),
                        ModBlocks.SIFT_SAPLING.get()));

        register(context, WITHERED_PLACED_KEY, configuredFeatures.getOrThrow(ModConfiguredFeatures.WITHERED_KEY),
                VegetationPlacements.treePlacement(PlacementUtils.countExtra(5, 0.1f, 1),
                        ModBlocks.WITHERED_SAPLING.get()));


        register(context, BAZULIUM_ORE_PLACED_KEY, configuredFeatures.getOrThrow(ModConfiguredFeatures.BAZULIUM_ORE_KEY),
                OrePlacements.commonOrePlacement(12,
                        HeightRangePlacement.triangle(VerticalAnchor.absolute(-64), VerticalAnchor.absolute(15))));

        register(context, WITHER_ROSE_PLACED_KEY, configuredFeatures.getOrThrow(ModConfiguredFeatures.WITHER_ROSE_KEY),
                List.of(RarityFilter.onAverageOnceEvery(32), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_WORLD_SURFACE, BiomeFilter.biome()));

        register(
                context,
                SIFT_GRASS_PLACED_KEY,
                configuredFeatures.getOrThrow(ModConfiguredFeatures.SIFT_GRASS_KEY),
                List.of(
                        CountPlacement.of(5),
                        InSquarePlacement.spread(),
                        PlacementUtils.HEIGHTMAP_WORLD_SURFACE,
                        BiomeFilter.biome(),

                        CountPlacement.of(32),
                        RandomOffsetPlacement.ofTriangle(7, 3),

                        BlockPredicateFilter.forPredicate(
                                BlockPredicate.allOf(
                                        BlockPredicate.ONLY_IN_AIR_PREDICATE,
                                        BlockPredicate.wouldSurvive(
                                                ModBlocks.SHORT_SIFT_GRASS.get().defaultBlockState(),
                                                net.minecraft.core.Vec3i.ZERO
                                        )
                                )
                        )
                )
        );

        register(
                context,
                TALL_SIFT_GRASS_PLACED_KEY,
                configuredFeatures.getOrThrow(ModConfiguredFeatures.TALL_SIFT_GRASS_KEY),
                List.of(
                        RarityFilter.onAverageOnceEvery(5),
                        InSquarePlacement.spread(),
                        PlacementUtils.HEIGHTMAP_WORLD_SURFACE,
                        BiomeFilter.biome(),

                        CountPlacement.of(48),
                        RandomOffsetPlacement.ofTriangle(7, 3),

                        BlockPredicateFilter.forPredicate(
                                BlockPredicate.allOf(
                                        BlockPredicate.ONLY_IN_AIR_PREDICATE,
                                        BlockPredicate.wouldSurvive(
                                                ModBlocks.TALL_SIFT_GRASS.get().defaultBlockState(),
                                                net.minecraft.core.Vec3i.ZERO
                                        )
                                )
                        )
                )
        );
    }

    private static ResourceKey<PlacedFeature> registerKey(String name) {
        return ResourceKey.create(Registries.PLACED_FEATURE, Identifier.fromNamespaceAndPath(Wellspring.MODID, name));
    }

    private static void register(BootstrapContext<PlacedFeature> context, ResourceKey<PlacedFeature> key,
                                 Holder<ConfiguredFeature<?, ?>> configuration, List<PlacementModifier> modifiers) {
        context.register(key, new PlacedFeature(configuration, List.copyOf(modifiers)));
    }
}
