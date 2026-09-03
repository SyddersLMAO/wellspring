package com.sydders.wellspring;

import com.sydders.wellspring.datagen.ModBlockTagsProvider;
import com.sydders.wellspring.datagen.ModModelProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;


@EventBusSubscriber(modid = Wellspring.MODID)
public class WellspringDatagen {
    @SubscribeEvent
    public static void gatherClientData(GatherDataEvent.Client event) {
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();
        var lookupProvider = event.getLookupProvider();

        // Model provider
        generator.addProvider(true, new ModModelProvider(packOutput));

        // Tags providers
        generator.addProvider(true, new ModBlockTagsProvider(packOutput, lookupProvider));
    }
}
