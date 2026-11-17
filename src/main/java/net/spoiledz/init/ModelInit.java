package net.spoiledz.init;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.spoiledz.util.SpoiledUtil;

@Environment(EnvType.CLIENT)
public class ModelInit {

    public static void init() {
        ItemProperties.registerGeneric(ResourceLocation.parse("spoiled"), (stack, world, entity, seed) -> {
            if (world != null) {
                return (SpoiledUtil.getSpoilingTime(world, stack) / 4f);
            } else if (entity != null) {
                return (SpoiledUtil.getSpoilingTime(entity.level(), stack) / 4f);
            } else {
                return 0f;
            }
        });
    }
}
