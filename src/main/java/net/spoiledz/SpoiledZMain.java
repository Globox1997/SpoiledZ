package net.spoiledz;

import java.util.List;

import net.fabricmc.api.ModInitializer;
import net.spoiledz.init.ComponentInit;
import net.spoiledz.init.ConfigInit;
import net.spoiledz.init.EventInit;
import net.spoiledz.init.TagInit;

public class SpoiledZMain implements ModInitializer {

    public static final List<String> FABRIC_SEASONS = List.of("spring", "summer", "fall", "winter");
    public static final List<String> SERENE_SEASONS = List.of("SPRING", "SUMMER", "AUTUMN", "WINTER");

    @Override
    public void onInitialize() {
        ConfigInit.init();
        ComponentInit.init();
        TagInit.init();
        EventInit.init();
    }

}
