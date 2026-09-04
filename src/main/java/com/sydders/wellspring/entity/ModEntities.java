package com.sydders.wellspring.entity;

import com.sydders.wellspring.Wellspring;
import com.sydders.wellspring.entity.custom.BlubEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModEntities {
    public static final DeferredRegister.Entities ENTITY_TYPES =
            DeferredRegister.createEntities(Wellspring.MODID);

    public static final Supplier<EntityType<BlubEntity>> BLUB =
            ENTITY_TYPES.registerEntityType(
                    "blub",
                    BlubEntity::new,
                    MobCategory.CREATURE,
                    builder -> builder
                            .sized(0.8f, 0.8f)
                            .clientTrackingRange(8)
                            .updateInterval(3)
            );
}
