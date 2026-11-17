package net.spoiledz.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.spoiledz.util.SpoiledUtil;

@Mixin(AnimalEntity.class)
public class AnimalEntityMixin {

    @Inject(method = "interactMob", at = @At("HEAD"), cancellable = true)
    private void interactMobMixin(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> info) {
        ItemStack stack = player.getStackInHand(hand);
        if (((AnimalEntity) (Object) this).isBreedingItem(stack) && SpoiledUtil.getSpoilingTime(player.getWorld(), stack) >= 4) {
            info.setReturnValue(ActionResult.PASS);
        }
    }
}
