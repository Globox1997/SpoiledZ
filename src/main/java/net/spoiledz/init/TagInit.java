package net.spoiledz.init;

import net.minecraft.item.Item;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class TagInit {

    public static final TagKey<Item> SPOILING_ITEMS = TagKey.of(Registry.ITEM_KEY, new Identifier("spoiledz", "spoiling_items"));
    public static final TagKey<Item> NON_SPOILING_ITEMS = TagKey.of(Registry.ITEM_KEY, new Identifier("spoiledz", "non_spoiling_items"));

    public static void init() {
    }

}
