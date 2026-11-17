package net.spoiledz.compat;

import net.spoiledz.util.SpoiledUtil;
import squeek.appleskin.api.AppleSkinApi;
import squeek.appleskin.api.event.FoodValuesEvent;

public class AppleSkinPlugin implements AppleSkinApi {

    @Override
    public void registerEvents() {
        FoodValuesEvent.EVENT.register(event -> {
            if (event.player != null && SpoiledUtil.hasSpoilage(event.itemStack)) {
                event.modifiedFoodComponent = SpoiledUtil.getSpoiledFoodComponent(event.modifiedFoodComponent, SpoiledUtil.getSpoilingTime(event.player.getWorld(), event.itemStack));
            }
        });
    }
}
