package com.sydders.wellspring.item;

import com.sydders.wellspring.Wellspring;
import com.sydders.wellspring.item.custom.WardenKeyItem;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.equipment.ArmorType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Wellspring.MODID);

    public static final DeferredItem<Item> RUBY = ITEMS.registerSimpleItem("ruby");
    public static final DeferredItem<Item> WARDEN_KEY = ITEMS.registerItem("warden_key", WardenKeyItem::new);

    public static final DeferredItem<Item> RAW_BAZULIUM = ITEMS.registerSimpleItem("raw_bazulium");
    public static final DeferredItem<Item> BAZULIUM_INGOT = ITEMS.registerSimpleItem("bazulium_ingot");
    public static final DeferredItem<Item> BAZULIUM_NUGGET = ITEMS.registerSimpleItem("bazulium_nugget");

    public static final DeferredItem<Item> BAZULIUM_SWORD = ITEMS.registerItem("bazulium_sword",
            properties -> new Item(properties.sword(ModToolTiers.BAZULIUM, 3, -2.4f)));
    public static final DeferredItem<Item> BAZULIUM_PICKAXE = ITEMS.registerItem("bazulium_pickaxe",
            properties -> new Item(properties.pickaxe(ModToolTiers.BAZULIUM, 1, -2.8f)));
    public static final DeferredItem<Item> BAZULIUM_SHOVEL = ITEMS.registerItem("bazulium_shovel",
            properties -> new ShovelItem(ModToolTiers.BAZULIUM, 1.5f, -3.0f, properties));
    public static final DeferredItem<Item> BAZULIUM_AXE = ITEMS.registerItem("bazulium_axe",
            properties -> new AxeItem(ModToolTiers.BAZULIUM, 3.5f, -2.5f, properties));
    public static final DeferredItem<Item> BAZULIUM_HOE = ITEMS.registerItem("bazulium_hoe",
            properties -> new HoeItem(ModToolTiers.BAZULIUM, 0, -3.0f, properties));
    public static final DeferredItem<Item> BAZULIUM_SPEAR = ITEMS.registerItem("bazulium_spear",
            properties -> new Item(properties.spear(ModToolTiers.BAZULIUM, 0.95f, 0.7f, 0.7f, 3.5f,
                    13f, 8.5f, 5.1f, 13.37f, 4.67f)));

    public static final DeferredItem<Item> BAZULIUM_HELMET = ITEMS.registerItem("bazulium_helmet",
            properties -> new Item(properties.humanoidArmor(ModArmorMaterials.BAZULIUM_ARMOR_MATERIAL, ArmorType.HELMET)));
    public static final DeferredItem<Item> BAZULIUM_CHESTPLATE = ITEMS.registerItem("bazulium_chestplate",
            properties -> new Item(properties.humanoidArmor(ModArmorMaterials.BAZULIUM_ARMOR_MATERIAL, ArmorType.CHESTPLATE)));
    public static final DeferredItem<Item> BAZULIUM_LEGGINGS = ITEMS.registerItem("bazulium_leggings",
            properties -> new Item(properties.humanoidArmor(ModArmorMaterials.BAZULIUM_ARMOR_MATERIAL, ArmorType.LEGGINGS)));
    public static final DeferredItem<Item> BAZULIUM_BOOTS = ITEMS.registerItem("bazulium_boots",
            properties -> new Item(properties.humanoidArmor(ModArmorMaterials.BAZULIUM_ARMOR_MATERIAL, ArmorType.BOOTS)));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
