package com.sydders.wellspring.portal;

import com.sydders.wellspring.block.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.BlockUtil;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.NetherPortalBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.apache.commons.lang3.mutable.MutableInt;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Predicate;

public class SiftPortalShape {
    private static final int MIN_WIDTH = 2;
    public static final int MAX_WIDTH = 21;
    private static final int MIN_HEIGHT = 3;
    public static final int MAX_HEIGHT = 21;
    private static final BlockBehaviour.StatePredicate FRAME =
            (state, level, pos) -> state.is(Blocks.REINFORCED_DEEPSLATE);
    private static final float SAFE_TRAVEL_MAX_ENTITY_XY = 4.0F;

    private final Direction.Axis axis;
    private final Direction rightDir;
    private final int numPortalBlocks;
    private final BlockPos bottomLeft;
    private final int height;
    private final int width;

    public SiftPortalShape(
            Direction.Axis axis,
            int portalBlockCount,
            Direction rightDir,
            BlockPos bottomLeft,
            int width,
            int height
    ) {
        this.axis = axis;
        this.numPortalBlocks = portalBlockCount;
        this.rightDir = rightDir;
        this.bottomLeft = bottomLeft;
        this.width = width;
        this.height = height;
    }

    public static Optional<SiftPortalShape> findEmptyPortalShape(
            LevelAccessor level,
            BlockPos pos,
            Direction.Axis preferredAxis
    ) {
        return findPortalShape(
                level,
                pos,
                shape -> shape.isValid() && shape.numPortalBlocks == 0,
                preferredAxis
        );
    }

    public static Optional<SiftPortalShape> findPortalShape(
            LevelAccessor level,
            BlockPos pos,
            Predicate<SiftPortalShape> isValid,
            Direction.Axis preferredAxis
    ) {
        Optional<SiftPortalShape> firstAxis = Optional.of(
                findAnyShape(level, pos, preferredAxis)
        ).filter(isValid);

        if (firstAxis.isPresent()) {
            return firstAxis;
        }

        Direction.Axis otherAxis = preferredAxis == Direction.Axis.X
                ? Direction.Axis.Z
                : Direction.Axis.X;

        return Optional.of(findAnyShape(level, pos, otherAxis))
                .filter(isValid);
    }

    public static SiftPortalShape findAnyShape(
            BlockGetter level,
            BlockPos pos,
            Direction.Axis axis
    ) {
        Direction rightDir = axis == Direction.Axis.X
                ? Direction.WEST
                : Direction.SOUTH;
        BlockPos bottomLeft = calculateBottomLeft(level, rightDir, pos);

        if (bottomLeft == null) {
            return new SiftPortalShape(axis, 0, rightDir, pos, 0, 0);
        }

        int width = calculateWidth(level, bottomLeft, rightDir);

        if (width == 0) {
            return new SiftPortalShape(axis, 0, rightDir, bottomLeft, 0, 0);
        }

        MutableInt portalBlockCountOutput = new MutableInt();
        int height = calculateHeight(
                level,
                bottomLeft,
                rightDir,
                width,
                portalBlockCountOutput
        );

        return new SiftPortalShape(
                axis,
                portalBlockCountOutput.intValue(),
                rightDir,
                bottomLeft,
                width,
                height
        );
    }

    @Nullable
    private static BlockPos calculateBottomLeft(
            BlockGetter level,
            Direction rightDir,
            BlockPos pos
    ) {
        int minY = Math.max(level.getMinY(), pos.getY() - MAX_HEIGHT);

        while (pos.getY() > minY && isEmpty(level.getBlockState(pos.below()))) {
            pos = pos.below();
        }

        Direction leftDir = rightDir.getOpposite();
        int edge = getDistanceUntilEdgeAboveFrame(level, pos, leftDir) - 1;

        return edge < 0 ? null : pos.relative(leftDir, edge);
    }

    private static int calculateWidth(
            BlockGetter level,
            BlockPos bottomLeft,
            Direction rightDir
    ) {
        int width = getDistanceUntilEdgeAboveFrame(level, bottomLeft, rightDir);

        return width >= MIN_WIDTH && width <= MAX_WIDTH ? width : 0;
    }

    private static int getDistanceUntilEdgeAboveFrame(
            BlockGetter level,
            BlockPos pos,
            Direction direction
    ) {
        BlockPos.MutableBlockPos blockPos = new BlockPos.MutableBlockPos();

        for (int width = 0; width <= MAX_WIDTH; width++) {
            blockPos.set(pos).move(direction, width);
            BlockState blockState = level.getBlockState(blockPos);

            if (!isEmpty(blockState)) {
                if (FRAME.test(blockState, level, blockPos)) {
                    return width;
                }

                break;
            }

            BlockState belowState = level.getBlockState(blockPos.move(Direction.DOWN));

            if (!FRAME.test(belowState, level, blockPos)) {
                break;
            }
        }

        return 0;
    }

