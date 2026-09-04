package com.sydders.wellspring.event;

import com.sydders.wellspring.Wellspring;
import com.sydders.wellspring.block.ModBlocks;
import net.minecraft.client.color.block.BlockTintSource;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.GrassColor;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;

import java.util.List;

@EventBusSubscriber(modid = Wellspring.MODID, value = Dist.CLIENT)
public class BlockColorRegister {

    @SubscribeEvent
    public static void registerBlockColorHandlers(RegisterColorHandlersEvent.BlockTintSources event) {
        event.register(
                List.of(
                        new BlockTintSource() {
                            @Override
                            public int color(BlockState state) {
                                return GrassColor.getDefaultColor(); // fallback, no world context
                            }

                            @Override
                            public int colorInWorld(BlockState state, BlockAndTintGetter level, BlockPos pos) {
                                return BiomeColors.getAverageGrassColor(level, pos);
                            }
                        }
                ),
                ModBlocks.SIFT_GRASS_BLOCK.get()
        );
    }
}