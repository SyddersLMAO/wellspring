package com.sydders.wellspring.item.custom;

import com.sydders.wellspring.portal.SiftPortalShape;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Blocks;

public class WardenKeyItem extends Item {

    public WardenKeyItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {

        if (!(context.getLevel() instanceof ServerLevel level)) {
            return InteractionResult.SUCCESS;
        }

        if (!level.getBlockState(context.getClickedPos())
                .is(Blocks.REINFORCED_DEEPSLATE)) {
            return InteractionResult.PASS;
        }

        var shape = SiftPortalShape.find(
                level,
                context.getClickedPos()
        );

        if (shape.isEmpty()) {
            return InteractionResult.PASS;
        }

        shape.get().activate(level);

        return InteractionResult.SUCCESS;
    }
}
