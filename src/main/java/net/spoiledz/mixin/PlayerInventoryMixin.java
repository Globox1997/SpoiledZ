package net.spoiledz.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.spoiledz.util.SpoiledUtil;

@Mixin(PlayerInventory.class)
public class PlayerInventoryMixin {

    @Inject(method = "updateItems", at = @At("HEAD"))
    private void updateItemsMixin(CallbackInfo info) {
        PlayerInventory inventory = (PlayerInventory) (Object) this;
        PlayerEntity player = inventory.player;
        if (!player.getWorld().isClient()) {
            for (int i = 0; i < inventory.size(); i++) {
                SpoiledUtil.onStackSeen(player.getWorld(), inventory.getStack(i));
            }
            if (player.currentScreenHandler != null) {
                SpoiledUtil.onStackSeen(player.getWorld(), player.currentScreenHandler.getCursorStack());
            }
        }
    }
}
