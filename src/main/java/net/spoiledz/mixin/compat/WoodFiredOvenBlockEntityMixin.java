package net.spoiledz.mixin.compat;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.spoiledz.util.SpoiledUtil;
import satisfyu.vinery.block.entity.WoodFiredOvenBlockEntity;
import satisfyu.vinery.recipe.WoodFiredOvenRecipe;

@Mixin(WoodFiredOvenBlockEntity.class)
public abstract class WoodFiredOvenBlockEntityMixin extends BlockEntity {

    @Shadow
    private DefaultedList<ItemStack> inventory;

    public WoodFiredOvenBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Inject(method = "canCraft", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isItemEqualIgnoreDamage(Lnet/minecraft/item/ItemStack;)Z"), locals = LocalCapture.CAPTURE_FAILSOFT)
    protected void canCraftMixin(WoodFiredOvenRecipe recipe, CallbackInfoReturnable<Boolean> info, ItemStack recipeOutput, ItemStack outputSlotStack) {
        if (outputSlotStack.isItemEqualIgnoreDamage(recipeOutput) && ((!inventory.get(0).isEmpty() && !SpoiledUtil.isSpoilageEqual(outputSlotStack, inventory.get(0)))
                || (!inventory.get(1).isEmpty() && !SpoiledUtil.isSpoilageEqual(outputSlotStack, inventory.get(1))
                        || (!inventory.get(2).isEmpty() && !SpoiledUtil.isSpoilageEqual(outputSlotStack, inventory.get(2)))))) {
            info.setReturnValue(false);
        }

    }

    @Inject(method = "craft", at = @At(value = "INVOKE_ASSIGN", target = "Lsatisfyu/vinery/block/entity/WoodFiredOvenBlockEntity;getStack(I)Lnet/minecraft/item/ItemStack;", ordinal = 0), locals = LocalCapture.CAPTURE_FAILSOFT)
    protected void craftMixin(WoodFiredOvenRecipe recipe, CallbackInfo info, ItemStack recipeOutput, ItemStack outputSlotStack) {
        SpoiledUtil.setItemStackSpoilage(this.world, outputSlotStack, List.of(this.inventory.get(0), this.inventory.get(1), this.inventory.get(2)));
    }
}
