package net.spoiledz.mixin.compat;

import java.util.List;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import daniking.vinery.block.entity.WinePressBlockEntity;
import net.minecraft.item.ItemStack;
import net.spoiledz.util.SpoiledUtil;

@Mixin(WinePressBlockEntity.class)
public abstract class WinePressBlockEntityMixin {

    @Nullable
    private ItemStack recipeStack = null;

    @Inject(method = "craftItem", at = @At(value = "INVOKE", target = "Ldaniking/vinery/block/entity/WinePressBlockEntity;removeStack(II)Lnet/minecraft/item/ItemStack;"))
    private static void stackCraftItemMixin(WinePressBlockEntity entity, CallbackInfo info) {
        ((WinePressBlockEntityMixin) (Object) entity).setRecipeStack(entity.getStack(0));
    }

    @Inject(method = "craftItem", at = @At(value = "INVOKE", target = "Ldaniking/vinery/block/entity/WinePressBlockEntity;resetProgress()V"))
    private static void craftItemMixin(WinePressBlockEntity entity, CallbackInfo info) {
        SpoiledUtil.setItemStackSpoilage(entity.getWorld(), entity.getStack(1), List.of(((WinePressBlockEntityMixin) (Object) entity).getRecipeStack()));
    }

    @Inject(method = "hasRecipe", at = @At("RETURN"), cancellable = true, remap = false)
    private static void hasRecipeMixin(WinePressBlockEntity entity, CallbackInfoReturnable<Boolean> info) {
        if (info.getReturnValue() && !entity.getStack(1).isEmpty() && !SpoiledUtil.isSpoilageEqual(entity.getStack(0), entity.getStack(1))) {
            info.setReturnValue(false);
        }
    }

    private void setRecipeStack(ItemStack stack) {
        this.recipeStack = stack.copy();
    }

    @Nullable
    private ItemStack getRecipeStack() {
        return this.recipeStack;
    }

}
