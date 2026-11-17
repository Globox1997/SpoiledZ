package net.spoiledz.mixin;

import java.util.ArrayList;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftingResultInventory;
import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.screen.CraftingScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.world.World;
import net.spoiledz.util.SpoiledUtil;

@Mixin(CraftingScreenHandler.class)
public class CraftingScreenHandlerMixin {

    @Inject(method = "updateResult", at = @At(value = "INVOKE", target = "Lnet/minecraft/screen/ScreenHandler;setPreviousTrackedSlot(ILnet/minecraft/item/ItemStack;)V"))
    private static void updateResultMixin(ScreenHandler handler, World world, PlayerEntity player, RecipeInputInventory craftingInventory, CraftingResultInventory resultInventory,
            @Nullable RecipeEntry<CraftingRecipe> recipe, CallbackInfo info) {
        SpoiledUtil.setItemStackSpoilage(world, resultInventory.getStack(0), new ArrayList<>(craftingInventory.getHeldStacks()));
    }
}
