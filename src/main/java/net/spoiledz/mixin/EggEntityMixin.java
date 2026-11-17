package net.spoiledz.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import net.minecraft.world.entity.projectile.ThrownEgg;
import net.spoiledz.util.SpoiledUtil;

@Mixin(ThrownEgg.class)
public class EggEntityMixin {

    @ModifyArg(method = "onHit", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/RandomSource;nextInt(I)I", ordinal = 0))
    private int onCollisionMixin(int bound) {
        ThrownEgg eggEntity = (ThrownEgg) (Object) this;
        int spoiledTime = SpoiledUtil.getSpoilingTime(eggEntity.level(), eggEntity.getItem());
        if (spoiledTime >= 4) {
            return Integer.MAX_VALUE;
        } else if (spoiledTime == 3) {
            return bound * 2;
        }
        return bound;
    }
}
