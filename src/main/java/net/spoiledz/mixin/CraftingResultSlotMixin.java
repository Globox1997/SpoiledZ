package net.spoiledz.mixin;

import java.util.ArrayList;
import java.util.List;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.CraftingResultSlot;
import net.spoiledz.util.SpoiledUtil;

@Mixin(CraftingResultSlot.class)
public class CraftingResultSlotMixin {

    @Shadow
    @Final
    @Mutable
    private CraftingInventory input;

    @Inject(method = "onTakeItem", at = @At("HEAD"))
    private void onTakeItemMixin(PlayerEntity player, ItemStack stack, CallbackInfo info) {
        List<ItemStack> inputStacks = new ArrayList<ItemStack>();
        for (int i = 0; i < input.size(); i++) {
            inputStacks.add(input.getStack(i));
        }
        SpoiledUtil.setItemStackSpoilage(player.world, stack, inputStacks);
    }

}
