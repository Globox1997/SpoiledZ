package net.spoiledz;

import java.util.List;

import net.fabricmc.api.ModInitializer;
import net.spoiledz.init.ConfigInit;
import net.spoiledz.init.EventInit;
import net.spoiledz.init.TagInit;

public class SpoiledZMain implements ModInitializer {

    public static final List<String> SEASONS = List.of("spring", "summer", "fall", "winter");

    @Override
    public void onInitialize() {
        ConfigInit.init();
        TagInit.init();
        EventInit.init();
    }

}
