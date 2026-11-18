package net.spoiledz.neoforge.compat;

import net.neoforged.neoforge.common.NeoForge;
import net.spoiledz.util.SpoiledUtil;
import squeek.appleskin.api.event.FoodValuesEvent;

public class AppleSkinNeoForgeCompat {

    public static void register() {
        NeoForge.EVENT_BUS.addListener(FoodValuesEvent.class, event -> {
            if (event.player != null && SpoiledUtil.hasSpoilage(event.itemStack)) {
                event.modifiedFoodProperties = SpoiledUtil.getSpoiledFoodComponent(event.modifiedFoodProperties, SpoiledUtil.getSpoilingTime(event.player.level(), event.itemStack));
            }
        });
    }
}
