package net.spoiledz.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.spoiledz.util.SpoiledUtil;

@Mixin(Animal.class)
public class AnimalEntityMixin {

    @Inject(method = "mobInteract", at = @At("HEAD"), cancellable = true)
    private void interactMobMixin(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> info) {
        ItemStack stack = player.getItemInHand(hand);
        if (((Animal) (Object) this).isFood(stack) && SpoiledUtil.getSpoilingTime(player.level(), stack) >= 4) {
            info.setReturnValue(InteractionResult.PASS);
        }
    }
}
