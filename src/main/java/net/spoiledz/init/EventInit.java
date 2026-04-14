package net.spoiledz.init;

import net.fabricmc.loader.api.FabricLoader;

public class EventInit {

    public static final boolean isSereneSeasonsLoaded = FabricLoader.getInstance().isModLoaded("sereneseasons");
    public static final boolean isFabricSeasonsLoaded = FabricLoader.getInstance().isModLoaded("seasons");

    public static void init() {

        // NAUSEA,WEAKNESS,HUNGER,BLINDNESS,INSTANT_DAMAGE,SLOWNESS,MINING_FATIGUE POISON
    }

}
