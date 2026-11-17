package net.spoiledz.init;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.spoiledz.SpoiledZMain;

public class TagInit {

    public static final TagKey<Item> SPOILING_ITEMS = TagKey.create(Registries.ITEM, SpoiledZMain.identifierOf("spoiling_items"));
    public static final TagKey<Item> NON_SPOILING_ITEMS = TagKey.create(Registries.ITEM, SpoiledZMain.identifierOf("non_spoiling_items"));

    public static void init() {
    }

}
