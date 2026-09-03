package com.sydders.wellspring.block;

import com.sydders.wellspring.Wellspring;
import com.sydders.wellspring.block.custom.ModFlammableRotatedPillarBlock;
import com.sydders.wellspring.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.UntintedParticleLeavesBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Function;

public class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS =
            DeferredRegister.createBlocks(Wellspring.MODID);

    public static final DeferredBlock<Block> SIFT_LOG = registerBlock("sift_log",
            properties -> new ModFlammableRotatedPillarBlock(
                    properties.strength(2f).sound(SoundType.NETHER_WOOD).ignitedByLava()));
    public static final DeferredBlock<Block> SIFT_WOOD = registerBlock("sift_wood",
            properties -> new ModFlammableRotatedPillarBlock(
                    properties.strength(2f).sound(SoundType.NETHER_WOOD).ignitedByLava()));
    public static final DeferredBlock<Block> STRIPPED_SIFT_LOG = registerBlock("stripped_sift_log",
            properties -> new ModFlammableRotatedPillarBlock(
                    properties.strength(2f).sound(SoundType.NETHER_WOOD).ignitedByLava()));
    public static final DeferredBlock<Block> STRIPPED_SIFT_WOOD = registerBlock("stripped_sift_wood",
            properties -> new ModFlammableRotatedPillarBlock(
                    properties.strength(2f).sound(SoundType.NETHER_WOOD).ignitedByLava()));

    public static final DeferredBlock<Block> SIFT_PLANKS = registerBlock("sift_planks",
            properties -> new Block(properties.mapColor(MapColor.WOOD).instrument(NoteBlockInstrument.BASS)
                    .strength(2.0F, 3.0F).sound(SoundType.NETHER_WOOD).ignitedByLava()) {
                @Override
                public boolean isFlammable(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
                    return true;
                }

                @Override
                public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
                    return 5;
                }

                @Override
                public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
                    return 5;
                }
            });
    public static final DeferredBlock<Block> SIFT_LEAVES = registerBlock("sift_leaves",
            properties -> new UntintedParticleLeavesBlock(0.01f, ParticleTypes.CHERRY_LEAVES,
                    properties.mapColor(MapColor.COLOR_GREEN).strength(2.0F).randomTicks().sound(SoundType.GRASS)
                            .noOcclusion().isValidSpawn(Blocks::ocelotOrParrot).ignitedByLava().pushReaction(PushReaction.DESTROY)) {
                @Override
                public boolean isFlammable(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
                    return true;
                }

                @Override
                public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
                    return 5;
                }

                @Override
                public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
                    return 5;
                }
            });

    private static <T extends Block> DeferredBlock<T> registerBlock(String name, Function<BlockBehaviour.Properties, T> function) {
        DeferredBlock<T> toReturn = BLOCKS.registerBlock(name, function);
        registerBlockItem(name, toReturn);
        return toReturn;
    }

    private static <T extends Block> void registerBlockItem(String name, DeferredBlock<T> block) {
        ModItems.ITEMS.registerItem(name, properties -> new BlockItem(block.get(), properties.useBlockDescriptionPrefix()));
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
