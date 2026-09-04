package com.sydders.wellspring.portal;

import com.sydders.wellspring.block.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

public final class SiftPortalManager {

    private static final Identifier SIFT_GATEWAY =
            Identifier.fromNamespaceAndPath(
                    "wellspring",
                    "sift_gateway"
            );

    private SiftPortalManager() {
    }

    public static BlockPos createDestinationPortal(
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

        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int minZ = Integer.MAX_VALUE;

        int maxX = Integer.MIN_VALUE;
        int maxY = Integer.MIN_VALUE;
        int maxZ = Integer.MIN_VALUE;

        boolean found = false;

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

            if (!level.getBlockState(pos)
                    .is(ModBlocks.SIFT_PORTAL.get())) {
                continue;
            }

            found = true;

            minX = Math.min(minX, pos.getX());
            minY = Math.min(minY, pos.getY());
            minZ = Math.min(minZ, pos.getZ());

            maxX = Math.max(maxX, pos.getX());
            maxY = Math.max(maxY, pos.getY());
            maxZ = Math.max(maxZ, pos.getZ());
        }

        if (!found) {
            throw new IllegalStateException(
                    "The Sift gateway structure contains no SIFT_PORTAL blocks"
            );
        }

        return new BlockPos(
                (minX + maxX) / 2,
                minY + 1,
                (minZ + maxZ) / 2
        );
    }
}
