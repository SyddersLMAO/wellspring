package com.sydders.wellspring.portal;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

import java.util.Optional;

public record PortalLink(
        PortalEndpoint first,
        PortalEndpoint second
) {

    public static final Codec<PortalLink> CODEC =
            RecordCodecBuilder.create(instance ->
                    instance.group(
                            PortalEndpoint.CODEC
                                    .fieldOf("first")
                                    .forGetter(PortalLink::first),

                            PortalEndpoint.CODEC
                                    .fieldOf("second")
                                    .forGetter(PortalLink::second)
                    ).apply(
                            instance,
                            PortalLink::new
                    )
            );

    public boolean connects(
            PortalEndpoint firstEndpoint,
            PortalEndpoint secondEndpoint
    ) {
        return first.equals(firstEndpoint) && second.equals(secondEndpoint)
                || first.equals(secondEndpoint) && second.equals(firstEndpoint);
    }

    public boolean contains(
            ResourceKey<Level> dimension,
            BlockPos position
    ) {
        return first.matches(dimension, position)
                || second.matches(dimension, position);
    }

    public Optional<PortalEndpoint> destinationFrom(
            ResourceKey<Level> dimension,
            BlockPos position
    ) {
        if (first.matches(dimension, position)) {
            return Optional.of(second);
        }

        if (second.matches(dimension, position)) {
            return Optional.of(first);
        }

        return Optional.empty();
    }
}
