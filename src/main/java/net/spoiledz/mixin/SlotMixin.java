package net.spoiledz.mixin;

import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.spoiledz.util.SpoiledComponent;
import net.spoiledz.util.SpoiledUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(Slot.class)
public class SlotMixin {

    @Inject(method = "safeInsert(Lnet/minecraft/world/item/ItemStack;I)Lnet/minecraft/world/item/ItemStack;", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;shrink(I)V"), locals = LocalCapture.CAPTURE_FAILSOFT)
    private void insertStackMixin(ItemStack stack, int count, CallbackInfoReturnable<ItemStack> info, ItemStack itemStack, int i) {
        SpoiledComponent spoiledComponent = SpoiledUtil.getSpoiledComponent(stack);
        if (spoiledComponent != null && !SpoiledUtil.hasSpoilage(itemStack)) {
            SpoiledUtil.setSpoiledComponent(itemStack, spoiledComponent);
        }
    }
}
