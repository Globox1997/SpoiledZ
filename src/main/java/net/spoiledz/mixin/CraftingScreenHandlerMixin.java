package net.spoiledz.mixin;

import java.util.ArrayList;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.spoiledz.util.SpoiledUtil;

@Mixin(CraftingMenu.class)
public class CraftingScreenHandlerMixin {

    @Inject(method = "slotChangedCraftingGrid", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/AbstractContainerMenu;setRemoteSlot(ILnet/minecraft/world/item/ItemStack;)V"))
    private static void updateResultMixin(AbstractContainerMenu handler, Level world, Player player, CraftingContainer craftingInventory, ResultContainer resultInventory,
            @Nullable RecipeHolder<CraftingRecipe> recipe, CallbackInfo info) {
        SpoiledUtil.setItemStackSpoilage(world, resultInventory.getItem(0), new ArrayList<>(craftingInventory.getItems()));
    }
}
