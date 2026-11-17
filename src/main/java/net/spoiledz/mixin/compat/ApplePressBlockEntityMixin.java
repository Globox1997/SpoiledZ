package net.spoiledz.mixin.compat;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.satisfy.vinery.core.block.entity.ApplePressBlockEntity;
import net.satisfy.vinery.core.recipe.ApplePressFermentingRecipe;
import net.satisfy.vinery.core.recipe.ApplePressMashingRecipe;
import net.spoiledz.util.SpoiledComponent;
import net.spoiledz.util.SpoiledUtil;

@Mixin(ApplePressBlockEntity.class)
public class ApplePressBlockEntityMixin {

    @Unique
    private final List<ItemStack> recipeStacks = new ArrayList<>();

    @Nullable
    @Unique
    private SpoiledComponent previousSpoiledComponent = null;

    @Inject(method = "processMashing", at = @At("HEAD"), require = 0)
    private static void processMashingHeadMixin(ApplePressBlockEntity entity, ApplePressMashingRecipe recipe, CallbackInfo info) {
        ((ApplePressBlockEntityMixin) (Object) entity).storeRecipeStacks(entity, 1, 0);
    }

    @Inject(method = "processMashing", at = @At("RETURN"), require = 0)
    private static void processMashingReturnMixin(ApplePressBlockEntity entity, ApplePressMashingRecipe recipe, CallbackInfo info) {
        ((ApplePressBlockEntityMixin) (Object) entity).applyRecipeStacks(entity, 1);
    }

    @Inject(method = "processFermenting", at = @At("HEAD"), require = 0)
    private static void processFermentingHeadMixin(ApplePressBlockEntity entity, ApplePressFermentingRecipe recipe, CallbackInfo info) {
        ((ApplePressBlockEntityMixin) (Object) entity).storeRecipeStacks(entity, 3, 1);
    }

    @Inject(method = "processFermenting", at = @At("RETURN"), require = 0)
    private static void processFermentingReturnMixin(ApplePressBlockEntity entity, ApplePressFermentingRecipe recipe, CallbackInfo info) {
        ((ApplePressBlockEntityMixin) (Object) entity).applyRecipeStacks(entity, 3);
    }

    @Unique
    private void storeRecipeStacks(ApplePressBlockEntity entity, int outputSlot, int inputSlot) {
        this.recipeStacks.clear();
        this.recipeStacks.add(entity.getStack(inputSlot).copy());
        this.previousSpoiledComponent = SpoiledUtil.getSpoiledComponent(entity.getStack(outputSlot));
    }

    @Unique
    private void applyRecipeStacks(ApplePressBlockEntity entity, int outputSlot) {
        World world = entity.getWorld();
        if (world != null) {
            ItemStack outputStack = entity.getStack(outputSlot);
            SpoiledUtil.setItemStackSpoilage(world, outputStack, new ArrayList<>(this.recipeStacks));
            SpoiledUtil.keepOlderSpoilage(outputStack, this.previousSpoiledComponent);
        }
        this.recipeStacks.clear();
        this.previousSpoiledComponent = null;
    }
}
