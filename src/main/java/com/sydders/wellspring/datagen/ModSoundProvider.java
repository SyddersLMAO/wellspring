package com.sydders.wellspring.datagen;

import com.sydders.wellspring.Wellspring;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.SoundDefinition;
import net.neoforged.neoforge.common.data.SoundDefinitionsProvider;

public class ModSoundProvider extends SoundDefinitionsProvider {
    public ModSoundProvider(PackOutput output) {
        super(output, Wellspring.MODID);
    }

    @Override
    public void registerSounds() {
        add("entity.blub.ambient", SoundDefinition.definition()
                .with(
                        sound("wellspring:entity/blub/ambient1"),
                        sound("wellspring:entity/blub/ambient2"),
                        sound("wellspring:entity/blub/ambient3"),
                        sound("wellspring:entity/blub/ambient4")
                )
        );

        add("entity.blub.hurt", SoundDefinition.definition()
                .with(
                        sound("wellspring:entity/blub/hurt1"),
                        sound("wellspring:entity/blub/hurt2"),
                        sound("wellspring:entity/blub/hurt3")
                )
        );

        add("entity.blub.death", SoundDefinition.definition()
                .with(
                        sound("wellspring:entity/blub/death1"),
                        sound("wellspring:entity/blub/death2")
                )
        );

        add("music.sift", SoundDefinition.definition()
                .with(
                        sound("wellspring:music/ambition").stream()
                ));

        add("music.sift.creative", SoundDefinition.definition()
                .with(
                        sound("wellspring:music/ambition").stream()
                ));

        add("ambient.sift.cave", SoundDefinition.definition()
                .with(
                        sound("wellspring:ambient/cave_1"),
                        sound("wellspring:ambient/cave_2"),
                        sound("wellspring:ambient/cave_3")
                ));
    }
}
