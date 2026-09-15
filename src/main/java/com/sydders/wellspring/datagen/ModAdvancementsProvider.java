package com.sydders.wellspring.datagen;

import com.mojang.serialization.JsonOps;
import com.sydders.wellspring.Wellspring;
import com.sydders.wellspring.block.ModBlocks;
import com.sydders.wellspring.item.ModItems;
import com.sydders.wellspring.worldgen.ModDimensions;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.criterion.ChangeDimensionTrigger;
import net.minecraft.advancements.criterion.LocationPredicate;
import net.minecraft.advancements.criterion.PlayerTrigger;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderOwner;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.advancements.AdvancementProvider;
import net.minecraft.data.advancements.AdvancementSubProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class ModAdvancementsProvider extends AdvancementProvider {
    public ModAdvancementsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, List.of(new WellspringModAdvancements()));
    }

    public static class WellspringModAdvancements implements AdvancementSubProvider {
        @Override
        public void generate(HolderLookup.Provider registries, Consumer<AdvancementHolder> output) {
            var biomeOwner = registries.createSerializationContext(JsonOps.INSTANCE)
                    .owner(Registries.BIOME).orElseThrow();

            AdvancementHolder root = Advancement.Builder.advancement()
                    .display(
                            ModItems.WARDEN_KEY,
                            Component.translatable("advancements.sift.root.title"),
                            Component.translatable("advancements.sift.root.description"),
                            Identifier.fromNamespaceAndPath(Wellspring.MODID, "block/sift_stone"),
                            AdvancementType.TASK,
                            true,
                            true,
                            false
                    )
                    .addCriterion("entered_sift", ChangeDimensionTrigger.TriggerInstance.changedDimensionTo(ModDimensions.SIFT))
                    .save(output, Identifier.fromNamespaceAndPath(Wellspring.MODID, "sift/root"));

            AdvancementHolder siftBiomes = Advancement.Builder.advancement()
                    .parent(root)
                    .display(
                            ModBlocks.SIFT_SAPLING,
                            Component.translatable("advancements.sift.sift_biomes.title"),
                            Component.translatable("advancements.sift.sift_biomes.description"),
                            null,
                            AdvancementType.CHALLENGE,
                            true,
                            true,
                            false
                    )
                    .addCriterion("sift_plains", PlayerTrigger.TriggerInstance.located(
                            LocationPredicate.Builder.inBiome(biomeReference(biomeOwner, "sift_plains"))))
                    .addCriterion("sift_forest", PlayerTrigger.TriggerInstance.located(
                            LocationPredicate.Builder.inBiome(biomeReference(biomeOwner, "sift_forest"))))
                    .addCriterion("jagged_arches", PlayerTrigger.TriggerInstance.located(
                            LocationPredicate.Builder.inBiome(biomeReference(biomeOwner, "jagged_arches"))))
                    .addCriterion("withered_forest", PlayerTrigger.TriggerInstance.located(
                            LocationPredicate.Builder.inBiome(biomeReference(biomeOwner, "withered_forest"))))
                    .save(output, Identifier.fromNamespaceAndPath(Wellspring.MODID, "sift/sift_biomes"));
        }

        private static Holder.Reference<Biome> biomeReference(HolderOwner<Biome> owner, String name) {
            var key = ResourceKey.create(Registries.BIOME, Identifier.fromNamespaceAndPath(Wellspring.MODID, name));
            return Holder.Reference.createStandAlone(owner, key);
        }
    }
}
