package net.spoiledz.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.consume_effects.ClearAllStatusEffectsConsumeEffect;
import net.minecraft.world.level.Level;
import net.spoiledz.util.SpoiledUtil;

@Mixin(ClearAllStatusEffectsConsumeEffect.class)
public class ClearAllStatusEffectsConsumeEffectMixin {

    @Inject(method = "apply", at = @At("RETURN"))
    private void applyMixin(Level level, ItemStack stack, LivingEntity user, CallbackInfoReturnable<Boolean> info) {
        SpoiledUtil.applySpoiledEffects(user, SpoiledUtil.getSpoilingTime(level, stack));
    }
}
