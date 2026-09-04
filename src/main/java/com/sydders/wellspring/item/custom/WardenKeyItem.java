package com.sydders.wellspring.item.custom;

import com.sydders.wellspring.portal.SiftPortalManager;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;

public class WardenKeyItem extends Item {

    public WardenKeyItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {

        if (!(context.getLevel() instanceof ServerLevel level)) {
            return InteractionResult.SUCCESS;
        }

        if (!SiftPortalManager.activate(
                level,
                context.getClickedPos()
        )) {
            return InteractionResult.PASS;
        }

        if (context.getPlayer() == null
                || !context.getPlayer().isCreative()) {
            context.getItemInHand().shrink(1);
        }

        return InteractionResult.SUCCESS;
    }
}
