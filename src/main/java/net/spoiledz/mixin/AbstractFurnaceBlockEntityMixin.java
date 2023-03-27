package net.spoiledz.mixin;

import java.util.List;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.spoiledz.mixin.access.AbstractFurnaceBlockEntityAccessor;
import net.spoiledz.util.SpoiledUtil;

@Mixin(AbstractFurnaceBlockEntity.class)
public class AbstractFurnaceBlockEntityMixin {

    @Nullable
    private ItemStack recipeStack = null;

    @Shadow
    protected DefaultedList<ItemStack> inventory = DefaultedList.ofSize(3, ItemStack.EMPTY);

    // No world instance given :/
    // @Inject(method = "craftRecipe", at = @At(value = "INVOKE", target = ""), locals = LocalCapture.PRINT)
    // private static void craftRecipeMixin(@Nullable Recipe<?> recipe, DefaultedList<ItemStack> slots, int count, CallbackInfoReturnable<Boolean> info) {
    // }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/entity/AbstractFurnaceBlockEntity;craftRecipe(Lnet/minecraft/recipe/Recipe;Lnet/minecraft/util/collection/DefaultedList;I)Z"))
    private static void stackTickMixin(World world, BlockPos pos, BlockState state, AbstractFurnaceBlockEntity blockEntity, CallbackInfo info) {
        ((AbstractFurnaceBlockEntityMixin) (Object) blockEntity).setRecipeStack(((AbstractFurnaceBlockEntityAccessor) blockEntity).getInventory().get(0));
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/entity/AbstractFurnaceBlockEntity;setLastRecipe(Lnet/minecraft/recipe/Recipe;)V"))
    private static void tickMixin(World world, BlockPos pos, BlockState state, AbstractFurnaceBlockEntity blockEntity, CallbackInfo info) {
        SpoiledUtil.setItemStackSpoilage(world, blockEntity.getStack(2), List.of(((AbstractFurnaceBlockEntityMixin) (Object) blockEntity).getRecipeStack()));
    }

    @Inject(method = "canAcceptRecipeOutput", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isItemEqualIgnoreDamage(Lnet/minecraft/item/ItemStack;)Z"), cancellable = true, locals = LocalCapture.CAPTURE_FAILSOFT)
    private static void canAcceptRecipeOutputMixin(@Nullable Recipe<?> recipe, DefaultedList<ItemStack> slots, int count, CallbackInfoReturnable<Boolean> info, ItemStack itemStack,
            ItemStack itemStack2) {
        if (itemStack.isItemEqualIgnoreDamage(itemStack2) && !SpoiledUtil.isSpoilageEqual(itemStack, itemStack2)) {
            info.setReturnValue(false);
        }
    }

    private void setRecipeStack(ItemStack stack) {
        recipeStack = stack.copy();
    }

    @Nullable
    private ItemStack getRecipeStack() {
        return this.recipeStack;
    }
}
