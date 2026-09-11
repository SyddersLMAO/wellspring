package com.sydders.wellspring.block.custom;

import com.sydders.wellspring.Wellspring;
import com.sydders.wellspring.portal.SiftPortalShape;
import com.sydders.wellspring.portal.SiftTeleporter;
import com.sydders.wellspring.worldgen.ModDimensions;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.BlockUtil;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Relative;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.NetherPortalBlock;
import net.minecraft.world.level.block.Portal;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class SiftPortalBlock extends NetherPortalBlock {
    public SiftPortalBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    public static boolean canSpawnPortal(
            Level level,
            BlockPos pos
    ) {
        return SiftPortalShape.findEmptyPortalShape(
                level,
                pos,
                Direction.Axis.X
        ).isPresent();
    }

    public static boolean trySpawnPortal(
            Level level,
            BlockPos pos
    ) {
        Optional<SiftPortalShape> optional = SiftPortalShape.findEmptyPortalShape(
                level,
                pos,
                Direction.Axis.X
        );

        optional.ifPresent(shape -> shape.createPortalBlocks(level));

        return optional.isPresent();
    }

    @Override
    protected BlockState updateShape(
            BlockState state,
            LevelReader level,
            ScheduledTickAccess ticks,
            BlockPos pos,
            Direction directionToNeighbour,
            BlockPos neighbourPos,
            BlockState neighbourState,
            RandomSource random
    ) {
        Direction.Axis updateAxis = directionToNeighbour.getAxis();
        Direction.Axis axis = state.getValue(AXIS);
        boolean wrongAxis = axis != updateAxis && updateAxis.isHorizontal();

        return !wrongAxis
                && !neighbourState.is(this)
                && !SiftPortalShape.findAnyShape(level, pos, axis).isComplete()
                ? Blocks.AIR.defaultBlockState()
                : super.updateShape(
                        state,
                        level,
                        ticks,
                        pos,
                        directionToNeighbour,
                        neighbourPos,
                        neighbourState,
                        random
                );
    }

    @Override
    @Nullable
    public TeleportTransition getPortalDestination(
            ServerLevel currentLevel,
            Entity entity,
            BlockPos portalEntryPos
    ) {
        ServerLevel destination = currentLevel.getServer().getLevel(
                currentLevel.dimension().equals(ModDimensions.SIFT)
                        ? Level.OVERWORLD
                        : ModDimensions.SIFT
        );

        if (destination == null) {
            return null;
        }

        boolean toSift = destination.dimension().equals(ModDimensions.SIFT);
        WorldBorder worldBorder = destination.getWorldBorder();
        double teleportationScale = DimensionType.getTeleportationScale(
                currentLevel.dimensionType(),
                destination.dimensionType()
        );
        BlockPos approximateExitPos = worldBorder.clampToBounds(
                entity.getX() * teleportationScale,
                entity.getY(),
                entity.getZ() * teleportationScale
        );

        return getExitPortal(
                destination,
                entity,
                portalEntryPos,
                approximateExitPos,
                toSift,
                worldBorder
        );
    }

    @Nullable
    private TeleportTransition getExitPortal(
            ServerLevel destination,
            Entity entity,
            BlockPos portalEntryPos,
            BlockPos approximateExitPos,
            boolean toSift,
            WorldBorder worldBorder
    ) {
        Optional<BlockPos> exitPortalPos = new SiftTeleporter(destination)
                .findClosestPortalPosition(
                        approximateExitPos,
                        toSift,
                        worldBorder
                );
        BlockUtil.FoundRectangle exitPortal;
        TeleportTransition.PostTeleportTransition postTeleportTransition;

        if (exitPortalPos.isPresent()) {
            BlockPos pos = exitPortalPos.get();
            BlockState portalState = destination.getBlockState(pos);
            exitPortal = BlockUtil.getLargestRectangleAround(
                    pos,
                    portalState.getValue(BlockStateProperties.HORIZONTAL_AXIS),
                    SiftPortalShape.MAX_WIDTH,
                    Direction.Axis.Y,
                    SiftPortalShape.MAX_HEIGHT,
                    blockPos -> destination.getBlockState(blockPos) == portalState
            );
            postTeleportTransition = TeleportTransition.PLAY_PORTAL_SOUND
                    .then(teleportedEntity -> teleportedEntity.placePortalTicket(pos));
        } else {
            Direction.Axis sourcePortalAxis = entity.level()
                    .getBlockState(portalEntryPos)
                    .getOptionalValue(AXIS)
                    .orElse(Direction.Axis.X);
            SiftTeleporter teleporter = new SiftTeleporter(destination);
            Optional<BlockUtil.FoundRectangle> createdExit = toSift
                    ? teleporter.createGatewayPortal(approximateExitPos, sourcePortalAxis)
                    : teleporter.createPortal(approximateExitPos, sourcePortalAxis);

            if (createdExit.isEmpty()) {
                Wellspring.LOGGER.error(
                        "Unable to create a Sift portal near {} in {}",
                        approximateExitPos,
                        destination.dimension().identifier()
                );
                return null;
            }

            exitPortal = createdExit.get();
            postTeleportTransition = TeleportTransition.PLAY_PORTAL_SOUND
                    .then(TeleportTransition.PLACE_PORTAL_TICKET);
        }

        return getDimensionTransitionFromExit(
                entity,
                portalEntryPos,
                exitPortal,
                destination,
                postTeleportTransition
        );
    }

    private static TeleportTransition getDimensionTransitionFromExit(
            Entity entity,
            BlockPos portalEntryPos,
            BlockUtil.FoundRectangle exitPortal,
            ServerLevel destination,
            TeleportTransition.PostTeleportTransition postTeleportTransition
    ) {
        BlockState blockState = entity.level().getBlockState(portalEntryPos);
        Direction.Axis axis;
        Vec3 offset;

        if (blockState.hasProperty(BlockStateProperties.HORIZONTAL_AXIS)) {
            axis = blockState.getValue(BlockStateProperties.HORIZONTAL_AXIS);
            BlockUtil.FoundRectangle portalArea = BlockUtil.getLargestRectangleAround(
                    portalEntryPos,
                    axis,
                    SiftPortalShape.MAX_WIDTH,
                    Direction.Axis.Y,
                    SiftPortalShape.MAX_HEIGHT,
                    pos -> entity.level().getBlockState(pos) == blockState
            );
            offset = entity.getRelativePortalPosition(axis, portalArea);
        } else {
            axis = Direction.Axis.X;
            offset = new Vec3(0.5, 0.0, 0.0);
        }

        return createDimensionTransition(
                destination,
                exitPortal,
                axis,
                offset,
                entity,
                postTeleportTransition
        );
    }

    private static TeleportTransition createDimensionTransition(
            ServerLevel destination,
            BlockUtil.FoundRectangle foundRectangle,
            Direction.Axis sourcePortalAxis,
            Vec3 offset,
            Entity entity,
            TeleportTransition.PostTeleportTransition postTeleportTransition
    ) {
        BlockPos bottomLeft = foundRectangle.minCorner;
        BlockState blockState = destination.getBlockState(bottomLeft);
        Direction.Axis targetPortalAxis = blockState
                .getOptionalValue(BlockStateProperties.HORIZONTAL_AXIS)
                .orElse(Direction.Axis.X);
        double width = foundRectangle.axis1Size;
        double height = foundRectangle.axis2Size;
        EntityDimensions dimensions = entity.getDimensions(entity.getPose());
        int outputRotation = sourcePortalAxis == targetPortalAxis ? 0 : 90;
        double offsetRight = dimensions.width() / 2.0
                + (width - dimensions.width()) * offset.x();
        double offsetUp = (height - dimensions.height()) * offset.y();
        double offsetForward = 0.5 + offset.z();
        boolean xAligned = targetPortalAxis == Direction.Axis.X;
        Vec3 targetPos = new Vec3(
                bottomLeft.getX() + (xAligned ? offsetRight : offsetForward),
                bottomLeft.getY() + offsetUp,
                bottomLeft.getZ() + (xAligned ? offsetForward : offsetRight)
        );
        Vec3 collisionFreePos = SiftPortalShape.findCollisionFreePosition(
                targetPos,
                destination,
                entity,
                dimensions
        );

        return new TeleportTransition(
                destination,
                collisionFreePos,
                Vec3.ZERO,
                outputRotation,
                0.0F,
                Relative.union(Relative.DELTA, Relative.ROTATION),
                postTeleportTransition
        );
    }

    @Override
    public int getPortalTransitionTime(
            ServerLevel level,
            Entity entity
    ) {
        return 80;
    }

    @Override
    public Portal.Transition getLocalTransition() {
        return Portal.Transition.NONE;
    }

    @Override
    public void animateTick(
            BlockState state,
            Level level,
            BlockPos pos,
            RandomSource random
    ) {
        if (random.nextInt(100) == 0) {
            level.playLocalSound(
                    pos.getX() + 0.5D,
                    pos.getY() + 0.5D,
                    pos.getZ() + 0.5D,
                    SoundEvents.PORTAL_AMBIENT,
                    SoundSource.BLOCKS,
                    0.5F,
                    random.nextFloat() * 0.4F + 0.8F,
                    false
            );
        }

        for (int i = 0; i < 4; i++) {
            double x = pos.getX() + random.nextDouble();
            double y = pos.getY() + random.nextDouble();
            double z = pos.getZ() + random.nextDouble();
            int side = random.nextBoolean() ? 1 : -1;

            if (state.getValue(AXIS) == Direction.Axis.X) {
                z = pos.getZ() + 0.5D + 0.25D * side;
            } else {
                x = pos.getX() + 0.5D + 0.25D * side;
            }

            level.addParticle(
                    ParticleTypes.SCULK_SOUL,
                    x,
                    y,
                    z,
                    0.0D,
                    0.03D,
                    0.0D
            );
        }
    }
}
