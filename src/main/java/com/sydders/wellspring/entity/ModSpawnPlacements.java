package com.sydders.wellspring.entity;

import com.sydders.wellspring.entity.custom.BlubEntity;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;

public class ModSpawnPlacements {
    public static void registerSpawnPlacements(RegisterSpawnPlacementsEvent event) {
        event.register(
                ModEntities.BLUB.get(),
                SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                BlubEntity::checkBlubSpawnRules,
                RegisterSpawnPlacementsEvent.Operation.REPLACE
        );
    }
}
