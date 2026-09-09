package com.sydders.wellspring.worldgen.tree;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacerType;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class WitheredTrunkPlacer extends TrunkPlacer {
    public static final MapCodec<WitheredTrunkPlacer> CODEC = RecordCodecBuilder.mapCodec(instance ->
            trunkPlacerParts(instance).apply(instance, WitheredTrunkPlacer::new));

    public WitheredTrunkPlacer(int baseHeight, int heightRandA, int heightRandB) {
        super(baseHeight, heightRandA, heightRandB);
    }

    @Override
    protected TrunkPlacerType<?> type() {
        return ModTrunkPlacerTypes.WITHERED.get();
    }

    @Override
    public List<FoliagePlacer.FoliageAttachment> placeTrunk(WorldGenLevel level, BiConsumer<BlockPos, BlockState> blockSetter,
                                                            RandomSource random, int treeHeight, BlockPos pos,
                                                            TreeConfiguration config) {
        List<FoliagePlacer.FoliageAttachment> attachments = new ArrayList<>();
        placeBelowTrunkBlock(level, blockSetter, random, pos.below(), config);

        for (int y = 0; y < treeHeight; y++) {
            placeLog(level, blockSetter, random, pos.above(y), config);
        }

        int branchCount = 3 + random.nextInt(3);
        int minBranchY = Math.max(2, treeHeight / 3);
        for (int i = 0; i < branchCount; i++) {
            int startY = minBranchY + random.nextInt(Math.max(1, treeHeight - minBranchY - 1));
            Direction direction = Direction.Plane.HORIZONTAL.getRandomDirection(random);
            int length = 4 + random.nextInt(4);
            BlockPos end = placeWindingBranch(level, blockSetter, random, pos.above(startY), config, direction, length, true);
            attachments.add(new FoliagePlacer.FoliageAttachment(end, 0, false));
        }

        attachments.add(new FoliagePlacer.FoliageAttachment(pos.above(treeHeight), 0, false));
        return attachments;
    }

    private BlockPos placeWindingBranch(WorldGenLevel level, BiConsumer<BlockPos, BlockState> blockSetter, RandomSource random,
                                        BlockPos start, TreeConfiguration config, Direction direction, int length,
                                        boolean canFork) {
        BlockPos current = start;
        Direction currentDirection = direction;

        for (int step = 1; step <= length; step++) {
            if (step > 1 && random.nextFloat() < 0.35f) {
                currentDirection = random.nextBoolean() ? currentDirection.getClockWise() : currentDirection.getCounterClockWise();
            }

            int yStep = step == 1 || random.nextFloat() < 0.55f ? 1 : 0;
            current = current.relative(currentDirection).above(yStep);
            placeLog(level, blockSetter, random, current, config, withAxis(currentDirection.getAxis()));

            if (canFork && step >= 2 && step <= length - 2 && random.nextFloat() < 0.28f) {
                Direction forkDirection = random.nextBoolean() ? currentDirection.getClockWise() : currentDirection.getCounterClockWise();
                placeWindingBranch(level, blockSetter, random, current, config, forkDirection, 2 + random.nextInt(3), false);
            }
        }

        return current;
    }

    private Function<BlockState, BlockState> withAxis(Direction.Axis axis) {
        return state -> state.hasProperty(RotatedPillarBlock.AXIS) ? state.setValue(RotatedPillarBlock.AXIS, axis) : state;
    }
}
