package net.spoiledz.init;

import de.siphalor.capsaicin.api.food.FoodContext;
import de.siphalor.capsaicin.api.food.FoodEvents;
import de.siphalor.capsaicin.api.food.FoodModifications;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.spoiledz.util.SpoiledUtil;

public class EventInit {

    public static void init() {

        // NAUSEA,WEAKNESS,HUNGER,BLINDNESS,INSTANT_DAMAGE,SLOWNESS,MINING_FATIGUE POISON
        FoodEvents.EATEN.on((event) -> {
            FoodContext context = event.context();
            if (context.stack() != null && context.user() != null) {
                if (!context.user().getWorld().isClient() && SpoiledUtil.getSpoilingTime(context.user().getWorld(), context.stack()) >= 0) {
                    int spoiledTime = SpoiledUtil.getSpoilingTime(context.user().getWorld(), context.stack());

                    if (spoiledTime == 3 && context.user().getWorld().getRandom().nextFloat() < (float) (ConfigInit.CONFIG.effectChance / 100f)) {
                        context.user().addStatusEffect(new StatusEffectInstance(StatusEffects.POISON, (int) (ConfigInit.CONFIG.effectDuration * 0.66f), 1));
                    } else if (spoiledTime >= 4) {
                        context.user().addStatusEffect(new StatusEffectInstance(StatusEffects.NAUSEA, ConfigInit.CONFIG.effectDuration / 2, 0));
                        context.user().addStatusEffect(new StatusEffectInstance(StatusEffects.POISON, ConfigInit.CONFIG.effectDuration, 1));
                        if (context.user().getWorld().getRandom().nextFloat() < 0.05f)
                            context.user().addStatusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS, ConfigInit.CONFIG.effectDuration / 3, 0));
                    }
                }
            }
        });
        FoodModifications.PROPERTIES_MODIFIERS.register((foodProperties, context) -> {
            ItemStack stack = context.stack();
            if (stack == null || context.user() == null || !stack.hasNbt()) {
                return foodProperties;
            }
            if (SpoiledUtil.getSpoilingTime(context.user().getWorld(), context.stack()) >= 0) {
                int spoiledTime = SpoiledUtil.getSpoilingTime(context.user().getWorld(), context.stack());
                if (spoiledTime == 3) {
                    foodProperties.setHunger((int) (foodProperties.getHunger() * 0.8f));
                } else if (spoiledTime >= 4) {
                    foodProperties.setHunger(foodProperties.getHunger() / 2);
                }
            }
            return foodProperties;
        }, new Identifier("spoiledz", "spoiling"));
    }

}
