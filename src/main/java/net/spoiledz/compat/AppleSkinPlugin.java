package net.spoiledz.compat;

 import net.minecraft.component.type.FoodComponent;
 import net.minecraft.item.ItemStack;
 import net.spoiledz.init.ComponentInit;
 import net.spoiledz.util.SpoiledUtil;
 import squeek.appleskin.api.AppleSkinApi;
 import squeek.appleskin.api.event.FoodValuesEvent;

 public class AppleSkinPlugin implements AppleSkinApi {

    @Override
    public void registerEvents() {
        FoodValuesEvent.EVENT.register(event -> {
            if (event.player != null && event.itemStack.get(ComponentInit.SPOILED) != null) {
                ItemStack stack = event.itemStack;
                if (SpoiledUtil.getSpoilingTime(event.player.getWorld(), stack) >= 0) {
                    int spoiledTime = SpoiledUtil.getSpoilingTime(event.player.getWorld(), stack);
                    if (spoiledTime == 3) {
                        event.modifiedFoodComponent =  new FoodComponent((int)(  event.modifiedFoodComponent.nutrition()*0.8f),   event.modifiedFoodComponent.saturation()*0.8f,  event.modifiedFoodComponent.canAlwaysEat(),  event.modifiedFoodComponent.eatSeconds(),  event.modifiedFoodComponent.usingConvertsTo(),  event.modifiedFoodComponent.effects());
                    } else if (spoiledTime >= 4) {
                        event.modifiedFoodComponent =  new FoodComponent((int)(  event.modifiedFoodComponent.nutrition()/ 2),   event.modifiedFoodComponent.saturation()/2,  event.modifiedFoodComponent.canAlwaysEat(),  event.modifiedFoodComponent.eatSeconds(),  event.modifiedFoodComponent.usingConvertsTo(),  event.modifiedFoodComponent.effects());
                    }
                }
            }
        });
    }
 }
