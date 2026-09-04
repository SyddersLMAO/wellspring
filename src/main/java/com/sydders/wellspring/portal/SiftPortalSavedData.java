package com.sydders.wellspring.portal;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class SiftPortalSavedData extends SavedData {

    private final List<PortalLink> links;

    public static final Codec<SiftPortalSavedData> CODEC =
            RecordCodecBuilder.create(instance ->
                    instance.group(
                            PortalLink.CODEC
                                    .listOf()
                                    .optionalFieldOf(
                                            "links",
                                            List.of()
                                    )
                                    .forGetter(data -> data.links)
                    ).apply(
                            instance,
                            SiftPortalSavedData::new
                    )
            );

    public static final SavedDataType<SiftPortalSavedData> TYPE =
            new SavedDataType<>(
                    Identifier.fromNamespaceAndPath(
                            "wellspring",
                            "sift_portal_links"
                    ),
                    SiftPortalSavedData::new,
                    CODEC
            );

    public SiftPortalSavedData() {
        this.links = new ArrayList<>();
    }

    private SiftPortalSavedData(List<PortalLink> links) {
        this.links = new ArrayList<>(links);
    }

    public static SiftPortalSavedData get(
            MinecraftServer server
    ) {

        ServerLevel overworld = Objects.requireNonNull(
                server.getLevel(Level.OVERWORLD)
        );

        return overworld
                .getDataStorage()
                .computeIfAbsent(TYPE);
    }

    public void addLinkIfAbsent(
            PortalEndpoint first,
            PortalEndpoint second
    ) {
        if (links.stream().anyMatch(link -> link.connects(first, second))) {
            return;
        }

        links.add(new PortalLink(first, second));
        setDirty();
    }

    public Optional<PortalEndpoint> findDestination(
            ResourceKey<Level> dimension,
            net.minecraft.core.BlockPos portalBlock
    ) {

        return links.stream()
                .map(link -> link.destinationFrom(dimension, portalBlock))
                .flatMap(Optional::stream)
                .findFirst();
    }

    public boolean removeLinkNear(
            ResourceKey<Level> dimension,
            net.minecraft.core.BlockPos portalBlock
    ) {
        boolean removed = links.removeIf(link -> link.contains(
                dimension,
                portalBlock
        ));

        if (removed) {
            setDirty();
        }

        return removed;
    }
}
