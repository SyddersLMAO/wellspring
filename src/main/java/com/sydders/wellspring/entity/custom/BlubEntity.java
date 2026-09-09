package com.sydders.wellspring.entity.custom;

import com.sydders.wellspring.tags.ModTags;
import com.sydders.wellspring.worldgen.ModDimensions;
import com.sydders.wellspring.sound.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ambient.AmbientCreature;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;

import javax.annotation.Nullable;

public class BlubEntity extends PathfinderMob {
    public BlubEntity(EntityType<? extends BlubEntity> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return AmbientCreature.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 8)
                .add(Attributes.MOVEMENT_SPEED, 0.25D)
                .add(Attributes.FOLLOW_RANGE, 32.0D);
    }

    public static boolean checkBlubSpawnRules(
            EntityType<? extends BlubEntity> type,
            ServerLevelAccessor level,
            EntitySpawnReason spawnReason,
            BlockPos pos,
            RandomSource random
    ) {
        return level.getLevel().dimension().equals(ModDimensions.SIFT)
                && level.getBlockState(pos.below()).is(ModTags.Blocks.BLUB_SPAWNABLE_ON);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(
                5,
                new RandomStrollGoal(this, 0.8D)
        );
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return ModSounds.BLUB_AMBIENT.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSounds.BLUB_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.BLUB_DEATH.get();
    }
}
