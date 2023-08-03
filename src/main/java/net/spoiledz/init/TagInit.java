package net.spoiledz.init;

import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public class TagInit {

    public static final TagKey<Item> SPOILING_ITEMS = TagKey.of(RegistryKeys.ITEM, new Identifier("spoiledz", "spoiling_items"));
    public static final TagKey<Item> NON_SPOILING_ITEMS = TagKey.of(RegistryKeys.ITEM, new Identifier("spoiledz", "non_spoiling_items"));

    public static void init() {
    }

}
