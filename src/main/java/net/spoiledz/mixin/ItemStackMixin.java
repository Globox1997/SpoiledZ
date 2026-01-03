package net.spoiledz.mixin;

import net.minecraft.component.DataComponentTypes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.item.ItemStack;
import net.spoiledz.init.TagInit;
import net.spoiledz.util.SpoiledUtil;

@Mixin(ItemStack.class)
public class ItemStackMixin {

    @Inject(method = "areItemsAndComponentsEqual", at = @At("RETURN"), cancellable = true)
    private static void areItemsAndComponentsEqualMixin(ItemStack stack, ItemStack otherStack, CallbackInfoReturnable<Boolean> info) {
        if (!info.getReturnValue() && stack.isOf(otherStack.getItem()) && (stack.get(DataComponentTypes.FOOD) != null || stack.isIn(TagInit.SPOILING_ITEMS))
                && (otherStack.get(DataComponentTypes.FOOD) != null || otherStack.isIn(TagInit.SPOILING_ITEMS)) && SpoiledUtil.isSpoilageEqual(stack, otherStack)) {
            info.setReturnValue(true);
        }
    }
}
