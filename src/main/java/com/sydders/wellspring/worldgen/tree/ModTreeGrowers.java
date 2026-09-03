package com.sydders.wellspring.worldgen.tree;

import com.sydders.wellspring.Wellspring;
import com.sydders.wellspring.worldgen.ModConfiguredFeatures;
import net.minecraft.world.level.block.grower.TreeGrower;

import java.util.Optional;

public class ModTreeGrowers {
    public static final TreeGrower SIFT = new TreeGrower(Wellspring.MODID + ":sift",
            Optional.empty(), Optional.of(ModConfiguredFeatures.SIFT_KEY), Optional.empty());
}
