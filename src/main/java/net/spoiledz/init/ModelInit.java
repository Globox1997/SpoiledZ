package net.spoiledz.init;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.util.Identifier;
import net.spoiledz.util.SpoiledUtil;

@Environment(EnvType.CLIENT)
public class ModelInit {

    public static void init() {
        ModelPredicateProviderRegistry.register(Identifier.of("spoiled"), (stack, world, entity, seed) -> {
            if (world != null) {
                return (SpoiledUtil.getSpoilingTime(world, stack) / 4f);
            } else if (entity != null) {
                return (SpoiledUtil.getSpoilingTime(entity.getWorld(), stack) / 4f);
            } else {
                return 0f;
            }
        });
    }
}
