package com.sydders.wellspring.event;

import com.sydders.wellspring.Wellspring;
import com.sydders.wellspring.portal.SiftPortalShape;
import com.sydders.wellspring.item.ModItems;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.core.BlockPos;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

@EventBusSubscriber(modid = Wellspring.MODID)
public class SiftPortalInteraction {

    @SubscribeEvent
    public static void onRightClickBlock(
            PlayerInteractEvent.RightClickBlock event) {

        Level level = event.getLevel();

        if (level.isClientSide()) {
            return;
        }

        ItemStack item = event.getItemStack();

        if (!item.is(ModItems.WARDEN_KEY.get())) {
            return;
        }

        BlockPos clicked = event.getPos();

        boolean created = SiftPortalShape.trySpawnPortal(
                (ServerLevel) level,
                clicked
        );

        if (!created) {
            return;
        }

        if (!event.getEntity().isCreative()) {
            item.shrink(1);
        }

        event.setCanceled(true);
    }
}
