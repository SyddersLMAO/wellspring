package com.sydders.wellspring.datagen;

import com.sydders.wellspring.Wellspring;
import com.sydders.wellspring.item.ModItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

import java.util.function.BiConsumer;

public class ModChestLootTableProvider implements LootTableSubProvider {
    public static final ResourceKey<LootTable> DUNGEON_KEY = ResourceKey.create(
            Registries.LOOT_TABLE, Identifier.fromNamespaceAndPath(Wellspring.MODID, "chests/dungeon"));
    public static final ResourceKey<LootTable> WITHERED_BUILDING_KEY = ResourceKey.create(
            Registries.LOOT_TABLE, Identifier.fromNamespaceAndPath(Wellspring.MODID, "chests/withered_building"));
    public static final ResourceKey<LootTable> PILLAGER_OUTPOST_KEY = ResourceKey.create(
            Registries.LOOT_TABLE, Identifier.fromNamespaceAndPath(Wellspring.MODID, "chests/pillager_outpost"));

    @Override
    public void generate(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> output) {
        output.accept(DUNGEON_KEY, LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .setRolls(UniformGenerator.between(3, 6))
                        .add(LootItem.lootTableItem(Items.BREAD)
                                .setWeight(10)
                                .apply(SetItemCountFunction.setCount(
                                        UniformGenerator.between(1,3))))
                        .add(LootItem.lootTableItem(ModItems.BAZULIUM_INGOT.get())
                                .setWeight(5)
                                .apply(SetItemCountFunction.setCount(
                                        UniformGenerator.between(1,3))))));

        output.accept(WITHERED_BUILDING_KEY, LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .setRolls(UniformGenerator.between(3, 6))
                        .add(LootItem.lootTableItem(ModItems.OCA.get())
                                .setWeight(10)
                                .apply(SetItemCountFunction.setCount(
                                        UniformGenerator.between(1,3))))
                        .add(LootItem.lootTableItem(ModItems.BAZULIUM_INGOT.get())
                                .setWeight(1)
                                .apply(SetItemCountFunction.setCount(
                                        UniformGenerator.between(1,2))))));

        output.accept(PILLAGER_OUTPOST_KEY, LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .setRolls(UniformGenerator.between(3, 6))
                        .add(LootItem.lootTableItem(Items.CROSSBOW)
                                .setWeight(3)
                                .apply(SetItemCountFunction.setCount(
                                        UniformGenerator.between(1,3))))
                        .add(LootItem.lootTableItem(Items.BREAD)
                                .setWeight(5)
                                .apply(SetItemCountFunction.setCount(
                                        UniformGenerator.between(1,3))))));
    }
}
