package net.spoiledz.mixin;

import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.spoiledz.init.ComponentInit;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(Slot.class)
public class SlotMixin {

    @Inject(method = "insertStack(Lnet/minecraft/item/ItemStack;I)Lnet/minecraft/item/ItemStack;", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;decrement(I)V"), locals = LocalCapture.CAPTURE_FAILSOFT)
    private void insertStackMixin(ItemStack stack, int count, CallbackInfoReturnable<ItemStack> info, ItemStack itemStack, int i) {
        if(stack.contains(ComponentInit.SPOILED) && !itemStack.contains(ComponentInit.SPOILED)){
            itemStack.set(ComponentInit.SPOILED, stack.get(ComponentInit.SPOILED));
        }
    }
}
