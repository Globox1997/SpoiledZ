package net.spoiledz.init;

//import de.siphalor.capsaicin.api.food.FoodContext;
//import de.siphalor.capsaicin.api.food.FoodEvents;
//import de.siphalor.capsaicin.api.food.FoodModifications;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.spoiledz.util.SpoiledUtil;

public class EventInit {

    public static final boolean isSereneSeasonsLoaded = FabricLoader.getInstance().isModLoaded("sereneseasons");
    public static final boolean isFabricSeasonsLoaded = FabricLoader.getInstance().isModLoaded("seasons");

    public static void init() {

        // NAUSEA,WEAKNESS,HUNGER,BLINDNESS,INSTANT_DAMAGE,SLOWNESS,MINING_FATIGUE POISON

//        FoodModifications.PROPERTIES_MODIFIERS.register((foodProperties, context) -> {
//            ItemStack stack = context.stack();
//            if (stack == null || context.user() == null || stack.get(ComponentInit.SPOILED) == null) {
//                return foodProperties;
//            }
//            if (SpoiledUtil.getSpoilingTime(context.user().getWorld(), context.stack()) >= 0) {
//                int spoiledTime = SpoiledUtil.getSpoilingTime(context.user().getWorld(), context.stack());
//                if (spoiledTime == 3) {
//                    foodProperties.setHunger((int) (foodProperties.getHunger() * 0.8f));
//                } else if (spoiledTime >= 4) {
//                    foodProperties.setHunger(foodProperties.getHunger() / 2);
//                }
//            }
//            return foodProperties;
//        }, Identifier.of("spoiledz", "spoiling"));

        if (isSereneSeasonsLoaded) {
//           new SeasonChangedEvent.Standard().getNewSeason();
//            SeasonChangedEvent.Standard.register(t->{
//
//            });
        }
    }

}
