package net.spoiledz.init;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.spoiledz.access.ServerWorldAccess;

public class EventInit {

    public static final boolean isSereneSeasonsLoaded = FabricLoader.getInstance().isModLoaded("sereneseasons");
    public static final boolean isFabricSeasonsLoaded = FabricLoader.getInstance().isModLoaded("seasons");

    public static void init() {
        ServerTickEvents.END_WORLD_TICK.register(world -> {
            if (world.getGameTime() % 100 == 0 && world instanceof ServerWorldAccess serverWorldAccess) {
                serverWorldAccess.getFoodBlockState().tick();
            }
        });
        // NAUSEA,WEAKNESS,HUNGER,BLINDNESS,INSTANT_DAMAGE,SLOWNESS,MINING_FATIGUE POISON
    }

}
