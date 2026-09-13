package com.sydders.wellspring.worldgen.structure;

import com.sydders.wellspring.Wellspring;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.FunctionGameTestInstance;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.gametest.framework.TestData;
import net.minecraft.gametest.framework.TestEnvironmentDefinition;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.JigsawBlock;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.RegisterGameTestsEvent;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Consumer;

public final class DungeonGameTests {
    private static final DeferredRegister<Consumer<GameTestHelper>> FUNCTIONS =
            DeferredRegister.create(BuiltInRegistries.TEST_FUNCTION, Wellspring.MODID);

    public static void register(IEventBus eventBus) {
        FUNCTIONS.register("dungeon_entrance_geometry", () -> DungeonGameTests::entranceGeometry);
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            FUNCTIONS.register("dungeon_sift_" + direction.getName(), () -> helper -> sampleSift(helper, 0, direction));
        }
        FUNCTIONS.register(eventBus);
        eventBus.addListener(DungeonGameTests::registerTests);
    }

    private static void registerTests(RegisterGameTestsEvent event) {
        Holder<TestEnvironmentDefinition<?>> environment = event.registerEnvironment(id("dungeon_tests"));
        for (String name : new String[]{"dungeon_entrance_geometry", "dungeon_sift_north", "dungeon_sift_south",
                "dungeon_sift_east", "dungeon_sift_west"}) {
            event.registerTest(id(name), new FunctionGameTestInstance(ResourceKey.create(Registries.TEST_FUNCTION, id(name)),
                    new TestData<>(environment, id("dungeon/anchor"), 1200, 0, true)));
        }
    }

    private static void entranceGeometry(GameTestHelper helper) {
        BlockPos floor = new BlockPos(-31, -32, 47);
        for (Direction outward : Direction.Plane.HORIZONTAL) {
            CaveWallEntrance.Terrain wall = pos -> {
                int depth = (pos.getX() - floor.getX()) * outward.getStepX()
                        + (pos.getZ() - floor.getZ()) * outward.getStepZ();
                return (pos.getY() <= floor.getY() || depth <= 0 ? Blocks.STONE : Blocks.AIR).defaultBlockState();
            };
            helper.assertTrue(CaveWallEntrance.fits(wall, floor, outward), "Valid wall rejected: " + outward);
            helper.assertFalse(CaveWallEntrance.fits(pos -> Blocks.STONE.defaultBlockState(), floor, outward), "Buried entrance accepted");
            helper.assertFalse(CaveWallEntrance.fits(pos -> Blocks.AIR.defaultBlockState(), floor, outward), "Floating entrance accepted");
            helper.assertFalse(CaveWallEntrance.fits(pos -> wall.get(pos).isAir() ? Blocks.WATER.defaultBlockState() : wall.get(pos),
                    floor, outward), "Flooded entrance accepted");
            BlockPos obstructed = floor.relative(outward, 2).relative(outward.getClockWise(), 2).above(4);
            helper.assertFalse(CaveWallEntrance.fits(pos -> pos.equals(obstructed) ? Blocks.STONE.defaultBlockState() : wall.get(pos),
                    floor, outward), "Partially blocked doorway accepted");
            helper.assertFalse(CaveWallEntrance.fits(pos -> pos.getY() <= floor.getY() ? Blocks.STONE.defaultBlockState() : Blocks.AIR.defaultBlockState(),
                    floor, outward), "Open floor without a wall accepted");
        }
        helper.succeed();
    }

    private static void sampleSift(GameTestHelper helper, int attempt, Direction requestedDirection) {
        ServerLevel level = helper.getLevel().getServer().getLevel(ResourceKey.create(Registries.DIMENSION, id("sift")));
        helper.assertTrue(level != null, "Sift dimension was not loaded");
        var registered = level.registryAccess().lookupOrThrow(Registries.STRUCTURE).getValue(id("dungeon"));
        helper.assertTrue(registered instanceof CaveWallDungeonStructure, "Dungeon did not load the cave wall structure type");
        CaveWallDungeonStructure dungeon = (CaveWallDungeonStructure) registered;
        var generator = level.getChunkSource().getGenerator();
        ChunkPos chunk = new ChunkPos(requestedDirection.getStepX() * 64 + attempt % 16,
                requestedDirection.getStepZ() * 64 + attempt / 16);
        Structure.GenerationContext context = new Structure.GenerationContext(level.registryAccess(), generator,
                generator.getBiomeSource(), level.getChunkSource().randomState(), level.getStructureManager(),
                level.getSeed(), chunk, level, dungeon.biomes()::contains);
        var generated = dungeon.findValidGenerationPoint(context);
        if (generated.isEmpty()) {
            helper.assertTrue(attempt < 255, "No accessible " + requestedDirection + " entrance found in 256 Sift chunks");
            helper.runAfterDelay(1, () -> sampleSift(helper, attempt + 1, requestedDirection));
            return;
        }
        var stub = generated.get();
        var pieces = stub.getPiecesBuilder().build().pieces();
        helper.assertTrue(pieces.size() > 1, "Dungeon generated only its anchor; hallway connections failed");
        var anchor = (PoolElementStructurePiece) pieces.getFirst();
        var entrance = anchor.getElement().getShuffledJigsawBlocks(level.getStructureManager(), anchor.getPosition(),
                anchor.getRotation(), context.random()).stream().filter(jigsaw -> jigsaw.name().equals(Identifier.withDefaultNamespace("empty")))
                .findFirst().orElseThrow();
        helper.assertTrue(entrance.info().pos().equals(stub.position()), "Entrance and structure position diverged during translation");
        Direction outward = JigsawBlock.getFrontFacing(entrance.info().state());
        if (outward != requestedDirection) {
            helper.assertTrue(attempt < 255, "No entrance facing " + requestedDirection + " found in 256 Sift chunks");
            helper.runAfterDelay(1, () -> sampleSift(helper, attempt + 1, requestedDirection));
            return;
        }
        var repeated = dungeon.findValidGenerationPoint(new Structure.GenerationContext(level.registryAccess(), generator,
                generator.getBiomeSource(), level.getChunkSource().randomState(), level.getStructureManager(),
                level.getSeed(), chunk, level, dungeon.biomes()::contains)).orElseThrow();
        helper.assertTrue(repeated.position().equals(stub.position()), "Entrance placement is not deterministic");
        var repeatedPieces = repeated.getPiecesBuilder().build().pieces();
        helper.assertTrue(repeatedPieces.size() == pieces.size(), "Dungeon layout is not deterministic");
        for (int index = 0; index < pieces.size(); index++) {
            var original = (PoolElementStructurePiece) pieces.get(index);
            var duplicate = (PoolElementStructurePiece) repeatedPieces.get(index);
            helper.assertTrue(original.getPosition().equals(duplicate.getPosition()) && original.getRotation() == duplicate.getRotation(),
                    "Room position or rotation changed for the same seed");
        }
        helper.assertTrue(CaveWallEntrance.fits(level::getBlockState, stub.position(), outward),
                "Predicted wall does not match generated Sift terrain");

        for (var piece : pieces) {
            var box = piece.getBoundingBox();
            for (int x = Math.floorDiv(box.minX(), 16); x <= Math.floorDiv(box.maxX(), 16); x++) {
                for (int z = Math.floorDiv(box.minZ(), 16); z <= Math.floorDiv(box.maxZ(), 16); z++) {
                    level.getChunk(x, z);
                }
            }
            ((PoolElementStructurePiece) piece).place(level, level.structureManager(), generator, context.random(),
                    BoundingBox.infinite(), stub.position(), false);
        }
        for (int depth = 0; depth <= 3; depth++) {
            for (int width = -2; width <= 2; width++) {
                for (int height = 1; height <= 5; height++) {
                    BlockPos pos = stub.position().relative(outward, depth).relative(outward.getClockWise(), width).above(height);
                    helper.assertTrue(level.getBlockState(pos).isAir(), "Placed dungeon blocks its entrance at " + pos);
                }
            }
        }
        Wellspring.LOGGER.info("Dungeon placement test: entrance={}, facing={}, pieces={}, attempts={}",
                stub.position(), outward, pieces.size(), attempt + 1);
        helper.succeed();
    }

    private static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(Wellspring.MODID, path);
    }
}
