package com.sydders.wellspring.portal;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

public record PortalEndpoint(
        ResourceKey<Level> dimension,
        BlockPos position
) {

    public static final Codec<PortalEndpoint> CODEC =
            RecordCodecBuilder.create(instance ->
                    instance.group(
                            ResourceKey.codec(Registries.DIMENSION)
                                    .fieldOf("dimension")
                                    .forGetter(PortalEndpoint::dimension),

                            BlockPos.CODEC
                                    .fieldOf("position")
                                    .forGetter(PortalEndpoint::position)
                    ).apply(
                            instance,
                            PortalEndpoint::new
                    )
            );
}