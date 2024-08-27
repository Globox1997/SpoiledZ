package net.spoiledz.mixin;

import net.minecraft.component.type.FoodComponent;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.spoiledz.init.ComponentInit;
import net.spoiledz.init.ConfigInit;
import net.spoiledz.util.SpoiledUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {

    @ModifyVariable(method = "eatFood", at = @At("HEAD"), ordinal = 0)
    private FoodComponent eatFoodMixin(FoodComponent original, World world, ItemStack stack, FoodComponent foodComponent) {
        PlayerEntity playerEntity = (PlayerEntity) (Object) this;
        if (stack != null && playerEntity != null && stack.get(ComponentInit.SPOILED) != null) {
            if (SpoiledUtil.getSpoilingTime(playerEntity.getWorld(), stack) >= 0) {
                int spoiledTime = SpoiledUtil.getSpoilingTime(playerEntity.getWorld(), stack);
                if (spoiledTime == 3) {
                    return new FoodComponent((int)(original.nutrition()*0.8f), original.saturation()*0.8f,original.canAlwaysEat(),original.eatSeconds(),original.usingConvertsTo(),original.effects());
                } else if (spoiledTime >= 4) {
                    return new FoodComponent((int)(original.nutrition()/ 2), original.saturation()/2,original.canAlwaysEat(),original.eatSeconds(),original.usingConvertsTo(),original.effects());
                }
            }
        }
        return original;
    }

    @Inject(method = "eatFood", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;eatFood(Lnet/minecraft/world/World;Lnet/minecraft/item/ItemStack;Lnet/minecraft/component/type/FoodComponent;)Lnet/minecraft/item/ItemStack;"))
    private void eatFoodMixin(World world, ItemStack stack, FoodComponent foodComponent, CallbackInfoReturnable<ItemStack> info) {
        PlayerEntity playerEntity = (PlayerEntity) (Object) this;
        if (stack != null && playerEntity != null) {
            if (!playerEntity.getWorld().isClient() && SpoiledUtil.getSpoilingTime(playerEntity.getWorld(), stack) >= 0) {
                int spoiledTime = SpoiledUtil.getSpoilingTime(playerEntity.getWorld(), stack);

                if (spoiledTime == 3 && playerEntity.getWorld().getRandom().nextFloat() < (float) (ConfigInit.CONFIG.effectChance / 100f)) {
                    playerEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.POISON, (int) (ConfigInit.CONFIG.effectDuration * 0.66f), 1));
                } else if (spoiledTime >= 4) {
                    playerEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.NAUSEA, ConfigInit.CONFIG.effectDuration / 2, 0));
                    playerEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.POISON, ConfigInit.CONFIG.effectDuration, 1));
                    if (playerEntity.getWorld().getRandom().nextFloat() < 0.05f)
                        playerEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS, ConfigInit.CONFIG.effectDuration / 3, 0));
                }
            }
        }
    }
}
