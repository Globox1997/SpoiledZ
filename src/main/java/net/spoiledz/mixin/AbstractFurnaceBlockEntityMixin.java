package net.spoiledz.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.spoiledz.mixin.access.AbstractFurnaceBlockEntityAccessor;
import net.spoiledz.util.SpoiledComponent;
import net.spoiledz.util.SpoiledUtil;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(AbstractFurnaceBlockEntity.class)
public class AbstractFurnaceBlockEntityMixin {


    @Nullable
    @Unique
    private ItemStack recipeStack = null;

    // No world instance given :/
    // @Inject(method = "craftRecipe", at = @At(value = "INVOKE", target = ""), locals = LocalCapture.PRINT)
    // private static void craftRecipeMixin(@Nullable Recipe<?> recipe, DefaultedList<ItemStack> slots, int count, CallbackInfoReturnable<Boolean> info) {
    // }

    @Inject(method = "serverTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/entity/AbstractFurnaceBlockEntity;burn(Lnet/minecraft/core/NonNullList;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;)V"))
    private static void stackTickMixin(ServerLevel world, BlockPos pos, BlockState state, AbstractFurnaceBlockEntity blockEntity, CallbackInfo info) {
        ((AbstractFurnaceBlockEntityMixin) (Object) blockEntity).setRecipeStack(((AbstractFurnaceBlockEntityAccessor) blockEntity).getInventory().get(0));
    }

    @Inject(method = "serverTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/entity/AbstractFurnaceBlockEntity;setRecipeUsed(Lnet/minecraft/world/item/crafting/RecipeHolder;)V"))
    private static void tickMixin(ServerLevel world, BlockPos pos, BlockState state, AbstractFurnaceBlockEntity blockEntity, CallbackInfo info) {
        ItemStack outputStack = blockEntity.getItem(2);
        SpoiledComponent previousSpoiledComponent = SpoiledUtil.getSpoiledComponent(outputStack);
        List<ItemStack> recipeStacks = new ArrayList<>();
        ItemStack recipeStack = ((AbstractFurnaceBlockEntityMixin) (Object) blockEntity).getRecipeStack();
        if (recipeStack != null) {
            recipeStacks.add(recipeStack);
        }
        SpoiledUtil.setItemStackSpoilage(world, outputStack, recipeStacks);
        SpoiledUtil.keepOlderSpoilage(outputStack, previousSpoiledComponent);
    }

//    @Inject(method = "canAcceptRecipeOutput", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;areItemsAndComponentsEqual(Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemStack;)Z"), cancellable = true, locals = LocalCapture.CAPTURE_FAILSOFT)
//    private static void canAcceptRecipeOutputMixin(DynamicRegistryManager registryManager, @Nullable RecipeEntry<?> recipe, DefaultedList<ItemStack> slots, int count, CallbackInfoReturnable<Boolean> info, ItemStack itemStack, ItemStack itemStack2) {
//        if (ItemStack.areItemsEqual(itemStack, itemStack2) && SpoiledUtil.isSpoilable(slots.get(0)) && SpoiledUtil.isSpoilable(itemStack2)) {
//            if (!SpoiledUtil.isSpoilageEqual(slots.get(0), itemStack2)) {
//                info.setReturnValue(false);
//            } else if (itemStack2.getCount() < count && itemStack2.getCount() < itemStack2.getMaxCount()) {
//                info.setReturnValue(true);
//            }
//        }
//    }

    @Unique
    private void setRecipeStack(ItemStack stack) {
        recipeStack = stack.copy();
    }


    @Nullable
    @Unique
    private ItemStack getRecipeStack() {
        return this.recipeStack;
    }
}
