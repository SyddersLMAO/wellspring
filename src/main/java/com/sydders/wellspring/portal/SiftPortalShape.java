package com.sydders.wellspring.portal;

import com.sydders.wellspring.block.ModBlocks;
import com.sydders.wellspring.block.custom.SiftPortalBlock;
import com.sydders.wellspring.worldgen.ModDimensions;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;

import java.util.Optional;

public record SiftPortalShape(
        BlockPos bottomLeft,
        Direction.Axis axis
) {

    public static final int FRAME_WIDTH = 22;
    public static final int FRAME_HEIGHT = 8;

    public static final int PORTAL_WIDTH = FRAME_WIDTH - 2;
    public static final int PORTAL_HEIGHT = FRAME_HEIGHT - 2;

    public static Optional<SiftPortalShape> find(
            ServerLevel level,
            BlockPos clickedPos
    ) {

        for (Direction.Axis axis : new Direction.Axis[]{
                Direction.Axis.X,
                Direction.Axis.Z
        }) {

            Direction right = axis == Direction.Axis.X
                    ? Direction.EAST
                    : Direction.SOUTH;

            for (int x = 0; x < FRAME_WIDTH; x++) {
                for (int y = 0; y < FRAME_HEIGHT; y++) {

                    BlockPos possibleBottomLeft = clickedPos
                            .relative(right, -x)
                            .below(y);

                    if (isValid(level, possibleBottomLeft, right)) {
                        return Optional.of(
                                new SiftPortalShape(
                                        possibleBottomLeft,
                                        axis
                                )
                        );
                    }
                }
            }
        }

        return Optional.empty();
    }

    public static boolean trySpawnPortal(
            ServerLevel level,
            BlockPos clickedPos
    ) {
        Optional<SiftPortalShape> shape = find(level, clickedPos);

        if (shape.isEmpty()) {
            return false;
        }

        shape.get().activate(level);
        return true;
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

        if (destination.isPresent()
                && isUsableDestination(level, destination.get())) {
            return destination;
        }

        if (destination.isPresent()) {
            savedData.removeLinkNear(
                    level.dimension(),
                    portalPos
            );
        } else if (level.dimension().equals(ModDimensions.SIFT)) {
            return destination;
        }

        Optional<SiftPortalShape> shape = find(level, portalPos);

        if (shape.isEmpty()) {
            return Optional.empty();
        }

        shape.get().activate(level);

        return savedData.findDestination(
                level.dimension(),
                portalPos
        );
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

        if (!endpoint.dimension().equals(ModDimensions.SIFT)) {
            return true;
        }

        return endpoint.position().getY() > destinationLevel.getMinY() + 2;
    }

    private static boolean isValid(
            ServerLevel level,
            BlockPos bottomLeft,
            Direction right
    ) {

        for (int x = 0; x < FRAME_WIDTH; x++) {
            for (int y = 0; y < FRAME_HEIGHT; y++) {

                BlockPos pos = bottomLeft
                        .relative(right, x)
                        .above(y);

                boolean frame =
                        x == 0 ||
                                x == FRAME_WIDTH - 1 ||
                                y == 0 ||
                                y == FRAME_HEIGHT - 1;

                if (frame) {
                    if (!level.getBlockState(pos)
                            .is(Blocks.REINFORCED_DEEPSLATE)) {
                        return false;
                    }
                } else {
                    var state = level.getBlockState(pos);

                    if (!state.isAir()
                            && !state.is(ModBlocks.SIFT_PORTAL.get())) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    public void createPortal(ServerLevel level) {

        Direction right = axis == Direction.Axis.X
                ? Direction.EAST
                : Direction.SOUTH;

        var portalState = ModBlocks.SIFT_PORTAL.get()
                .defaultBlockState()
                .setValue(SiftPortalBlock.AXIS, axis);

        for (int x = 1; x < FRAME_WIDTH - 1; x++) {
            for (int y = 1; y < FRAME_HEIGHT - 1; y++) {

                BlockPos pos = bottomLeft
                        .relative(right, x)
                        .above(y);

                level.setBlock(
                        pos,
                        portalState,
                        3
                );
            }
        }
    }

    public void activate(ServerLevel level) {
        createPortal(level);

        if (level.dimension().equals(ModDimensions.SIFT)) {
            return;
        }

        ServerLevel siftLevel = level.getServer().getLevel(ModDimensions.SIFT);

        if (siftLevel == null) {
            return;
        }

        PortalEndpoint source = new PortalEndpoint(
                level.dimension(),
                portalCenter()
        );

        SiftPortalSavedData savedData = SiftPortalSavedData.get(level.getServer());

        if (savedData.findDestination(
                source.dimension(),
                source.position()
        ).isPresent()) {
            return;
        }

        BlockPos destinationPortal = SiftPortalManager.createDestinationPortal(
                siftLevel,
                source.position(),
                structureRotation()
        );

        savedData.addLinkIfAbsent(
                source,
                new PortalEndpoint(
                        siftLevel.dimension(),
                        destinationPortal
                )
        );
    }

    public BlockPos portalCenter() {
        Direction right = axis == Direction.Axis.X
                ? Direction.EAST
                : Direction.SOUTH;

        return bottomLeft
                .relative(right, FRAME_WIDTH / 2)
                .above(PORTAL_HEIGHT / 2);
    }

    private Rotation structureRotation() {
        return axis == Direction.Axis.X
                ? Rotation.CLOCKWISE_90
                : Rotation.NONE;
    }
}
