package net.spoiledz.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.spoiledz.util.SpoiledUtil;

@Mixin(Item.class)
public class ItemMixin {

    @Inject(method = "onCraftedBy", at = @At("TAIL"))
    private void onCraftByPlayerMixin(ItemStack stack, Level world, Player player, CallbackInfo info) {
        if (!world.isClientSide() && !stack.isEmpty()) {
            SpoiledUtil.setItemStackSpoilage(world, stack, null);
        }
    }


    @Inject(method = "onCraftedPostProcess", at = @At("TAIL"))
    private void onCraftMixin(ItemStack stack, Level world, CallbackInfo info) {
        if (!world.isClientSide() && !stack.isEmpty()) {
            SpoiledUtil.setItemStackSpoilage(world, stack, null);
        }
    }

}
