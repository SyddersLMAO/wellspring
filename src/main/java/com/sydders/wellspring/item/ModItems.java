package com.sydders.wellspring.item;

import com.sydders.wellspring.Wellspring;
import com.sydders.wellspring.item.custom.WardenKeyItem;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Wellspring.MODID);

    public static final DeferredItem<Item> RUBY = ITEMS.registerSimpleItem("ruby");
    public static final DeferredItem<Item> WARDEN_KEY = ITEMS.registerItem("warden_key", WardenKeyItem::new);

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
