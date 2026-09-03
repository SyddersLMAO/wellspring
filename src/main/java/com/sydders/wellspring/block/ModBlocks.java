package com.sydders.wellspring.block;

import com.sydders.wellspring.Wellspring;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS =
            DeferredRegister.createBlocks(Wellspring.MODID);

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
