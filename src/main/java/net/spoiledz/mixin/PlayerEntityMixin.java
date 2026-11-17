package net.spoiledz.mixin;

import net.minecraft.component.type.FoodComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.spoiledz.util.SpoiledUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {

    @ModifyVariable(method = "eatFood", at = @At("HEAD"), ordinal = 0)
    private FoodComponent eatFoodMixin(FoodComponent original, World world, ItemStack stack, FoodComponent foodComponent) {
        PlayerEntity playerEntity = (PlayerEntity) (Object) this;
        if (stack != null && SpoiledUtil.hasSpoilage(stack)) {
            return SpoiledUtil.getSpoiledFoodComponent(original, SpoiledUtil.getSpoilingTime(playerEntity.getWorld(), stack));
        }
        return original;
    }

    @Inject(method = "eatFood", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;eatFood(Lnet/minecraft/world/World;Lnet/minecraft/item/ItemStack;Lnet/minecraft/component/type/FoodComponent;)Lnet/minecraft/item/ItemStack;"))
    private void eatFoodMixin(World world, ItemStack stack, FoodComponent foodComponent, CallbackInfoReturnable<ItemStack> info) {
        PlayerEntity playerEntity = (PlayerEntity) (Object) this;
        if (stack != null) {
            SpoiledUtil.applySpoiledEffects(playerEntity, SpoiledUtil.getSpoilingTime(playerEntity.getWorld(), stack));
        }
    }
}
