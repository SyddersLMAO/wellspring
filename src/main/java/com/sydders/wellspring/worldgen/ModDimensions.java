package com.sydders.wellspring.worldgen;

import com.sydders.wellspring.Wellspring;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

public class ModDimensions {
    public static final ResourceKey<Level> SIFT = ResourceKey.create(
            Registries.DIMENSION,
            Identifier.fromNamespaceAndPath(Wellspring.MODID, "sift")
    );
}
