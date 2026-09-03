package com.sydders.wellspring.item;

import com.sydders.wellspring.Wellspring;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Wellspring.MODID);

    public static final DeferredItem<Item> RUBY = ITEMS.registerSimpleItem("ruby");

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
