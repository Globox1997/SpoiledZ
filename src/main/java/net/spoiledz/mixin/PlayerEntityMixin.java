package net.spoiledz.mixin;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.spoiledz.util.SpoiledUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public class PlayerEntityMixin {

    @ModifyVariable(method = "eat(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/food/FoodProperties;)Lnet/minecraft/world/item/ItemStack;", at = @At("HEAD"), ordinal = 0)
    private FoodProperties eatFoodMixin(FoodProperties original, Level world, ItemStack stack, FoodProperties foodComponent) {
        Player playerEntity = (Player) (Object) this;
        if (stack != null && SpoiledUtil.hasSpoilage(stack)) {
            return SpoiledUtil.getSpoiledFoodComponent(original, SpoiledUtil.getSpoilingTime(playerEntity.level(), stack));
        }
        return original;
    }

    @Inject(method = "eat(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/food/FoodProperties;)Lnet/minecraft/world/item/ItemStack;", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;eat(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/food/FoodProperties;)Lnet/minecraft/world/item/ItemStack;"))
    private void eatFoodMixin(Level world, ItemStack stack, FoodProperties foodComponent, CallbackInfoReturnable<ItemStack> info) {
        Player playerEntity = (Player) (Object) this;
        if (stack != null) {
            SpoiledUtil.applySpoiledEffects(playerEntity, SpoiledUtil.getSpoilingTime(playerEntity.level(), stack));
        }
    }
}
