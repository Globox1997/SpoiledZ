package net.spoiledz.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import net.minecraft.entity.projectile.thrown.EggEntity;
import net.spoiledz.util.SpoiledUtil;

@Mixin(EggEntity.class)
public class EggEntityMixin {

    @ModifyArg(method = "onCollision", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/random/Random;nextInt(I)I", ordinal = 0))
    private int onCollisionMixin(int bound) {
        EggEntity eggEntity = (EggEntity) (Object) this;
        int spoiledTime = SpoiledUtil.getSpoilingTime(eggEntity.getWorld(), eggEntity.getStack());
        if (spoiledTime >= 4) {
            return Integer.MAX_VALUE;
        } else if (spoiledTime == 3) {
            return bound * 2;
        }
        return bound;
    }
}
