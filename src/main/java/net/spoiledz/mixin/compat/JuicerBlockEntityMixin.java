package net.spoiledz.mixin.compat;

import java.util.ArrayList;
import java.util.List;

import com.ianm1647.expandeddelight.block.entity.JuicerBlockEntity;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.spoiledz.util.SpoiledUtil;

@Mixin(JuicerBlockEntity.class)
public class JuicerBlockEntityMixin {

    @Unique
    private final List<ItemStack> recipeStacks = new ArrayList<ItemStack>();

    @Inject(method = "craftItem", at = @At(value = "INVOKE", target = "Lcom/ianm1647/expandeddelight/block/entity/JuicerBlockEntity;removeItem(II)Lnet/minecraft/world/item/ItemStack;", ordinal = 0))
    private static void stackCraftItemMixin(JuicerBlockEntity entity, CallbackInfo info) {
        ((JuicerBlockEntityMixin) (Object) entity).setRecipeStacks(List.of(entity.getItem(0).copy(), entity.getItem(1).copy()));
    }

    @Inject(method = "craftItem", at = @At(value = "INVOKE", target = "Lcom/ianm1647/expandeddelight/block/entity/JuicerBlockEntity;resetProgress()V"))
    private static void craftItemMixin(JuicerBlockEntity entity, CallbackInfo info) {
        SpoiledUtil.setItemStackSpoilage(entity.getLevel(), entity.getItem(3), ((JuicerBlockEntityMixin) (Object) entity).getRecipeStacks());
    }

    @Inject(method = "Lcom/ianm1647/expandeddelight/block/entity/JuicerBlockEntity;hasRecipe(Lcom/ianm1647/expandeddelight/block/entity/JuicerBlockEntity;)Z", at = @At(value = "RETURN"), cancellable = true, remap = false)
    private static void hasRecipeMixin(JuicerBlockEntity entity, CallbackInfoReturnable<Boolean> info) {
        if (info.getReturnValue() && !entity.getItem(3).isEmpty() && SpoiledUtil.isSpoilable(entity.getItem(3))) {
            for (int i = 0; i < 2; i++) {
                if (!entity.getItem(i).isEmpty() && SpoiledUtil.isSpoilable(entity.getItem(i)) && !SpoiledUtil.isSpoilageEqual(entity.getItem(3), entity.getItem(i))) {
                    info.setReturnValue(false);
                    return;
                }
            }
        }
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lcom/ianm1647/expandeddelight/block/entity/JuicerBlockEntity;useStoredBottleOnJuice()V"), cancellable = true)
    private static void tickMixin(Level world, BlockPos pos, BlockState state, JuicerBlockEntity entity, CallbackInfo info) {
        if (!entity.getItem(4).isEmpty() && ItemStack.isSameItem(entity.getItem(3), entity.getItem(4)) && !SpoiledUtil.isSpoilageEqual(entity.getItem(3), entity.getItem(4))) {
            info.cancel();
        }
    }

    @Unique
    private void setRecipeStacks(List<ItemStack> stacks) {
        recipeStacks.clear();
        recipeStacks.addAll(stacks);
    }

    @Nullable
    @Unique
    private List<ItemStack> getRecipeStacks() {
        return this.recipeStacks;
    }

}
