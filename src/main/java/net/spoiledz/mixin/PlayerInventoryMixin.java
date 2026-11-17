package net.spoiledz.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.spoiledz.util.SpoiledUtil;

@Mixin(Inventory.class)
public class PlayerInventoryMixin {

    @Inject(method = "tick", at = @At("HEAD"))
    private void updateItemsMixin(CallbackInfo info) {
        Inventory inventory = (Inventory) (Object) this;
        Player player = inventory.player;
        if (!player.level().isClientSide()) {
            for (int i = 0; i < inventory.getContainerSize(); i++) {
                SpoiledUtil.onStackSeen(player.level(), inventory.getItem(i));
            }
            if (player.containerMenu != null) {
                SpoiledUtil.onStackSeen(player.level(), player.containerMenu.getCarried());
            }
        }
    }
}
