package net.spoiledz.init;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.spoiledz.access.ServerWorldAccess;

public class EventInit {

    public static void init() {
        ServerTickEvents.END_LEVEL_TICK.register(world -> {
            if (world.getGameTime() % 100 == 0 && world instanceof ServerWorldAccess serverWorldAccess) {
                serverWorldAccess.getFoodBlockState().tick(world);
            }
        });
        // NAUSEA,WEAKNESS,HUNGER,BLINDNESS,INSTANT_DAMAGE,SLOWNESS,MINING_FATIGUE POISON
    }

}
