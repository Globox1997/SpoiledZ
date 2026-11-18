package net.spoiledz.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.spoiledz.util.SpoiledUtil;

@Mixin(ServerPlayer.class)
public class ServerPlayerEntityMixin {

    @Inject(method = "initMenu", at = @At("HEAD"))
    private void onScreenHandlerOpenedMixin(AbstractContainerMenu screenHandler, CallbackInfo info) {
        ServerPlayer player = (ServerPlayer) (Object) this;
        for (Slot slot : screenHandler.slots) {
            if (SpoiledUtil.onStackSeen(player.level(), slot.getItem())) {
                slot.setChanged();
            }
        }
    }
}
