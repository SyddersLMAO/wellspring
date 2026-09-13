package com.sydders.wellspring.item.custom;

import com.sydders.wellspring.block.custom.SiftPortalBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

public class WardenKeyItem extends Item {

    public WardenKeyItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        ItemStack itemStack = context.getItemInHand();
        Level level = context.getLevel();
        BlockPos portalPos = context.getClickedPos()
                .relative(context.getClickedFace());

        if (player != null
                && !player.mayUseItemAt(
                        portalPos,
                        context.getClickedFace(),
                        itemStack
                )) {
            return InteractionResult.FAIL;
        }

        if (!level.isEmptyBlock(portalPos)
                || !SiftPortalBlock.canSpawnPortal(level, portalPos)) {
            return InteractionResult.FAIL;
        }

        if (!level.isClientSide()) {
            SiftPortalBlock.trySpawnPortal(level, portalPos);

            if (player == null || !player.isCreative()) {
                itemStack.shrink(1);
            }
        }

        return InteractionResult.SUCCESS;
    }
}
