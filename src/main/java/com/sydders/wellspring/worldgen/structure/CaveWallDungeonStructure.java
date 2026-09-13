package com.sydders.wellspring.worldgen.structure;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.block.JigsawBlock;
import net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.TerrainAdjustment;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;
import net.minecraft.world.level.levelgen.structure.pools.JigsawJunction;
import net.minecraft.world.level.levelgen.structure.pools.JigsawPlacement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.pools.alias.PoolAliasLookup;
import net.minecraft.world.level.levelgen.structure.structures.JigsawStructure;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class CaveWallDungeonStructure extends Structure {
    public static final MapCodec<CaveWallDungeonStructure> CODEC = RecordCodecBuilder.<CaveWallDungeonStructure>mapCodec(
            instance -> instance.group(
                    settingsCodec(instance),
                    StructureTemplatePool.CODEC.fieldOf("start_pool").forGetter(value -> value.startPool),
                    Identifier.CODEC.fieldOf("entrance_jigsaw").forGetter(value -> value.entranceJigsaw),
                    Codec.intRange(1, 20).fieldOf("size").forGetter(value -> value.size),
                    Codec.INT.fieldOf("min_y").forGetter(value -> value.minY),
                    Codec.INT.fieldOf("max_y").forGetter(value -> value.maxY),
                    Codec.intRange(1, 96).fieldOf("max_distance_from_center").forGetter(value -> value.maxDistance)
            ).apply(instance, CaveWallDungeonStructure::new)
    ).validate(value -> {
        if (value.minY > value.maxY) {
            return DataResult.error(() -> "min_y must not exceed max_y");
        }
        if (value.terrainAdaptation() != TerrainAdjustment.NONE) {
            return DataResult.error(() -> "Cave wall entrances require terrain_adaptation: none");
        }
        return DataResult.success(value);
    });

    private final Holder<StructureTemplatePool> startPool;
    private final Identifier entranceJigsaw;
    private final int size;
    private final int minY;
    private final int maxY;
    private final int maxDistance;

    public CaveWallDungeonStructure(StructureSettings settings, Holder<StructureTemplatePool> startPool,
                                    Identifier entranceJigsaw, int size, int minY, int maxY, int maxDistance) {
        super(settings);
        this.startPool = startPool;
        this.entranceJigsaw = entranceJigsaw;
        this.size = size;
        this.minY = minY;
        this.maxY = maxY;
        this.maxDistance = maxDistance;
    }

    @Override
    public Optional<GenerationStub> findGenerationPoint(GenerationContext context) {
        int lowestFloor = Math.max(minY, context.heightAccessor().getMinY() + 1);
        int highestFloor = Math.min(maxY, context.heightAccessor().getMaxY() - 7);
        if (lowestFloor > highestFloor) {
            return Optional.empty();
        }

        // Let vanilla assemble and rotate the rooms, then move the entire connected layout.
        BlockPos provisionalStart = context.chunkPos().getMiddleBlockPosition((lowestFloor + highestFloor) / 2);
        Optional<GenerationStub> layout = JigsawPlacement.addPieces(context, startPool, Optional.of(entranceJigsaw),
                size, provisionalStart, false, Optional.empty(), new JigsawStructure.MaxDistance(maxDistance),
                PoolAliasLookup.EMPTY, JigsawStructure.DEFAULT_DIMENSION_PADDING, JigsawStructure.DEFAULT_LIQUID_SETTINGS);
        if (layout.isEmpty()) {
            return Optional.empty();
        }
        StructurePiecesBuilder pieces = layout.get().getPiecesBuilder();
        if (pieces.isEmpty() || !(pieces.build().pieces().getFirst() instanceof PoolElementStructurePiece anchor)) {
            return Optional.empty();
        }
        Optional<StructureTemplate.JigsawBlockInfo> entrance = anchor.getElement()
                .getShuffledJigsawBlocks(context.structureTemplateManager(), anchor.getPosition(), anchor.getRotation(), context.random())
                .stream().filter(jigsaw -> jigsaw.name().equals(entranceJigsaw)).findFirst();
        if (entrance.isEmpty()) {
            return Optional.empty();
        }
        Direction outward = JigsawBlock.getFrontFacing(entrance.get().info().state());
        if (!outward.getAxis().isHorizontal()) {
            return Optional.empty();
        }
        BlockPos oldFloor = entrance.get().info().pos();
        var approachBox = CaveWallEntrance.approachBox(oldFloor, outward);
        if (pieces.build().pieces().stream().anyMatch(piece -> piece.getBoundingBox().intersects(approachBox))) {
            // A branching hallway may loop around the anchor. Keep the outside approach open.
            return Optional.empty();
        }
        // Also keep every room inside the dimension after translation.
        lowestFloor = Math.max(lowestFloor, oldFloor.getY() + context.heightAccessor().getMinY() - pieces.getBoundingBox().minY());
        highestFloor = Math.min(highestFloor, oldFloor.getY() + context.heightAccessor().getMaxY() - pieces.getBoundingBox().maxY());

        Optional<BlockPos> floor = findEntrance(context, outward, lowestFloor, highestFloor);
        if (floor.isEmpty()) {
            return Optional.empty();
        }
        BlockPos offset = floor.get().subtract(oldFloor);
        for (StructurePiece piece : pieces.build().pieces()) {
            piece.move(offset.getX(), offset.getY(), offset.getZ());
            if (piece instanceof PoolElementStructurePiece poolPiece) {
                var movedJunctions = poolPiece.getJunctions().stream().map(junction -> new JigsawJunction(
                        junction.getSourceX() + offset.getX(), junction.getSourceGroundY() + offset.getY(),
                        junction.getSourceZ() + offset.getZ(), junction.getDeltaY(), junction.getDestProjection())).toList();
                poolPiece.getJunctions().clear();
                movedJunctions.forEach(poolPiece::addJunction);
            }
        }
        return Optional.of(new GenerationStub(floor.get(), builder -> pieces.build().pieces().forEach(builder::addPiece)));
    }

    private Optional<BlockPos> findEntrance(GenerationContext context, Direction outward, int lowestFloor, int highestFloor) {
        // Sampling the generator avoids loading neighboring chunks during STRUCTURE_STARTS.
        // Cache columns only for this attempt; a seed or dimension must never share the cache.
        Map<Long, NoiseColumn> columns = new HashMap<>();
        CaveWallEntrance.Terrain terrain = pos -> columns.computeIfAbsent(((long) pos.getX() << 32) ^ (pos.getZ() & 0xffffffffL),
                key -> context.chunkGenerator().getBaseColumn(pos.getX(), pos.getZ(), context.heightAccessor(), context.randomState()))
                .getBlock(pos.getY());
        int first = context.random().nextInt(64);
        int xParity = context.random().nextInt(2);
        int zParity = context.random().nextInt(2);
        for (int attempt = 0; attempt < 64; attempt++) {
            int index = (first + attempt * 17) % 64;
            int x = context.chunkPos().getMinBlockX() + (index % 8) * 2 + xParity;
            int z = context.chunkPos().getMinBlockZ() + (index / 8) * 2 + zParity;
            for (int y = lowestFloor; y <= highestFloor; y++) {
                BlockPos approach = new BlockPos(x, y, z);
                if (CaveWallEntrance.isSolid(terrain.get(approach)) && terrain.get(approach.above()).isAir()) {
                    BlockPos floor = approach.relative(outward.getOpposite());
                    if (CaveWallEntrance.fits(terrain, floor, outward)) {
                        return Optional.of(floor);
                    }
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public StructureType<?> type() {
        return ModStructureTypes.CAVE_WALL_DUNGEON.get();
    }
}
