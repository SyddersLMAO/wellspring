package com.sydders.wellspring.block.custom;

import com.mojang.serialization.MapCodec;
import com.sydders.wellspring.portal.SiftPortalManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Portal;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.portal.PortalShape;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class SiftPortalBlock extends Block implements Portal {

    public static final MapCodec<SiftPortalBlock> CODEC =
            simpleCodec(SiftPortalBlock::new);

    public static final EnumProperty<Direction.Axis> AXIS =
            BlockStateProperties.HORIZONTAL_AXIS;

    private static final VoxelShape X_SHAPE =
            Block.box(0, 0, 6, 16, 16, 10);

    private static final VoxelShape Z_SHAPE =
            Block.box(6, 0, 0, 10, 16, 16);

    public SiftPortalBlock(Properties properties) {
        super(properties);

        registerDefaultState(
                stateDefinition.any()
                        .setValue(AXIS, Direction.Axis.X)
        );
    }

    @Override
    public MapCodec<SiftPortalBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(
            StateDefinition.Builder<Block, BlockState> builder
    ) {
        builder.add(AXIS);
    }

    @Override
    protected VoxelShape getShape(
            BlockState state,
            BlockGetter level,
            BlockPos pos,
            CollisionContext context
    ) {
        return state.getValue(AXIS) == Direction.Axis.X
                ? X_SHAPE
                : Z_SHAPE;
    }

    @Override
    protected BlockState rotate(
            BlockState state,
            Rotation rotation
    ) {
        return switch (rotation) {
            case CLOCKWISE_90, COUNTERCLOCKWISE_90 -> state.setValue(
                    AXIS,
                    state.getValue(AXIS) == Direction.Axis.X
                            ? Direction.Axis.Z
                            : Direction.Axis.X
            );
            default -> state;
        };
    }

    @Override
    protected void entityInside(
            BlockState state,
            Level level,
            BlockPos pos,
            Entity entity,
            InsideBlockEffectApplier effectApplier,
            boolean isPrecise
    ) {
        if (entity.canUsePortal(false)) {
            entity.setAsInsidePortal(this, pos);
        }
    }

    @Override
    public int getPortalTransitionTime(
            ServerLevel level,
            Entity entity
    ) {
        return 80;
    }

    @Override
    public Transition getLocalTransition() {
        return Transition.NONE;
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

            level.addParticle(ParticleTypes.SCULK_SOUL, x, y, z, 0.0D, 0.03D, 0.0D);
        }
    }

    @Nullable
    @Override
    public TeleportTransition getPortalDestination(
            ServerLevel currentLevel,
            Entity entity,
            BlockPos portalEntryPos
    ) {

        var endpoint = SiftPortalManager.findOrCreateDestination(
                currentLevel,
                portalEntryPos
        );

        if (endpoint.isEmpty()) {
            return null;
        }

        ServerLevel destination = currentLevel.getServer()
                .getLevel(endpoint.get().dimension());

        if (destination == null) {
            return null;
        }

        Vec3 target = PortalShape.findCollisionFreePosition(
                Vec3.atBottomCenterOf(endpoint.get().position()),
                destination,
                entity,
                entity.getDimensions(entity.getPose())
        );

        return new TeleportTransition(
                destination,
                target,
                Vec3.ZERO,
                entity.getYRot(),
                entity.getXRot(),
                TeleportTransition.PLAY_PORTAL_SOUND
                        .then(TeleportTransition.PLACE_PORTAL_TICKET)
        );
    }
}
