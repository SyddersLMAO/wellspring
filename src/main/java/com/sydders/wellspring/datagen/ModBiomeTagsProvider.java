package com.sydders.wellspring.datagen;

import com.sydders.wellspring.Wellspring;
import com.sydders.wellspring.tags.ModTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.BiomeTagsProvider;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;

import java.util.concurrent.CompletableFuture;

public class ModBiomeTagsProvider extends BiomeTagsProvider {
    public ModBiomeTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider, Wellspring.MODID);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(ModTags.Biomes.IS_SIFT)
                .addOptional(biomeId("sift_plains"))
                .addOptional(biomeId("sift_forest"))
                .addOptional(biomeId("jagged_arches"))
                .addOptional(biomeId("withered_forest"));

        tag(ModTags.Biomes.IS_WITHERED)
                .addOptional(biomeId("withered_forest"));
    }

    private ResourceKey<Biome> biomeId(String name) {
        return ResourceKey.create(Registries.BIOME, Identifier.fromNamespaceAndPath(Wellspring.MODID, name));
    }
}
