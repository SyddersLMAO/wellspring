package com.sydders.wellspring.entity;

import com.sydders.wellspring.entity.custom.BlubEntity;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;

public class ModEntityAttributes {
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(
                ModEntities.BLUB.get(),
                BlubEntity.createAttributes().build()
        );
    }
}
