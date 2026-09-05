package com.sydders.wellspring.item;

import com.google.common.collect.Maps;
import com.sydders.wellspring.Wellspring;
import com.sydders.wellspring.tags.ModTags;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.EquipmentAssets;

import java.util.Map;

public class ModArmorMaterials {
    public static final ResourceKey<? extends Registry<EquipmentAsset>> ROOT_ID = ResourceKey.createRegistryKey(Identifier.withDefaultNamespace("equipment_asset"));

    public static final ResourceKey<EquipmentAsset> BAZULIUM_KEY = ResourceKey.create(ROOT_ID, Identifier.fromNamespaceAndPath(Wellspring.MODID, "bazulium"));

    public static final ArmorMaterial BAZULIUM_ARMOR_MATERIAL = new ArmorMaterial(
            52, makeDefense(3, 6, 8, 3, 20), 22, SoundEvents.ARMOR_EQUIP_GENERIC,
            4.0F, 0.2F, ModTags.Items.BAZULIUM_REPAIRABLE, BAZULIUM_KEY);

    private static Map<ArmorType, Integer> makeDefense(int boots, int legs, int chest, int helm, int body) {
        return Maps.newEnumMap(
                Map.of(ArmorType.BOOTS, boots, ArmorType.LEGGINGS, legs, ArmorType.CHESTPLATE, chest, ArmorType.HELMET, helm, ArmorType.BODY, body)
        );
    }
}