    private static int calculateHeight(
            BlockGetter level,
            BlockPos bottomLeft,
            Direction rightDir,
            int width,
            MutableInt portalBlockCount
    ) {
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        int height = getDistanceUntilTop(
                level,
                bottomLeft,
                rightDir,
                pos,
                width,
                portalBlockCount
        );

        return height >= MIN_HEIGHT
                && height <= MAX_HEIGHT
                && hasTopFrame(level, bottomLeft, rightDir, pos, width, height)
                ? height
                : 0;
    }

    private static boolean hasTopFrame(
            BlockGetter level,
            BlockPos bottomLeft,
            Direction rightDir,
            BlockPos.MutableBlockPos pos,
            int width,
            int height
    ) {
        for (int i = 0; i < width; i++) {
            BlockPos.MutableBlockPos framePos = pos
                    .set(bottomLeft)
                    .move(Direction.UP, height)
                    .move(rightDir, i);

            if (!FRAME.test(level.getBlockState(framePos), level, framePos)) {
                return false;
            }
        }

        return true;
    }

    private static int getDistanceUntilTop(
            BlockGetter level,
            BlockPos bottomLeft,
            Direction rightDir,
            BlockPos.MutableBlockPos pos,
            int width,
            MutableInt portalBlockCount
    ) {
        for (int height = 0; height < MAX_HEIGHT; height++) {
            pos.set(bottomLeft).move(Direction.UP, height).move(rightDir, -1);

            if (!FRAME.test(level.getBlockState(pos), level, pos)) {
                return height;
            }

            pos.set(bottomLeft).move(Direction.UP, height).move(rightDir, width);

            if (!FRAME.test(level.getBlockState(pos), level, pos)) {
                return height;
            }

            for (int i = 0; i < width; i++) {
                pos.set(bottomLeft).move(Direction.UP, height).move(rightDir, i);
                BlockState state = level.getBlockState(pos);

                if (!isEmpty(state)) {
                    return height;
                }

                if (state.is(ModBlocks.SIFT_PORTAL.get())) {
                    portalBlockCount.increment();
                }
            }
        }

        return MAX_HEIGHT;
    }

    private static boolean isEmpty(BlockState state) {
        return state.isAir() || state.is(ModBlocks.SIFT_PORTAL.get());
    }

    public boolean isValid() {
        return width >= MIN_WIDTH
                && width <= MAX_WIDTH
                && height >= MIN_HEIGHT
                && height <= MAX_HEIGHT;
    }

    public void createPortalBlocks(LevelAccessor level) {
        BlockState portalState = ModBlocks.SIFT_PORTAL.get()
                .defaultBlockState()
                .setValue(NetherPortalBlock.AXIS, axis);

        BlockPos.betweenClosed(
                bottomLeft,
                bottomLeft.relative(Direction.UP, height - 1)
                        .relative(rightDir, width - 1)
        ).forEach(pos -> level.setBlock(pos, portalState, 18));
    }

    public boolean isComplete() {
        return isValid() && numPortalBlocks == width * height;
    }

    public static Vec3 findCollisionFreePosition(
            Vec3 bottomCenter,
            ServerLevel serverLevel,
            Entity entity,
            EntityDimensions dimensions
    ) {
        if (dimensions.width() > SAFE_TRAVEL_MAX_ENTITY_XY
                || dimensions.height() > SAFE_TRAVEL_MAX_ENTITY_XY) {
            return bottomCenter;
        }

        double halfHeight = dimensions.height() / 2.0;
        Vec3 center = bottomCenter.add(0.0, halfHeight, 0.0);
        VoxelShape allowedCenters = Shapes.create(
                AABB.ofSize(center, dimensions.width(), 0.0, dimensions.width())
                        .expandTowards(0.0, 1.0, 0.0)
                        .inflate(1.0E-6)
        );
        Optional<Vec3> collisionFreePosition = serverLevel.findFreePosition(
                entity,
                allowedCenters,
                center,
                dimensions.width(),
                dimensions.height(),
                dimensions.width()
        );

        return collisionFreePosition
                .map(vec -> vec.subtract(0.0, halfHeight, 0.0))
                .orElse(bottomCenter);
    }
}
