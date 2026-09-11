package com.sydders.wellspring.entity.client;

import com.sydders.wellspring.Wellspring;
import net.minecraft.client.animation.KeyframeAnimation;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.entity.animation.json.AnimationHolder;

public class BlubModel extends EntityModel<BlubRenderState> {

    public static final ModelLayerLocation LAYER_LOCATION =
            new ModelLayerLocation(
                    Identifier.fromNamespaceAndPath(
                            Wellspring.MODID,
                            "blub"
                    ),
                    "main"
            );

    public static final AnimationHolder WALK_ANIMATION =
            Model.getAnimation(
                    Identifier.fromNamespaceAndPath(
                            Wellspring.MODID,
                            "blub_walk"
                    )
            );

    private final KeyframeAnimation walkAnimation;

    private final ModelPart Body;
    private final ModelPart FrontLegL;
    private final ModelPart FrontLegR;
    private final ModelPart BackLegL;
    private final ModelPart BackLegR;
    private final ModelPart EarL;
    private final ModelPart EarR;

    public BlubModel(ModelPart root) {
        super(root);

        this.Body = root.getChild("Body");
        this.FrontLegL = this.Body.getChild("FrontLegL");
        this.FrontLegR = this.Body.getChild("FrontLegR");
        this.BackLegL = this.Body.getChild("BackLegL");
        this.BackLegR = this.Body.getChild("BackLegR");
        this.EarL = this.Body.getChild("EarL");
        this.EarR = this.Body.getChild("EarR");

        this.walkAnimation =
                WALK_ANIMATION.get().bake(root);
    }

    @Override
    public void setupAnim(BlubRenderState state) {
        super.setupAnim(state);

        if (state.moving) {
            this.walkAnimation.applyWalk(
                    state.ageInTicks,
                    Math.max(state.walkAnimationSpeed, 0.1F),
                    1.0F,
                    1.0F
            );
        }
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition Body = partdefinition.addOrReplaceChild(
                "Body",
                CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(
                                -5.0F, -3.0F, -6.0F,
                                10.0F, 7.0F, 12.0F,
                                new CubeDeformation(0.0F)
                        ),
                PartPose.offset(0.0F, 18.0F, 0.0F)
        );

        Body.addOrReplaceChild(
                "FrontLegL",
                CubeListBuilder.create()
                        .texOffs(16, 19)
                        .addBox(
                                -1.0F, 0.0F, -1.0F,
                                2.0F, 2.0F, 2.0F,
                                new CubeDeformation(0.0F)
                        ),
                PartPose.offset(-4.0F, 4.0F, -5.0F)
        );

        Body.addOrReplaceChild(
                "FrontLegR",
                CubeListBuilder.create()
                        .texOffs(16, 23)
                        .addBox(
                                -1.0F, 0.0F, -1.0F,
                                2.0F, 2.0F, 2.0F,
                                new CubeDeformation(0.0F)
                        ),
                PartPose.offset(4.0F, 4.0F, -5.0F)
        );

        Body.addOrReplaceChild(
                "BackLegL",
                CubeListBuilder.create()
                        .texOffs(0, 24)
                        .addBox(
                                -1.0F, 0.0F, -1.0F,
                                2.0F, 2.0F, 2.0F,
                                new CubeDeformation(0.0F)
                        ),
                PartPose.offset(-4.0F, 4.0F, 5.0F)
        );

        Body.addOrReplaceChild(
                "BackLegR",
                CubeListBuilder.create()
                        .texOffs(8, 24)
                        .addBox(
                                -1.0F, 0.0F, -1.0F,
                                2.0F, 2.0F, 2.0F,
                                new CubeDeformation(0.0F)
                        ),
                PartPose.offset(4.0F, 4.0F, 5.0F)
        );

        Body.addOrReplaceChild(
                "EarL",
                CubeListBuilder.create()
                        .texOffs(0, 19)
                        .addBox(
                                -1.5F, -4.0F, -0.5F,
                                3.0F, 4.0F, 1.0F,
                                new CubeDeformation(0.0F)
                        ),
                PartPose.offset(-2.5F, -3.0F, -3.5F)
        );

        Body.addOrReplaceChild(
                "EarR",
                CubeListBuilder.create()
                        .texOffs(8, 19)
                        .addBox(
                                -1.5F, -4.0F, -0.5F,
                                3.0F, 4.0F, 1.0F,
                                new CubeDeformation(0.0F)
                        ),
                PartPose.offset(2.5F, -3.0F, -3.5F)
        );

        return LayerDefinition.create(meshdefinition, 64, 64);
    }
}