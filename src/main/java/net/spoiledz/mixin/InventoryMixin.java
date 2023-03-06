package net.spoiledz.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;

@SuppressWarnings("unused")
@Mixin(Inventory.class)
public interface InventoryMixin {

    // maybe used for other system
    // @Inject(method = "onOpen", at = @At("HEAD"))
    // default public void onOpenMixin(PlayerEntity player, CallbackInfo info) {
    //     System.out.println("on Open " + player);
    //     if (!player.world.isClient)
    //         for (int j = 0; j < this.size(); ++j) {
    //             // if (getStack(j) != null && !getStack(j).isEmpty())
    //             // System.out.println(getStack(j));
    //             // ChestBlockEntity
    //         }
    // }

    // @Shadow
    // public ItemStack getStack(int var1);

    // @Shadow
    // public int size();

}
