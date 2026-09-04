package com.sydders.wellspring.portal;

import com.sydders.wellspring.block.ModBlocks;
import com.sydders.wellspring.block.custom.SiftPortalBlock;
import com.sydders.wellspring.worldgen.ModDimensions;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.BlockUtil;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

import java.util.Optional;

public final class SiftPortalManager {

    private static final Identifier SIFT_GATEWAY =
            Identifier.fromNamespaceAndPath(
                    "wellspring",
                    "sift_gateway"
            );

    private SiftPortalManager() {
    }

    public static boolean activate(
            ServerLevel level,
            BlockPos clickedPos
    ) {
        return SiftPortalShape.find(level, clickedPos)
                .map(shape -> {
                    activate(level, shape);
                    return true;
                })
                .orElse(false);
    }

    public static Optional<PortalEndpoint> findOrCreateDestination(
            ServerLevel level,
            BlockPos portalPos
    ) {
        SiftPortalSavedData savedData = SiftPortalSavedData.get(level.getServer());
        Optional<PortalEndpoint> destination = savedData.findDestination(
                level.dimension(),
                portalPos
        );

        if (destination.filter(endpoint -> isUsableDestination(level, endpoint)).isPresent()) {
            return destination;
        }

        if (level.dimension().equals(ModDimensions.SIFT)) {
            return Optional.empty();
        }

        destination.ifPresent(endpoint -> savedData.removeLinkNear(
                level.dimension(),
                portalPos
        ));

        return SiftPortalShape.find(level, portalPos)
                .flatMap(shape -> activate(level, shape));
    }

    private static Optional<PortalEndpoint> activate(
            ServerLevel level,
            SiftPortalShape shape
    ) {
        shape.createPortal(level);

        if (level.dimension().equals(ModDimensions.SIFT)) {
            return Optional.empty();
        }

        ServerLevel siftLevel = level.getServer().getLevel(ModDimensions.SIFT);

        if (siftLevel == null) {
            return Optional.empty();
        }

        PortalEndpoint source = new PortalEndpoint(
                level.dimension(),
                shape.portalCenter()
        );

        SiftPortalSavedData savedData = SiftPortalSavedData.get(level.getServer());
        Optional<PortalEndpoint> existing = savedData.findDestination(
                source.dimension(),
                source.position()
        );

        if (existing.filter(endpoint -> isUsableDestination(level, endpoint)).isPresent()) {
            return existing;
        }

        existing.ifPresent(endpoint -> savedData.removeLinkNear(
                source.dimension(),
                source.position()
        ));

        PortalEndpoint destination = new PortalEndpoint(
                siftLevel.dimension(),
                placeDestinationPortal(
                        siftLevel,
                        source.position(),
                        shape.structureRotation()
                )
        );

        savedData.addLinkIfAbsent(source, destination);

        return Optional.of(destination);
    }

    private static boolean isUsableDestination(
            ServerLevel sourceLevel,
            PortalEndpoint endpoint
    ) {
        ServerLevel destinationLevel = sourceLevel.getServer()
                .getLevel(endpoint.dimension());

        if (destinationLevel == null) {
            return false;
        }

        return !endpoint.dimension().equals(ModDimensions.SIFT)
                || endpoint.position().getY() > destinationLevel.getMinY() + 2;
    }

    private static BlockPos placeDestinationPortal(
            ServerLevel siftLevel,
            BlockPos sourcePortalPos,
            Rotation rotation
    ) {

        StructureTemplate template = siftLevel
                .getServer()
                .getStructureManager()
                .get(SIFT_GATEWAY)
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Missing structure: " + SIFT_GATEWAY
                        )
                );

        StructurePlaceSettings settings =
                new StructurePlaceSettings()
                        .setMirror(Mirror.NONE)
                        .setRotation(rotation)
                        .setIgnoreEntities(false);

        int targetX = sourcePortalPos.getX();
        int targetZ = sourcePortalPos.getZ();

        int groundY = findTopSolidGround(siftLevel, targetX, targetZ);

        Vec3i size = template.getSize(rotation);

        BlockPos origin = new BlockPos(
                targetX - size.getX() / 2,
                groundY,
                targetZ - size.getZ() / 2
        );

        boolean placed = template.placeInWorld(
                siftLevel,
                origin,
                origin,
                settings,
                RandomSource.create(),
                Block.UPDATE_ALL
        );

        if (!placed) {
            throw new IllegalStateException(
                    "Failed to place Sift gateway at " + origin
            );
        }

        BoundingBox box = template.getBoundingBox(
                settings,
                origin
        );

        return findPortalCentre(siftLevel, box);
    }

    private static int findTopSolidGround(
            ServerLevel level,
            int x,
            int z
    ) {
        level.getChunk(x >> 4, z >> 4);

        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

        for (int y = level.getMaxY() - 1; y >= level.getMinY(); y--) {
            pos.set(x, y, z);

            if (level.getBlockState(pos)
                    .isFaceSturdy(level, pos, Direction.UP)) {
                return y + 1;
            }
        }

        return level.getSeaLevel();
    }

    private static BlockPos findPortalCentre(
            ServerLevel level,
            BoundingBox box
    ) {
        BlockPos portalPos = findFirstPortalBlock(level, box)
                .orElseThrow(() ->
                        new IllegalStateException(
                                "The Sift gateway structure contains no SIFT_PORTAL blocks"
                        )
                );

        Direction.Axis axis = level.getBlockState(portalPos)
                .getValue(SiftPortalBlock.AXIS);
        BlockUtil.FoundRectangle portal = BlockUtil.getLargestRectangleAround(
                portalPos,
                axis,
                SiftPortalShape.PORTAL_WIDTH,
                Direction.Axis.Y,
                SiftPortalShape.PORTAL_HEIGHT,
                pos -> level.getBlockState(pos).is(ModBlocks.SIFT_PORTAL.get())
        );

        return portal.minCorner
                .relative(axis, portal.axis1Size / 2)
                .above(1);
    }

    private static Optional<BlockPos> findFirstPortalBlock(
            ServerLevel level,
            BoundingBox box
    ) {
        for (BlockPos pos : BlockPos.betweenClosed(
                new BlockPos(
                        box.minX(),
                        box.minY(),
                        box.minZ()
                ),
                new BlockPos(
                        box.maxX(),
                        box.maxY(),
                        box.maxZ()
                )
        )) {
            BlockState state = level.getBlockState(pos);

            if (state.is(ModBlocks.SIFT_PORTAL.get())) {
                return Optional.of(pos.immutable());
            }
        }

        return Optional.empty();
    }
}
