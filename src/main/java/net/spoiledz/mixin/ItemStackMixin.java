package net.spoiledz.mixin;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.item.ItemStack;
import net.spoiledz.util.SpoiledUtil;

@Mixin(ItemStack.class)
public class ItemStackMixin {

    @Inject(method = "areItemsAndComponentsEqual", at = @At("RETURN"), cancellable = true)
    private static void areItemsAndComponentsEqualMixin(ItemStack stack, ItemStack otherStack, CallbackInfoReturnable<Boolean> info) {
        if (!info.getReturnValue() && stack.isOf(otherStack.getItem()) && SpoiledUtil.isSpoilageEqual(stack, otherStack) && SpoiledUtil.areComponentsEqualIgnoringSpoilage(stack, otherStack)) {
            info.setReturnValue(true);
        }
    }

    @Inject(method = "hashCode(Lnet/minecraft/item/ItemStack;)I", at = @At("HEAD"), cancellable = true)
    private static void hashCodeMixin(@Nullable ItemStack stack, CallbackInfoReturnable<Integer> info) {
        if (stack != null && !stack.isEmpty() && SpoiledUtil.isSpoilable(stack)) {
            int i = 31 + stack.getItem().hashCode();
            info.setReturnValue(31 * i + SpoiledUtil.hashComponentsIgnoringSpoilage(stack));
        }
    }
}
