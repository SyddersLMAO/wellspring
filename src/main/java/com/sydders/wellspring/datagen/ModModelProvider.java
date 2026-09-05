package com.sydders.wellspring.datagen;

import com.sydders.wellspring.Wellspring;
import com.sydders.wellspring.block.ModBlocks;
import com.sydders.wellspring.block.custom.SiftPortalBlock;
import com.sydders.wellspring.item.ModArmorMaterials;
import com.sydders.wellspring.item.ModItems;
import net.minecraft.client.color.item.GrassColorSource;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.data.models.model.ModelLocationUtils;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;

public class ModModelProvider extends ModelProvider {
    public ModModelProvider(PackOutput output) {
        super(output, Wellspring.MODID);
    }

    @Override
    protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
        itemModels.generateFlatItem(ModItems.RUBY.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModItems.WARDEN_KEY.get(), ModelTemplates.FLAT_ITEM);

        itemModels.generateFlatItem(ModItems.RAW_BAZULIUM.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModItems.BAZULIUM_INGOT.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModItems.BAZULIUM_NUGGET.get(), ModelTemplates.FLAT_ITEM);

        itemModels.generateFlatItem(ModItems.BAZULIUM_SWORD.get(), ModelTemplates.FLAT_HANDHELD_ITEM);
        itemModels.generateFlatItem(ModItems.BAZULIUM_PICKAXE.get(), ModelTemplates.FLAT_HANDHELD_ITEM);
        itemModels.generateFlatItem(ModItems.BAZULIUM_SHOVEL.get(), ModelTemplates.FLAT_HANDHELD_ITEM);
        itemModels.generateFlatItem(ModItems.BAZULIUM_AXE.get(), ModelTemplates.FLAT_HANDHELD_ITEM);
        itemModels.generateFlatItem(ModItems.BAZULIUM_HOE.get(), ModelTemplates.FLAT_HANDHELD_ITEM);
        itemModels.generateSpear(ModItems.BAZULIUM_SPEAR.get());

        itemModels.generateTrimmableItem(ModItems.BAZULIUM_HELMET.get(), ModArmorMaterials.BAZULIUM_KEY, ItemModelGenerators.TRIM_PREFIX_HELMET, false);
        itemModels.generateTrimmableItem(ModItems.BAZULIUM_CHESTPLATE.get(), ModArmorMaterials.BAZULIUM_KEY, ItemModelGenerators.TRIM_PREFIX_CHESTPLATE, false);
        itemModels.generateTrimmableItem(ModItems.BAZULIUM_LEGGINGS.get(), ModArmorMaterials.BAZULIUM_KEY, ItemModelGenerators.TRIM_PREFIX_LEGGINGS, false);
        itemModels.generateTrimmableItem(ModItems.BAZULIUM_BOOTS.get(), ModArmorMaterials.BAZULIUM_KEY, ItemModelGenerators.TRIM_PREFIX_BOOTS, false);

        //blocks
        blockModels.createTrivialCube(ModBlocks.RED_SCULK.get());
        blockModels.createTrivialCube(ModBlocks.SIFT_STONE.get());
        blockModels.createTrivialCube(ModBlocks.HARDENED_SIFT_STONE.get());
        blockModels.blockStateOutput.accept(
                MultiVariantGenerator.dispatch(ModBlocks.SIFT_PORTAL.get())
                        .with(PropertyDispatch.initial(SiftPortalBlock.AXIS)
                                .select(
                                        Direction.Axis.X,
                                        BlockModelGenerators.plainVariant(
                                                Identifier.fromNamespaceAndPath(Wellspring.MODID, "block/sift_portal_ns")
                                        )
                                )
                                .select(
                                        Direction.Axis.Z,
                                        BlockModelGenerators.plainVariant(
                                                Identifier.fromNamespaceAndPath(Wellspring.MODID, "block/sift_portal_ew")
                                        )
                                )
                        )
        );

        blockModels.blockStateOutput.accept(
                MultiVariantGenerator.dispatch(
                        ModBlocks.SIFT_GRASS_BLOCK.get(),
                        BlockModelGenerators.plainVariant(
                                ModelLocationUtils.getModelLocation(ModBlocks.SIFT_GRASS_BLOCK.get())
                        )
                )
        );
        itemModels.itemModelOutput.accept(
                ModBlocks.SIFT_GRASS_BLOCK.get().asItem(),
                ItemModelUtils.tintedModel(
                        ModelLocationUtils.getModelLocation(ModBlocks.SIFT_GRASS_BLOCK.get()),
                        new GrassColorSource(0.5F, 1.0F)
                )
        );

        blockModels.woodProvider(ModBlocks.SIFT_LOG.get()).logWithHorizontal(ModBlocks.SIFT_LOG.get()).wood(ModBlocks.SIFT_WOOD.get());
        blockModels.woodProvider(ModBlocks.STRIPPED_SIFT_LOG.get()).logWithHorizontal(ModBlocks.STRIPPED_SIFT_LOG.get()).wood(ModBlocks.STRIPPED_SIFT_WOOD.get());
        blockModels.createTrivialCube(ModBlocks.SIFT_LEAVES.get());

        blockModels.createPlantWithDefaultItem(ModBlocks.SIFT_SAPLING.get(), ModBlocks.POTTED_SIFT_SAPLING.get(), BlockModelGenerators.PlantType.NOT_TINTED);

        blockModels.family(ModBlocks.SIFT_PLANKS.get())
                .stairs(ModBlocks.SIFT_PLANKS_STAIRS.get())
                .slab(ModBlocks.SIFT_PLANKS_SLAB.get())
                .pressurePlate(ModBlocks.SIFT_PLANKS_PRESSURE_PLATE.get())
                .button(ModBlocks.SIFT_PLANKS_BUTTON.get())
                .fence(ModBlocks.SIFT_PLANKS_FENCE.get())
                .fenceGate(ModBlocks.SIFT_PLANKS_FENCE_GATE.get())
                .door(ModBlocks.SIFT_PLANKS_DOOR.get())
                .trapdoor(ModBlocks.SIFT_PLANKS_TRAPDOOR.get());

        blockModels.createTrivialCube(ModBlocks.BAZULIUM_ORE.get());
        blockModels.createTrivialCube(ModBlocks.HARDENED_BAZULIUM_ORE.get());
        blockModels.createTrivialCube(ModBlocks.BAZULIUM_BLOCK.get());
    }
}
