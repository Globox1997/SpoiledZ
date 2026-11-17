package net.spoiledz.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.entity.ItemEntity;
import net.spoiledz.util.SpoiledUtil;

@Mixin(ItemEntity.class)
public class ItemEntityMixin {

    @Inject(method = "tick", at = @At("HEAD"))
    private void tickMixin(CallbackInfo info) {
        ItemEntity itemEntity = (ItemEntity) (Object) this;
        if (itemEntity.age % 20 == 0) {
            SpoiledUtil.onStackSeen(itemEntity.getWorld(), itemEntity.getStack());
        }
    }
}
