package com.sydders.wellspring;

import com.sydders.wellspring.block.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;

// This class will not load on dedicated servers. Accessing client side code from here is safe.
@Mod(value = Wellspring.MODID, dist = Dist.CLIENT)
// You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
@EventBusSubscriber(modid = Wellspring.MODID, value = Dist.CLIENT)
public class WellspringClient {
    private static final Identifier SIFT_PORTAL_OVERLAY_LAYER =
            Identifier.fromNamespaceAndPath(Wellspring.MODID, "sift_portal_overlay");
    private static final Identifier SIFT_PORTAL_OVERLAY_TEXTURE =
            Identifier.fromNamespaceAndPath(Wellspring.MODID, "textures/misc/sift_portal_overlay.png");
    private static final int SIFT_PORTAL_TRANSITION_TICKS = 80;
    private static final int SIFT_PORTAL_OVERLAY_TEXTURE_SIZE = 256;
    private static float siftPortalOverlayProgress;

    public WellspringClient(ModContainer container) {
        // Allows NeoForge to create a config screen for this mod's configs.
        // The config screen is accessed by going to the Mods screen > clicking on your mod > clicking on config.
        // Do not forget to add translations for your config options to the en_us.json file.
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }

    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {
        // Some client setup code
        Wellspring.LOGGER.info("HELLO FROM CLIENT SETUP");
        Wellspring.LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
    }

    @SubscribeEvent
    static void registerGuiLayers(RegisterGuiLayersEvent event) {
        event.registerAbove(
                VanillaGuiLayers.CAMERA_OVERLAYS,
                SIFT_PORTAL_OVERLAY_LAYER,
                WellspringClient::renderSiftPortalOverlay
        );
    }

    private static void renderSiftPortalOverlay(
            GuiGraphicsExtractor guiGraphics,
            DeltaTracker deltaTracker
    ) {
        Minecraft minecraft = Minecraft.getInstance();

        if (minecraft.player == null || minecraft.level == null) {
            return;
        }

        float deltaTicks = deltaTracker.getGameTimeDeltaTicks();

        if (isPlayerInsideSiftPortal(minecraft)) {
            siftPortalOverlayProgress = Math.min(
                    1.0F,
                    siftPortalOverlayProgress + deltaTicks / SIFT_PORTAL_TRANSITION_TICKS
            );
        } else {
            siftPortalOverlayProgress = Math.max(
                    0.0F,
                    siftPortalOverlayProgress - deltaTicks / 10.0F
            );
        }

        if (siftPortalOverlayProgress <= 0.0F) {
            return;
        }

        float alpha = 0.15F + siftPortalOverlayProgress * 0.65F;
        int color = ARGB.white(alpha);

        guiGraphics.blit(
                RenderPipelines.GUI_TEXTURED,
                SIFT_PORTAL_OVERLAY_TEXTURE,
                0,
                0,
                0.0F,
                0.0F,
                guiGraphics.guiWidth(),
                guiGraphics.guiHeight(),
                SIFT_PORTAL_OVERLAY_TEXTURE_SIZE,
                SIFT_PORTAL_OVERLAY_TEXTURE_SIZE,
                SIFT_PORTAL_OVERLAY_TEXTURE_SIZE,
                SIFT_PORTAL_OVERLAY_TEXTURE_SIZE,
                color
        );
    }

    private static boolean isPlayerInsideSiftPortal(Minecraft minecraft) {
        for (BlockPos pos : BlockPos.betweenClosed(minecraft.player.getBoundingBox().deflate(1.0E-7D))) {
            if (minecraft.level.getBlockState(pos).is(ModBlocks.SIFT_PORTAL.get())) {
                return true;
            }
        }

        return false;
    }
}
