package net.spoiledz;

import java.util.List;

import net.fabricmc.api.ModInitializer;
import net.minecraft.resources.ResourceLocation;
import net.spoiledz.init.*;

public class SpoiledZMain implements ModInitializer {

    public static final List<String> FABRIC_SEASONS = List.of("spring", "summer", "fall", "winter");
    public static final List<String> SERENE_SEASONS = List.of("SPRING", "SUMMER", "AUTUMN", "WINTER");

    @Override
    public void onInitialize() {
        if (!EventInit.isSereneSeasonsLoaded && !EventInit.isFabricSeasonsLoaded) {
            throw new IllegalStateException("SpoiledZ requires Fabric Seasons or Serene Seasons to be installed");
        }
        ConfigInit.init();
        ComponentInit.init();
        TagInit.init();
        EventInit.init();
        CompatInit.init();
    }

    public static ResourceLocation identifierOf(String name) {
        return ResourceLocation.fromNamespaceAndPath("spoiledz", name);
    }

}
