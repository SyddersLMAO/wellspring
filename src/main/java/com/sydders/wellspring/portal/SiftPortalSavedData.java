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

    public void addLink(
            PortalEndpoint first,
            PortalEndpoint second
    ) {
        links.add(new PortalLink(first, second));
        setDirty();
    }

    public void addLinkIfAbsent(
            PortalEndpoint first,
            PortalEndpoint second
    ) {
        for (PortalLink link : links) {
            boolean sameOrder = link.first().equals(first)
                    && link.second().equals(second);
            boolean reverseOrder = link.first().equals(second)
                    && link.second().equals(first);

            if (sameOrder || reverseOrder) {
                return;
            }
        }

        addLink(first, second);
    }

    public Optional<PortalEndpoint> findDestination(
            ResourceKey<Level> dimension,
            net.minecraft.core.BlockPos portalBlock
    ) {

        for (PortalLink link : links) {

            if (matches(
                    link.first(),
                    dimension,
                    portalBlock
            )) {
                return Optional.of(link.second());
            }

            if (matches(
                    link.second(),
                    dimension,
                    portalBlock
            )) {
                return Optional.of(link.first());
            }
        }

        return Optional.empty();
    }

    public boolean removeLinkNear(
            ResourceKey<Level> dimension,
            net.minecraft.core.BlockPos portalBlock
    ) {
        boolean removed = links.removeIf(link ->
                matches(link.first(), dimension, portalBlock)
                        || matches(link.second(), dimension, portalBlock)
        );

        if (removed) {
            setDirty();
        }

        return removed;
    }

    private boolean matches(
            PortalEndpoint endpoint,
            ResourceKey<Level> dimension,
            net.minecraft.core.BlockPos pos
    ) {

        if (!endpoint.dimension().equals(dimension)) {
            return false;
        }

        int dx = endpoint.position().getX() - pos.getX();
        int dy = endpoint.position().getY() - pos.getY();
        int dz = endpoint.position().getZ() - pos.getZ();

        return dx * dx + dy * dy + dz * dz <= 14 * 14;
    }
}
