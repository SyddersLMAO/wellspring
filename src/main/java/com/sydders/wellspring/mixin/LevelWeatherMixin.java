package com.sydders.wellspring.mixin;

import com.sydders.wellspring.worldgen.ModDimensions;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Level.class)
public abstract class LevelWeatherMixin {
    @Inject(method = "canHaveWeather", at = @At("HEAD"), cancellable = true)
    private void wellspring$disableSiftWeather(
            CallbackInfoReturnable<Boolean> cir
    ) {
        Level level = (Level) (Object) this;
        if (level.dimension().equals(ModDimensions.SIFT)) {
            cir.setReturnValue(false);
        }
    }
}