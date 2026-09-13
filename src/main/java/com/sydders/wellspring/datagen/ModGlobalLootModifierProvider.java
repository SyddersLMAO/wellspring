package com.sydders.wellspring.datagen;

import com.sydders.wellspring.Wellspring;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.data.GlobalLootModifierProvider;
import net.neoforged.neoforge.common.loot.AddTableLootModifier;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.common.loot.LootTableIdCondition;

import java.util.concurrent.CompletableFuture;

public class ModGlobalLootModifierProvider extends GlobalLootModifierProvider {

    public ModGlobalLootModifierProvider(
            PackOutput output,
            CompletableFuture<HolderLookup.Provider> registries
    ) {
        super(output, registries, Wellspring.MODID);
    }

    @Override
    protected void start() {
        this.add(
                "warden_key",
                new AddTableLootModifier(
                        new LootItemCondition[]{
                                LootTableIdCondition.builder(
                                        Identifier.withDefaultNamespace(
                                                "entities/warden"
                                        )
                                ).build()
                        },
                        IGlobalLootModifier.DEFAULT_PRIORITY,
                        ModLootTables.WARDEN_BONUS
                )
        );
    }
}