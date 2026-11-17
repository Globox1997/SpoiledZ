package net.spoiledz.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.server.level.ServerLevel;
import net.spoiledz.access.ServerWorldAccess;
import net.spoiledz.util.SpoiledUtil.FoodBlockState;

@Mixin(ServerLevel.class)
public abstract class ServerWorldMixin implements ServerWorldAccess {

    @Unique
    private FoodBlockState foodBlockState;

    @Inject(at = @At("TAIL"), method = "<init>")
    private void initMixin(CallbackInfo info) {
        this.foodBlockState = FoodBlockState.getOrCreate((ServerLevel) (Object) this);
    }

    @Override
    public FoodBlockState getFoodBlockState() {
        return this.foodBlockState;
    }

}
