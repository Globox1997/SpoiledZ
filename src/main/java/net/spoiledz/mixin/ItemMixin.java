package net.spoiledz.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.spoiledz.util.SpoiledUtil;

@Mixin(Item.class)
public class ItemMixin {

    @Inject(method = "onCraftByPlayer", at = @At("TAIL"))
    private void onCraftByPlayerMixin(ItemStack stack, World world, PlayerEntity player, CallbackInfo info) {
        if (!world.isClient() && !stack.isEmpty()) {
            SpoiledUtil.setItemStackSpoilage(world, stack, null);
        }
    }


    @Inject(method = "onCraft", at = @At("TAIL"))
    private void onCraftMixin(ItemStack stack, World world, CallbackInfo info) {
        if (!world.isClient() && !stack.isEmpty()) {
            SpoiledUtil.setItemStackSpoilage(world, stack, null);
        }
    }

}
