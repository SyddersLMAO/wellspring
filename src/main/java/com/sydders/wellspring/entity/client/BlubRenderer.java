package com.sydders.wellspring.entity.client;

import com.sydders.wellspring.Wellspring;
import com.sydders.wellspring.entity.custom.BlubEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.Identifier;

public class BlubRenderer extends MobRenderer<
        BlubEntity,
        BlubRenderState,
        BlubModel
        > {

    private static final Identifier TEXTURE =
            Identifier.fromNamespaceAndPath(
                    Wellspring.MODID,
                    "textures/entity/blub.png"
            );

    public BlubRenderer(
            EntityRendererProvider.Context context
    ) {
        super(
                context,
                new BlubModel(
                        context.bakeLayer(
                                BlubModel.LAYER_LOCATION
                        )
                ),
                0.4F
        );
    }

    @Override
    public BlubRenderState createRenderState() {
        return new BlubRenderState();
    }

    @Override
    public Identifier getTextureLocation(
            BlubRenderState state
    ) {
        return TEXTURE;
    }

    @Override
    public void extractRenderState(
            BlubEntity entity,
            BlubRenderState state,
            float partialTick
    ) {
        super.extractRenderState(entity, state, partialTick);

        state.moving =
                entity.getDeltaMovement().horizontalDistanceSqr()
                        > 0.0001D;
    }
}