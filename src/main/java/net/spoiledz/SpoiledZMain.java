package net.spoiledz;

import java.util.List;

import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;
import net.spoiledz.init.*;

public class SpoiledZMain implements ModInitializer {

    public static final List<String> FABRIC_SEASONS = List.of("spring", "summer", "fall", "winter");
    public static final List<String> SERENE_SEASONS = List.of("SPRING", "SUMMER", "AUTUMN", "WINTER");

    @Override
    public void onInitialize() {
        ConfigInit.init();
        ComponentInit.init();
        TagInit.init();
        EventInit.init();
        CompatInit.init();
    }

    public static Identifier identifierOf(String name) {
        return Identifier.of("spoiledz", name);
    }

}
