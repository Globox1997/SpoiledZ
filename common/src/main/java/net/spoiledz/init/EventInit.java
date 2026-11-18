package net.spoiledz.init;

import net.minecraft.server.level.ServerLevel;
import net.spoiledz.access.ServerWorldAccess;

public class EventInit {

    public static void onServerLevelTick(ServerLevel world) {
        if (world.getGameTime() % 100 == 0 && world instanceof ServerWorldAccess serverWorldAccess) {
            serverWorldAccess.getFoodBlockState().tick(world);
        }
    }

}
