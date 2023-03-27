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

    @Inject(method = "onCraft", at = @At("HEAD"))
    private void onCraftMixin(ItemStack stack, World world, PlayerEntity player, CallbackInfo info) {
        SpoiledUtil.setItemStackSpoilage(world, stack, null);
    }

}
