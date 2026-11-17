package net.spoiledz.mixin.compat;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.spoiledz.util.SpoiledUtil;
import vectorwing.farmersdelight.common.item.SkilletItem;

@Mixin(SkilletItem.class)
public class SkilletItemMixin {

    @Inject(method = "lambda$finishUsingItem$0", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Inventory;add(Lnet/minecraft/world/item/ItemStack;)Z"), locals = LocalCapture.CAPTURE_FAILSOFT, require = 0)
    private static void finishUsingMixin(ItemStack cookingStack, Level world, Player player, ItemStack skilletStack, RecipeHolder<?> recipe, CallbackInfo info, ItemStack resultStack) {
        SpoiledUtil.setItemStackSpoilage(world, resultStack, List.of(cookingStack));
    }
}
