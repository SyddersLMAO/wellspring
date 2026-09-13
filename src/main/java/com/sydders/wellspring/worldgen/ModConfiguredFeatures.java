package com.sydders.wellspring.worldgen;

import com.sydders.wellspring.Wellspring;
import com.sydders.wellspring.block.ModBlocks;
import com.sydders.wellspring.worldgen.tree.WitheredTrunkPlacer;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.random.WeightedList;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.*;
import net.minecraft.world.level.levelgen.feature.featuresize.TwoLayersFeatureSize;
import net.minecraft.world.level.levelgen.feature.foliageplacers.BlobFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.WeightedStateProvider;
import net.minecraft.world.level.levelgen.feature.trunkplacers.ForkingTrunkPlacer;
import net.minecraft.world.level.levelgen.placement.BlockPredicateFilter;
import net.minecraft.world.level.levelgen.placement.CountPlacement;
import net.minecraft.world.level.levelgen.placement.RandomOffsetPlacement;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockMatchTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;

import java.util.List;

public class ModConfiguredFeatures {
    public static final ResourceKey<ConfiguredFeature<?, ?>> SIFT_KEY = registerKey("sift");
    public static final ResourceKey<ConfiguredFeature<?, ?>> WITHERED_KEY = registerKey("withered");

    public static final ResourceKey<ConfiguredFeature<?, ?>> BAZULIUM_ORE_KEY = registerKey("bazulium_ore");

    public static final ResourceKey<ConfiguredFeature<?, ?>> WITHER_ROSE_KEY = registerKey("wither_rose");

    public static final ResourceKey<ConfiguredFeature<?, ?>> SIFT_GRASS_KEY = registerKey("sift_grass");
    public static final ResourceKey<ConfiguredFeature<?, ?>> TALL_SIFT_GRASS_KEY = registerKey("tall_sift_grass");

    public static void bootstrap(BootstrapContext<ConfiguredFeature<?, ?>> context) {
        register(context, SIFT_KEY, Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(
                BlockStateProvider.simple(ModBlocks.SIFT_LOG.get()),
                new ForkingTrunkPlacer(4,3,4),

                BlockStateProvider.simple(ModBlocks.SIFT_LEAVES.get()),
                new BlobFoliagePlacer(ConstantInt.of(2), ConstantInt.of(3), 3),

                new TwoLayersFeatureSize(1, 0, 2)
        ).build());

        register(context, WITHERED_KEY, Feature.TREE, new TreeConfiguration.TreeConfigurationBuilder(
                BlockStateProvider.simple(ModBlocks.WITHERED_LOG.get()),
                new WitheredTrunkPlacer(6, 3, 4),

                BlockStateProvider.simple(Blocks.AIR),
                new BlobFoliagePlacer(ConstantInt.of(0), ConstantInt.of(0), 0),

                new TwoLayersFeatureSize(1, 0, 5)
        ).build());

        RuleTest siftStoneReplaceables = new BlockMatchTest(ModBlocks.SIFT_STONE.get());
        RuleTest hardenedSiftStoneReplaceables = new BlockMatchTest(ModBlocks.HARDENED_SIFT_STONE.get());

        register(context, BAZULIUM_ORE_KEY, Feature.ORE, new OreConfiguration(List.of(
                OreConfiguration.target(siftStoneReplaceables, ModBlocks.BAZULIUM_ORE.get().defaultBlockState()),
                OreConfiguration.target(hardenedSiftStoneReplaceables, ModBlocks.HARDENED_BAZULIUM_ORE.get().defaultBlockState())), 8));

        register(context, WITHER_ROSE_KEY, Feature.SIMPLE_RANDOM_SELECTOR,
                new SimpleRandomFeatureConfiguration(
                        HolderSet.direct(PlacementUtils.inlinePlaced(Feature.SIMPLE_BLOCK,
                                new SimpleBlockConfiguration(BlockStateProvider.simple(Blocks.WITHER_ROSE.defaultBlockState())),
                                CountPlacement.of(32),
                                RandomOffsetPlacement.ofTriangle(6, 3),
                                BlockPredicateFilter.forPredicate(BlockPredicate.ONLY_IN_AIR_PREDICATE)))));

        WeightedList.Builder<BlockState> siftGrassStates = WeightedList.builder();

        siftGrassStates.add(
                ModBlocks.SHORT_SIFT_GRASS.get().defaultBlockState(),
                4
        );

        siftGrassStates.add(
                ModBlocks.TALL_SIFT_GRASS.get()
                        .defaultBlockState()
                        .setValue(DoublePlantBlock.HALF, DoubleBlockHalf.LOWER),
                1
        );

        register(
                context,
                SIFT_GRASS_KEY,
                Feature.SIMPLE_BLOCK,
                new SimpleBlockConfiguration(
                        BlockStateProvider.simple(
                                ModBlocks.SHORT_SIFT_GRASS.get().defaultBlockState()
                        )
                )
        );

        register(
                context,
                TALL_SIFT_GRASS_KEY,
                Feature.SIMPLE_BLOCK,
                new SimpleBlockConfiguration(
                        BlockStateProvider.simple(
                                ModBlocks.TALL_SIFT_GRASS.get().defaultBlockState()
                        )
                )
        );

    }

    public static ResourceKey<ConfiguredFeature<?, ?>> registerKey(String name) {
        return ResourceKey.create(Registries.CONFIGURED_FEATURE, Identifier.fromNamespaceAndPath(Wellspring.MODID, name));
    }

    private static <FC extends FeatureConfiguration, F extends Feature<FC>> void register(BootstrapContext<ConfiguredFeature<?, ?>> context,
                                                                                          ResourceKey<ConfiguredFeature<?, ?>> key, F feature, FC configuration) {
        context.register(key, new ConfiguredFeature<>(feature, configuration));
    }
}
