package net.spoiledz.mixin.compat;

import java.util.ArrayList;
import java.util.List;

import com.ianm1647.expandeddelight.block.entity.JuicerBlockEntity;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.spoiledz.util.SpoiledUtil;

@Mixin(JuicerBlockEntity.class)
public class JuicerBlockEntityMixin {

    private List<ItemStack> recipeStacks = new ArrayList<ItemStack>();

    @Inject(method = "craftItem", at = @At(value = "INVOKE", target = "Lcom/ianm1647/expandeddelight/block/entity/JuicerBlockEntity;removeStack(II)Lnet/minecraft/item/ItemStack;", ordinal = 0))
    private static void stackCraftItemMixin(JuicerBlockEntity entity, CallbackInfo info) {
        ((JuicerBlockEntityMixin) (Object) entity).setRecipeStacks(List.of(entity.getStack(0).copy(), entity.getStack(1).copy()));
    }

    @Inject(method = "craftItem", at = @At(value = "INVOKE", target = "Lcom/ianm1647/expandeddelight/block/entity/JuicerBlockEntity;resetProgress()V"))
    private static void craftItemMixin(JuicerBlockEntity entity, CallbackInfo info) {
        SpoiledUtil.setItemStackSpoilage(entity.getWorld(), entity.getStack(3), ((JuicerBlockEntityMixin) (Object) entity).getRecipeStacks());
    }

    @Inject(method = "Lcom/ianm1647/expandeddelight/block/entity/JuicerBlockEntity;hasRecipe(Lcom/ianm1647/expandeddelight/block/entity/JuicerBlockEntity;)Z", at = @At(value = "RETURN"), cancellable = true, remap = false)
    private static void hasRecipeMixin(JuicerBlockEntity entity, CallbackInfoReturnable<Boolean> info) {
        if (info.getReturnValue()) {
            if (!entity.getStack(3).isEmpty() && ItemStack.areItemsEqual(entity.getStack(3), entity.getStack(0)) && !SpoiledUtil.isSpoilageEqual(entity.getStack(3), entity.getStack(0))) {
                info.setReturnValue(false);
            }
        }
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lcom/ianm1647/expandeddelight/block/entity/JuicerBlockEntity;useStoredBottleOnJuice()V"), cancellable = true)
    private static void tickMixin(World world, BlockPos pos, BlockState state, JuicerBlockEntity entity, CallbackInfo info) {
        if (!entity.getStack(4).isEmpty() && ItemStack.areItemsEqual(entity.getStack(3), entity.getStack(4)) && !SpoiledUtil.isSpoilageEqual(entity.getStack(3), entity.getStack(4))) {
            info.cancel();
        }
    }

    private void setRecipeStacks(List<ItemStack> stacks) {
        recipeStacks.clear();
        recipeStacks.addAll(stacks);
    }

    @Nullable
    private List<ItemStack> getRecipeStacks() {
        return this.recipeStacks;
    }

}
