package com.sydders.wellspring.portal;

import com.google.common.collect.ImmutableSet;
import com.sydders.wellspring.Wellspring;
import com.sydders.wellspring.block.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.BlockUtil;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiRecord;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.NetherPortalBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.neoforge.registries.RegisterEvent;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class SiftTeleporter {
    private static final ResourceKey<PoiType> SIFT_PORTAL_POI = ResourceKey.create(
            Registries.POINT_OF_INTEREST_TYPE,
            Identifier.fromNamespaceAndPath(Wellspring.MODID, "sift_portal")
    );
    private static final Identifier SIFT_GATEWAY =
            Identifier.fromNamespaceAndPath(Wellspring.MODID, "sift_gateway");
    private static final int SEARCH_RADIUS_TO_SIFT = 16;
    private static final int SEARCH_RADIUS_TO_OVERWORLD = 128;

    private static Holder<PoiType> poi;

    private final ServerLevel level;

    public SiftTeleporter(ServerLevel level) {
        this.level = level;
    }

    public static void registerPointOfInterest(RegisterEvent event) {
        event.register(Registries.POINT_OF_INTEREST_TYPE, registerHelper -> {
            PoiType poiType = new PoiType(
                    ImmutableSet.copyOf(
                            ModBlocks.SIFT_PORTAL.get()
                                    .getStateDefinition()
                                    .getPossibleStates()
                    ),
                    0,
                    1
            );

            registerHelper.register(SIFT_PORTAL_POI.identifier(), poiType);
            poi = BuiltInRegistries.POINT_OF_INTEREST_TYPE.wrapAsHolder(poiType);
        });
    }

    public Optional<BlockPos> findClosestPortalPosition(
            BlockPos approximateExitPos,
            boolean toSift,
            WorldBorder worldBorder
    ) {
        if (poi == null) {
            return Optional.empty();
        }

        PoiManager poiManager = level.getPoiManager();
        int radius = toSift
                ? SEARCH_RADIUS_TO_SIFT
                : SEARCH_RADIUS_TO_OVERWORLD;

        poiManager.ensureLoadedAndValid(level, approximateExitPos, radius);

        return poiManager
                .getInSquare(
                        type -> type.is(SIFT_PORTAL_POI),
                        approximateExitPos,
                        radius,
                        PoiManager.Occupancy.ANY
                )
                .map(PoiRecord::getPos)
                .filter(worldBorder::isWithinBounds)
                .filter(pos -> level.getBlockState(pos)
                        .hasProperty(BlockStateProperties.HORIZONTAL_AXIS))
                .min(Comparator
                        .<BlockPos>comparingDouble(pos -> pos.distSqr(approximateExitPos))
                        .thenComparingInt(Vec3i::getY));
    }

    public Optional<BlockUtil.FoundRectangle> createGatewayPortal(
            BlockPos origin,
            Direction.Axis portalAxis
    ) {
        Optional<StructureTemplate> template = level
                .getServer()
                .getStructureManager()
                .get(SIFT_GATEWAY);

        if (template.isEmpty()) {
            Wellspring.LOGGER.error("Missing Sift gateway structure: {}", SIFT_GATEWAY);
            return Optional.empty();
        }

        Rotation rotation = portalAxis == Direction.Axis.X
                ? Rotation.NONE
                : Rotation.CLOCKWISE_90;
        StructurePlaceSettings settings = new StructurePlaceSettings()
                .setMirror(Mirror.NONE)
                .setRotation(rotation)
                .setIgnoreEntities(false)
                .setKnownShape(true);
        Vec3i size = template.get().getSize(rotation);
        BlockPos clampedOrigin = level.getWorldBorder().clampToBounds(origin);
        BlockPos placementOrigin = findGatewayPlacementOrigin(
                clampedOrigin,
                size
        );
        List<StructureTemplate.StructureBlockInfo> portalBlocks = template.get()
                .filterBlocks(
                        placementOrigin,
                        settings,
                        ModBlocks.SIFT_PORTAL.get()
                );

        if (portalBlocks.isEmpty()) {
            Wellspring.LOGGER.error(
                    "Sift gateway structure {} contains no Sift portal blocks",
                    SIFT_GATEWAY
            );
            return Optional.empty();
        }

        boolean placed = template.get().placeInWorld(
                level,
                placementOrigin,
                placementOrigin,
                settings,
                RandomSource.create(),
                Block.UPDATE_CLIENTS | Block.UPDATE_KNOWN_SHAPE
        );

        if (!placed) {
            Wellspring.LOGGER.error(
                    "Failed to place Sift gateway structure near {} in {}",
                    origin,
                    level.dimension().identifier()
            );
            return Optional.empty();
        }

        return placeGatewayPortalBlocks(portalBlocks);
    }

    public Optional<BlockUtil.FoundRectangle> createPortal(
            BlockPos origin,
            Direction.Axis portalAxis
    ) {
        Direction direction = Direction.get(
                Direction.AxisDirection.POSITIVE,
                portalAxis
        );
        double closestFullDistanceSqr = -1.0;
        BlockPos closestFullPosition = null;
        double closestPartialDistanceSqr = -1.0;
        BlockPos closestPartialPosition = null;
        WorldBorder worldBorder = level.getWorldBorder();
        int maxPlaceableY = Math.min(
                level.getMaxY(),
                level.getMinY() + level.getLogicalHeight() - 1
        );
        BlockPos.MutableBlockPos mutable = origin.mutable();

        for (BlockPos.MutableBlockPos columnPos : BlockPos.spiralAround(
                origin,
                16,
                Direction.EAST,
                Direction.SOUTH
        )) {
            int height = Math.min(
                    maxPlaceableY,
                    level.getHeight(
                            Heightmap.Types.MOTION_BLOCKING,
                            columnPos.getX(),
                            columnPos.getZ()
                    )
            );

            if (worldBorder.isWithinBounds(columnPos)
                    && worldBorder.isWithinBounds(columnPos.move(direction, 1))) {
                columnPos.move(direction.getOpposite(), 1);

                for (int y = height; y >= level.getMinY(); y--) {
                    columnPos.setY(y);

                    if (canPortalReplaceBlock(columnPos)) {
                        int firstEmptyY = y;

                        while (y > level.getMinY()
                                && canPortalReplaceBlock(columnPos.move(Direction.DOWN))) {
                            y--;
                        }

                        if (y + 4 <= maxPlaceableY) {
                            int deltaY = firstEmptyY - y;

                            if ((deltaY <= 0 || deltaY >= 3)
                                    && canHostFrame(columnPos.setY(y), mutable, direction, 0)) {
                                double distance = origin.distSqr(columnPos);

                                if (canHostFrame(columnPos, mutable, direction, -1)
                                        && canHostFrame(columnPos, mutable, direction, 1)
                                        && (closestFullDistanceSqr == -1.0
                                        || closestFullDistanceSqr > distance)) {
                                    closestFullDistanceSqr = distance;
                                    closestFullPosition = columnPos.immutable();
                                }

                                if (closestFullDistanceSqr == -1.0
                                        && (closestPartialDistanceSqr == -1.0
                                        || closestPartialDistanceSqr > distance)) {
                                    closestPartialDistanceSqr = distance;
                                    closestPartialPosition = columnPos.immutable();
                                }
                            }
                        }
                    }
                }
            }
        }

        if (closestFullDistanceSqr == -1.0
                && closestPartialDistanceSqr != -1.0) {
            closestFullPosition = closestPartialPosition;
            closestFullDistanceSqr = closestPartialDistanceSqr;
        }

        if (closestFullDistanceSqr == -1.0) {
            int minStartY = Math.max(level.getMinY() + 1, 70);
            int maxStartY = maxPlaceableY - 9;

            if (maxStartY < minStartY) {
                return Optional.empty();
            }

            closestFullPosition = new BlockPos(
                    origin.getX() - direction.getStepX(),
                    Mth.clamp(origin.getY(), minStartY, maxStartY),
                    origin.getZ() - direction.getStepZ()
            ).immutable();
            closestFullPosition = worldBorder
                    .clampToBounds(closestFullPosition)
                    .immutable();
            Direction clockwise = direction.getClockWise();

            for (int box = -1; box < 2; box++) {
                for (int width = 0; width < 2; width++) {
                    for (int height = -1; height < 3; height++) {
                        BlockState blockState = height < 0
                                ? Blocks.REINFORCED_DEEPSLATE.defaultBlockState()
                                : Blocks.AIR.defaultBlockState();

                        mutable.setWithOffset(
                                closestFullPosition,
                                width * direction.getStepX()
                                        + box * clockwise.getStepX(),
                                height,
                                width * direction.getStepZ()
                                        + box * clockwise.getStepZ()
                        );
                        level.setBlockAndUpdate(mutable, blockState);
                    }
                }
            }
        }

        for (int width = -1; width < 3; width++) {
            for (int height = -1; height < 4; height++) {
                if (width == -1 || width == 2 || height == -1 || height == 3) {
                    mutable.setWithOffset(
                            closestFullPosition,
                            width * direction.getStepX(),
                            height,
                            width * direction.getStepZ()
                    );
                    level.setBlock(
                            mutable,
                            Blocks.REINFORCED_DEEPSLATE.defaultBlockState(),
                            3
                    );
                }
            }
        }

        BlockState portalBlockState = ModBlocks.SIFT_PORTAL.get()
                .defaultBlockState()
                .setValue(NetherPortalBlock.AXIS, portalAxis);

        for (int width = 0; width < 2; width++) {
            for (int height = 0; height < 3; height++) {
                mutable.setWithOffset(
                        closestFullPosition,
                        width * direction.getStepX(),
                        height,
                        width * direction.getStepZ()
                );
                level.setBlock(mutable, portalBlockState, 18);

                if (poi != null) {
                    level.getPoiManager().add(mutable, poi);
                }
            }
        }

        return Optional.of(new BlockUtil.FoundRectangle(
                closestFullPosition.immutable(),
                2,
                3
        ));
    }

    private BlockPos findGatewayPlacementOrigin(
            BlockPos center,
            Vec3i size
    ) {
        int groundY = findTopSolidGround(
                level,
                center.getX(),
                center.getZ()
        );
        int maxOriginY = level.getMaxY() - size.getY();
        int y = Mth.clamp(
                groundY,
                level.getMinY(),
                maxOriginY
        );

        return new BlockPos(
                center.getX() - size.getX() / 2,
                y,
                center.getZ() - size.getZ() / 2
        );
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

    private Optional<BlockUtil.FoundRectangle> placeGatewayPortalBlocks(
            List<StructureTemplate.StructureBlockInfo> portalBlocks
    ) {
        BlockPos firstPortalBlock = null;

        for (StructureTemplate.StructureBlockInfo portalBlock : portalBlocks) {
            if (!portalBlock.state()
                    .hasProperty(BlockStateProperties.HORIZONTAL_AXIS)) {
                continue;
            }

            BlockPos pos = portalBlock.pos();
            level.setBlock(
                    pos,
                    portalBlock.state(),
                    Block.UPDATE_CLIENTS | Block.UPDATE_KNOWN_SHAPE
            );

            if (poi != null) {
                level.getPoiManager().add(pos.immutable(), poi);
            }

            if (firstPortalBlock == null) {
                firstPortalBlock = pos.immutable();
            }
        }

        if (firstPortalBlock == null) {
            Wellspring.LOGGER.error(
                    "Sift gateway structure {} contains no valid Sift portal blocks",
                    SIFT_GATEWAY
            );
            return Optional.empty();
        }

        Direction.Axis axis = level
                .getBlockState(firstPortalBlock)
                .getValue(BlockStateProperties.HORIZONTAL_AXIS);

        return Optional.of(BlockUtil.getLargestRectangleAround(
                firstPortalBlock,
                axis,
                SiftPortalShape.MAX_WIDTH,
                Direction.Axis.Y,
                SiftPortalShape.MAX_HEIGHT,
                pos -> level.getBlockState(pos).is(ModBlocks.SIFT_PORTAL.get())
        ));
    }

    private boolean canHostFrame(
            BlockPos origin,
            BlockPos.MutableBlockPos mutable,
            Direction direction,
            int offset
    ) {
        Direction clockwise = direction.getClockWise();

        for (int width = -1; width < 3; width++) {
            for (int height = -1; height < 4; height++) {
                mutable.setWithOffset(
                        origin,
                        direction.getStepX() * width
                                + clockwise.getStepX() * offset,
                        height,
                        direction.getStepZ() * width
                                + clockwise.getStepZ() * offset
                );

                if (height < 0 && !level.getBlockState(mutable).isSolid()) {
                    return false;
                }

                if (height >= 0 && !canPortalReplaceBlock(mutable)) {
                    return false;
                }
            }
        }

        return true;
    }

    private boolean canPortalReplaceBlock(BlockPos.MutableBlockPos pos) {
        BlockState blockState = level.getBlockState(pos);

        return blockState.canBeReplaced()
                && blockState.getFluidState().isEmpty();
    }
}
