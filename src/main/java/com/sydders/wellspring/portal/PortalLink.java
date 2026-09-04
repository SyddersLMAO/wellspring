package com.sydders.wellspring.portal;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

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
}