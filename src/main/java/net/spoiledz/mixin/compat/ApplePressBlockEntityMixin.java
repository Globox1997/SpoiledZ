package net.spoiledz.mixin.compat;

import java.util.List;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.item.ItemStack;
import net.spoiledz.util.SpoiledUtil;
import satisfyu.vinery.block.entity.ApplePressBlockEntity;
import satisfyu.vinery.recipe.ApplePressRecipe;

@Mixin(ApplePressBlockEntity.class)
public abstract class ApplePressBlockEntityMixin {

    @Nullable
    private ItemStack recipeStack = null;

    @Inject(method = "craftItem", at = @At(value = "INVOKE", target = "Lsatisfyu/vinery/block/entity/ApplePressBlockEntity;removeStack(II)Lnet/minecraft/item/ItemStack;"))
    private static void stackCraftItemMixin(ApplePressBlockEntity entity, ApplePressRecipe recipe, CallbackInfo info) {
        ((ApplePressBlockEntityMixin) (Object) entity).setRecipeStack(entity.getStack(0));
    }

    @Inject(method = "craftItem", at = @At(value = "INVOKE", target = "Lsatisfyu/vinery/block/entity/ApplePressBlockEntity;resetProgress()V"))
    private static void craftItemMixin(ApplePressBlockEntity entity, ApplePressRecipe recipe, CallbackInfo info) {
        SpoiledUtil.setItemStackSpoilage(entity.getWorld(), entity.getStack(1), List.of(((ApplePressBlockEntityMixin) (Object) entity).getRecipeStack()));
    }

    @Inject(method = "hasRecipe", at = @At("RETURN"), cancellable = true, remap = false)
    private static void hasRecipeMixin(ApplePressBlockEntity entity, ApplePressRecipe recipe, CallbackInfoReturnable<Boolean> info) {
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
