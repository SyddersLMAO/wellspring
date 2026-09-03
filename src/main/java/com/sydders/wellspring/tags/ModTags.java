package com.sydders.wellspring.tags;

import com.sydders.wellspring.Wellspring;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class ModTags {
    public static class Blocks {

        private static TagKey<Block> createTag(String name) {
            return BlockTags.create(Identifier.fromNamespaceAndPath(Wellspring.MODID, name));
        }
    }

    public static class Items {
        public static final TagKey<Item> SIFT_LOGS = createTag("sift_logs");

        private static TagKey<Item> createTag(String name) {
            return ItemTags.create(Identifier.fromNamespaceAndPath(Wellspring.MODID, name));
        }
    }
}
