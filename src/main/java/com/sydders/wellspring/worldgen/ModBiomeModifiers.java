package com.sydders.wellspring.worldgen;

import com.sydders.wellspring.Wellspring;
import com.sydders.wellspring.entity.ModEntities;
import com.sydders.wellspring.tags.ModTags;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BiomeTags;
import net.minecraft.util.random.Weighted;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.BiomeModifiers;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class ModBiomeModifiers {
    public static final ResourceKey<BiomeModifier> ADD_TREE_SIFT = registerKey("add_tree_sift");
    public static final ResourceKey<BiomeModifier> ADD_TREE_SIFT_SPARSE = registerKey("add_tree_sift_sparse");
    public static final ResourceKey<BiomeModifier> ADD_TREE_WITHERED = registerKey("add_tree_withered");
    public static final ResourceKey<BiomeModifier> ADD_BLUB_SPAWNS = registerKey("add_blub_spawns");
    public static final ResourceKey<BiomeModifier> ADD_BAZULIUM_ORE = registerKey("add_bazulium_ore");
    public static final ResourceKey<BiomeModifier> ADD_WITHER_ROSE = registerKey("add_wither_rose");
    public static final ResourceKey<BiomeModifier> ADD_SIFT_GRASS = registerKey("add_sift_grass");

    public static void bootstrap(BootstrapContext<BiomeModifier> context) {
        var placedFeatures = context.lookup(Registries.PLACED_FEATURE);
        var biomes = context.lookup(Registries.BIOME);

        context.register(ADD_TREE_SIFT, new BiomeModifiers.AddFeaturesBiomeModifier(
                HolderSet.direct(
                        biomes.getOrThrow(biomeKey("sift_forest"))
                ),
                HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.SIFT_PLACED_KEY)),
                GenerationStep.Decoration.VEGETAL_DECORATION));

        context.register(ADD_TREE_SIFT_SPARSE, new BiomeModifiers.AddFeaturesBiomeModifier(
                HolderSet.direct(
                        biomes.getOrThrow(biomeKey("sift_plains")),
                        biomes.getOrThrow(biomeKey("jagged_arches"))
                ),
                HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.SIFT_SPARSE_PLACED_KEY)),
                GenerationStep.Decoration.VEGETAL_DECORATION));

        context.register(ADD_TREE_WITHERED, new BiomeModifiers.AddFeaturesBiomeModifier(
                HolderSet.direct(
                        biomes.getOrThrow(biomeKey("withered_forest"))
                ),
                HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.WITHERED_PLACED_KEY)),
                GenerationStep.Decoration.VEGETAL_DECORATION));

        context.register(ADD_BLUB_SPAWNS, BiomeModifiers.AddSpawnsBiomeModifier.singleSpawn(
                HolderSet.direct(
                        biomes.getOrThrow(biomeKey("sift_plains")),
                        biomes.getOrThrow(biomeKey("sift_forest")),
                        biomes.getOrThrow(biomeKey("jagged_arches")),
                        biomes.getOrThrow(biomeKey("withered_forest"))
                ),
                new Weighted<>(
                        new MobSpawnSettings.SpawnerData(ModEntities.BLUB.get(), 2, 4),
                        60
                )));

        context.register(ADD_BAZULIUM_ORE, new BiomeModifiers.AddFeaturesBiomeModifier(
                biomes.getOrThrow(ModTags.Biomes.IS_SIFT),
                HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.BAZULIUM_ORE_PLACED_KEY)),
                GenerationStep.Decoration.UNDERGROUND_ORES));

        context.register(ADD_WITHER_ROSE, new BiomeModifiers.AddFeaturesBiomeModifier(
                biomes.getOrThrow(ModTags.Biomes.IS_WITHERED),
                HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.WITHER_ROSE_PLACED_KEY)),
                GenerationStep.Decoration.VEGETAL_DECORATION));

        context.register(
                ADD_SIFT_GRASS,
                new BiomeModifiers.AddFeaturesBiomeModifier(
                        biomes.getOrThrow(ModTags.Biomes.IS_SIFT),
                        HolderSet.direct(
                                placedFeatures.getOrThrow(ModPlacedFeatures.SIFT_GRASS_PLACED_KEY),
                                placedFeatures.getOrThrow(ModPlacedFeatures.TALL_SIFT_GRASS_PLACED_KEY)
                        ),
                        GenerationStep.Decoration.VEGETAL_DECORATION
                )
        );
    }

    private static ResourceKey<BiomeModifier> registerKey(String name) {
        return ResourceKey.create(NeoForgeRegistries.Keys.BIOME_MODIFIERS, Identifier.fromNamespaceAndPath(Wellspring.MODID, name));
    }

    private static ResourceKey<Biome> biomeKey(String name) {
        return ResourceKey.create(Registries.BIOME, Identifier.fromNamespaceAndPath(Wellspring.MODID, name));
    }
}
