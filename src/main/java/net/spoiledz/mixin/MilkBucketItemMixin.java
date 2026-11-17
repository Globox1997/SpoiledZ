package net.spoiledz.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MilkBucketItem;
import net.minecraft.world.level.Level;
import net.spoiledz.util.SpoiledUtil;

@Mixin(MilkBucketItem.class)
public class MilkBucketItemMixin {

    @Inject(method = "finishUsingItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;removeAllEffects()Z", shift = At.Shift.AFTER))
    private void finishUsingMixin(ItemStack stack, Level world, LivingEntity user, CallbackInfoReturnable<ItemStack> info) {
        SpoiledUtil.applySpoiledEffects(user, SpoiledUtil.getSpoilingTime(world, stack));
    }
}
