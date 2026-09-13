package com.sydders.wellspring.worldgen.structure;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

/** Terrain checks for the anchor template's five-block-wide, five-block-high doorway. */
final class CaveWallEntrance {
    private CaveWallEntrance() {
    }

    @FunctionalInterface
    interface Terrain {
        BlockState get(BlockPos pos);
    }

    static BoundingBox approachBox(BlockPos floor, Direction outward) {
        Direction sideways = outward.getClockWise();
        return BoundingBox.fromCorners(floor.relative(outward).relative(sideways, -2).above(),
                floor.relative(outward, 3).relative(sideways, 2).above(5));
    }

    static boolean fits(Terrain terrain, BlockPos floor, Direction outward) {
        Direction sideways = outward.getClockWise();

        // The outside must be a walkable approach, not a small air pocket or a fluid cavity.
        for (int depth = 1; depth <= 3; depth++) {
            for (int width = -2; width <= 2; width++) {
                BlockPos approach = floor.relative(outward, depth).relative(sideways, width);
                if (!isSolid(terrain.get(approach)) && !isSolid(terrain.get(approach.below()))) {
                    return false;
                }
                for (int height = 1; height <= 5; height++) {
                    if (!terrain.get(approach.above(height)).isAir()) {
                        return false;
                    }
                }
            }
        }

        // Embed the back half of the 7 x 7 x 9 anchor in the wall.
        for (int depth : new int[]{4, 8}) {
            for (int width : new int[]{-3, 0, 3}) {
                for (int height : new int[]{1, 3, 5}) {
                    BlockPos inside = floor.relative(outward, -depth).relative(sideways, width).above(height);
                    if (!isSolid(terrain.get(inside))) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    static boolean isSolid(BlockState state) {
        return state.isSolid() && state.getFluidState().isEmpty();
    }
}
