package net.spoiledz.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.level.Level;
import net.spoiledz.util.SpoiledUtil;

@Mixin(FoodProperties.class)
public class FoodPropertiesMixin {

    @Redirect(method = "onConsume", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/food/FoodData;eat(Lnet/minecraft/world/food/FoodProperties;)V"))
    private void onConsumeMixin(FoodData foodData, FoodProperties foodProperties, Level level, LivingEntity user, ItemStack stack, Consumable consumable) {
        if (stack != null && SpoiledUtil.hasSpoilage(stack)) {
            int spoiledTime = SpoiledUtil.getSpoilingTime(level, stack);
            foodData.eat(SpoiledUtil.getSpoiledFoodComponent(foodProperties, spoiledTime));
            SpoiledUtil.applySpoiledEffects(user, spoiledTime);
        } else {
            foodData.eat(foodProperties);
        }
    }
}
