package com.sydders.wellspring.worldgen.tree;

import com.sydders.wellspring.Wellspring;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacerType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModTrunkPlacerTypes {
    public static final DeferredRegister<TrunkPlacerType<?>> TRUNK_PLACER_TYPES =
            DeferredRegister.create(BuiltInRegistries.TRUNK_PLACER_TYPE, Wellspring.MODID);

    public static final Supplier<TrunkPlacerType<WitheredTrunkPlacer>> WITHERED =
            TRUNK_PLACER_TYPES.register("withered_trunk_placer",
                    () -> new TrunkPlacerType<>(WitheredTrunkPlacer.CODEC));

    public static void register(IEventBus eventBus) {
        TRUNK_PLACER_TYPES.register(eventBus);
    }
}
