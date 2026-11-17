package net.spoiledz.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;
import net.spoiledz.util.SpoiledUtil;

@Mixin(ServerPlayerEntity.class)
public class ServerPlayerEntityMixin {

    @Inject(method = "onScreenHandlerOpened", at = @At("HEAD"))
    private void onScreenHandlerOpenedMixin(ScreenHandler screenHandler, CallbackInfo info) {
        ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;
        for (Slot slot : screenHandler.slots) {
            if (SpoiledUtil.onStackSeen(player.getWorld(), slot.getStack())) {
                slot.markDirty();
            }
        }
    }
}
