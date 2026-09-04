package com.sydders.wellspring.portal;

import com.sydders.wellspring.block.ModBlocks;
import com.sydders.wellspring.block.custom.SiftPortalBlock;
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

            Direction right = right(axis);

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

        var portalState = ModBlocks.SIFT_PORTAL.get()
                .defaultBlockState()
                .setValue(SiftPortalBlock.AXIS, axis);

        BlockPos.betweenClosed(
                bottomLeft.relative(right(), 1).above(),
                bottomLeft.relative(right(), PORTAL_WIDTH).above(PORTAL_HEIGHT)
        ).forEach(pos -> level.setBlock(pos, portalState, 3));
    }

    public BlockPos portalCenter() {
        return bottomLeft
                .relative(right(), FRAME_WIDTH / 2)
                .above(PORTAL_HEIGHT / 2);
    }

    Rotation structureRotation() {
        return axis == Direction.Axis.X
                ? Rotation.CLOCKWISE_90
                : Rotation.NONE;
    }

    private Direction right() {
        return right(axis);
    }

    private static Direction right(Direction.Axis axis) {
        return axis == Direction.Axis.X
                ? Direction.EAST
                : Direction.SOUTH;
    }
}
