package com.sydders.wellspring.entity.client;

import com.sydders.wellspring.Wellspring;
import net.minecraft.client.animation.KeyframeAnimation;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.entity.animation.json.AnimationHolder;

public class BlubModel extends EntityModel<BlubRenderState> {

    public static final ModelLayerLocation LAYER_LOCATION =
            new ModelLayerLocation(
                    Identifier.fromNamespaceAndPath(Wellspring.MODID, "blub"),
                    "main"
            );

    public static final AnimationHolder WALK_ANIMATION =
            Model.getAnimation(Identifier.fromNamespaceAndPath(Wellspring.MODID, "blub_walk"));

    public static final AnimationHolder IDLE_ANIMATION =
            Model.getAnimation(Identifier.fromNamespaceAndPath(Wellspring.MODID, "blub_idle"));

    private final KeyframeAnimation walkAnimation;
    private final KeyframeAnimation idleAnimation;

    public BlubModel(ModelPart root) {
        super(root);
        this.walkAnimation = WALK_ANIMATION.get().bake(root);
        this.idleAnimation = IDLE_ANIMATION.get().bake(root);
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
        } else {
            this.idleAnimation.apply((long) (state.ageInTicks * 50.0F), 1.0F);
        }
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        PartDefinition blub = root.addOrReplaceChild(
                "blub",
                CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(-5.0F, -9.0F, -5.0F, 10.0F, 7.0F, 10.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 24.0F, 0.0F)
        );

        blub.addOrReplaceChild(
                "front_l_leg",
                CubeListBuilder.create()
                        .texOffs(16, 17)
                        .addBox(-1.0F, 0.0F, 0.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)),
                PartPose.offset(4.0F, -2.0F, -5.0F)
        );

        blub.addOrReplaceChild(
                "back_l_leg",
                CubeListBuilder.create()
                        .texOffs(8, 22)
                        .addBox(-1.0F, 0.0F, -2.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)),
                PartPose.offset(4.0F, -2.0F, 5.0F)
        );

        blub.addOrReplaceChild(
                "front_r_leg",
                CubeListBuilder.create()
                        .texOffs(16, 21)
                        .addBox(-1.0F, 0.0F, 0.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)),
                PartPose.offset(-4.0F, -2.0F, -5.0F)
        );

        blub.addOrReplaceChild(
                "back_r_leg",
                CubeListBuilder.create()
                        .texOffs(0, 22)
                        .addBox(-1.0F, 0.0F, -2.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)),
                PartPose.offset(-4.0F, -2.0F, 5.0F)
        );

        blub.addOrReplaceChild(
                "l_ear",
                CubeListBuilder.create()
                        .texOffs(0, 17)
                        .addBox(-1.5F, -4.0F, -0.5F, 3.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)),
                PartPose.offset(2.5F, -9.0F, -3.5F)
        );

        blub.addOrReplaceChild(
                "r_ear",
                CubeListBuilder.create()
                        .texOffs(8, 17)
                        .addBox(-1.5F, -4.0F, -0.5F, 3.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)),
                PartPose.offset(-2.5F, -9.0F, -3.5F)
        );

        return LayerDefinition.create(mesh, 64, 32);
    }
}
