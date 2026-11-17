package net.spoiledz.mixin.compat;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.spoiledz.util.SpoiledComponent;
import net.spoiledz.util.SpoiledUtil;
import vectorwing.farmersdelight.common.block.entity.CookingPotBlockEntity;
import vectorwing.farmersdelight.common.crafting.CookingPotRecipe;
import vectorwing.farmersdelight.refabricated.inventory.ItemStackHandler;

@Mixin(CookingPotBlockEntity.class)
public class CookingPotBlockEntityMixin {

    @Nullable
    @Unique
    private SpoiledComponent mealSpoiledComponent = null;

    @Inject(method = "processCooking", at = @At(value = "INVOKE", target = "Lvectorwing/farmersdelight/common/block/entity/CookingPotBlockEntity;setRecipeUsed(Lnet/minecraft/world/item/crafting/RecipeHolder;)V"), require = 0)
    private void processCookingMixin(RecipeHolder<CookingPotRecipe> recipe, CookingPotBlockEntity cookingPot, CallbackInfoReturnable<Boolean> info) {
        CookingPotBlockEntity entity = (CookingPotBlockEntity) (Object) this;
        Level world = entity.getLevel();
        if (world != null) {
            ItemStackHandler inventory = entity.getInventory();
            ItemStack mealStack = inventory.getStackInSlot(CookingPotBlockEntity.MEAL_DISPLAY_SLOT);
            SpoiledComponent previousSpoiledComponent = SpoiledUtil.getSpoiledComponent(mealStack);
            List<ItemStack> recipeStacks = new ArrayList<>();
            for (int i = 0; i < CookingPotBlockEntity.MEAL_DISPLAY_SLOT; i++) {
                recipeStacks.add(inventory.getStackInSlot(i));
            }
            SpoiledUtil.setItemStackSpoilage(world, mealStack, recipeStacks);
            SpoiledUtil.keepOlderSpoilage(mealStack, previousSpoiledComponent);
        }
    }

    @Inject(method = {"moveMealToOutput", "useStoredContainersOnMeal"}, at = @At("HEAD"), require = 0)
    private void moveMealToOutputHeadMixin(CallbackInfo info) {
        this.mealSpoiledComponent = SpoiledUtil.getSpoiledComponent(((CookingPotBlockEntity) (Object) this).getInventory().getStackInSlot(CookingPotBlockEntity.MEAL_DISPLAY_SLOT));
    }

    @Inject(method = {"moveMealToOutput", "useStoredContainersOnMeal"}, at = @At("RETURN"), require = 0)
    private void moveMealToOutputReturnMixin(CallbackInfo info) {
        SpoiledUtil.keepOlderSpoilage(((CookingPotBlockEntity) (Object) this).getInventory().getStackInSlot(CookingPotBlockEntity.OUTPUT_SLOT), this.mealSpoiledComponent);
        this.mealSpoiledComponent = null;
    }
}
