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
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.ResultSlot;
import net.minecraft.world.item.ItemStack;
import net.spoiledz.util.SpoiledUtil;

@Mixin(ResultSlot.class)
public class CraftingResultSlotMixin {

    @Shadow
    @Final
    @Mutable
    private CraftingContainer craftSlots;

    @Inject(method = "onTake", at = @At("HEAD"))
    private void onTakeItemMixin(Player player, ItemStack stack, CallbackInfo info) {
        List<ItemStack> inputStacks = new ArrayList<ItemStack>();
        for (int i = 0; i < craftSlots.getContainerSize(); i++) {
            inputStacks.add(craftSlots.getItem(i));
        }
        SpoiledUtil.setItemStackSpoilage(player.level(), stack, inputStacks);
    }

}
