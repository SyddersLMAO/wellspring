package com.sydders.wellspring.worldgen.structure;

import com.sydders.wellspring.Wellspring;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModStructureTypes {
    private static final DeferredRegister<StructureType<?>> STRUCTURE_TYPES =
            DeferredRegister.create(BuiltInRegistries.STRUCTURE_TYPE, Wellspring.MODID);

    public static final Supplier<StructureType<CaveWallDungeonStructure>> CAVE_WALL_DUNGEON =
            STRUCTURE_TYPES.register("cave_wall_dungeon", () -> () -> CaveWallDungeonStructure.CODEC);

    public static void register(IEventBus eventBus) {
        STRUCTURE_TYPES.register(eventBus);
    }
}
