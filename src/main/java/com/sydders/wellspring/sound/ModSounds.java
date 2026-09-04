package com.sydders.wellspring.sound;

import com.sydders.wellspring.Wellspring;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(BuiltInRegistries.SOUND_EVENT, Wellspring.MODID);

    public static final Supplier<SoundEvent> BLUB_AMBIENT =
            registerSound("entity.blub.ambient");

    public static final Supplier<SoundEvent> BLUB_HURT =
            registerSound("entity.blub.hurt");

    public static final Supplier<SoundEvent> BLUB_DEATH =
            registerSound("entity.blub.death");


    private static Supplier<SoundEvent> registerSound(String name) {
        return SOUND_EVENTS.register(
                name,
                () -> SoundEvent.createVariableRangeEvent(
                        Identifier.fromNamespaceAndPath(
                                Wellspring.MODID,
                                name
                        )
                )
        );
    }

    public static void register(IEventBus eventBus) {
        SOUND_EVENTS.register(eventBus);
    }
}
