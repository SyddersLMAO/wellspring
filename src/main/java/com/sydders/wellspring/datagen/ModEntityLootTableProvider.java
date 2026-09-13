package com.sydders.wellspring.datagen;

import com.sydders.wellspring.entity.ModEntities;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.EntityLootSubProvider;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.storage.loot.LootTable;

import java.util.stream.Stream;

public class ModEntityLootTableProvider extends EntityLootSubProvider {

    public ModEntityLootTableProvider(HolderLookup.Provider registries) {
        super(FeatureFlags.DEFAULT_FLAGS, registries);
    }

    @Override
    public void generate() {
        this.add(
                ModEntities.BLUB.get(),
                LootTable.lootTable()
        );
    }

    @Override
    protected Stream<EntityType<?>> getKnownEntityTypes() {
        return ModEntities.ENTITY_TYPES.getEntries()
                .stream()
                .map(entry -> (EntityType<?>) entry.value());
    }
}