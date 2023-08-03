package net.spoiledz.init;

import java.util.ArrayList;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.event.registry.RegistryEntryAddedCallback;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.spoiledz.util.SpoiledUtil;

@Environment(EnvType.CLIENT)
public class ModelProviderInit {

    private static final List<Item> spoiledItems = new ArrayList<Item>();
    private static final List<Identifier> extraItemIdentifiers = new ArrayList<Identifier>();
    private static final List<String> extraItemIds = List.of("minecraft:wheat", "minecraft:egg", "minecraft:milk_bucket", "minecraft:honey_bottle");

    public static void init() {
        extraItemIds.forEach((id) -> extraItemIdentifiers.add(new Identifier(id)));

        Registries.ITEM.forEach((item) -> {
            if (item.isFood() || extraItemIdentifiers.contains(Registries.ITEM.getId(item))) {
                spoiledItems.add(item);
            }
        });
        spoiledItems.forEach((item) -> {
            registerModelPredicateProvider(item);
        });
        // [apple, mushroom_stew, wheat, bread, porkchop, cooked_porkchop, golden_apple, enchanted_golden_apple, milk_bucket, egg, cod, salmon, tropical_fish, pufferfish, cooked_cod, cooked_salmon,
        // cookie, melon_slice, dried_kelp, beef, cooked_beef, chicken, cooked_chicken, rotten_flesh, spider_eye, carrot, potato, baked_potato, poisonous_potato, golden_carrot, pumpkin_pie, rabbit,
        // cooked_rabbit, rabbit_stew, mutton, cooked_mutton, chorus_fruit, beetroot, beetroot_soup, suspicious_stew, sweet_berries, glow_berries, honey_bottle]

        RegistryEntryAddedCallback.event(Registries.ITEM).register((rawId, id, item) -> {
            if (item.isFood() || extraItemIdentifiers.contains(id)) {
                registerModelPredicateProvider(item);
            }
        });
    }

    private static void registerModelPredicateProvider(Item item) {
        ModelPredicateProviderRegistry.register(item, new Identifier("spoiled"), (stack, world, entity, seed) -> {
            if (world != null)
                return (float) (SpoiledUtil.getSpoilingTime(world, stack) / 4f);
            else if (entity != null)
                return (float) (SpoiledUtil.getSpoilingTime(entity.getWorld(), stack) / 4f);
            else
                return 0f;
        });
    }
}
