package net.spoiledz.mixin.compat;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.world.World;
import net.spoiledz.util.SpoiledUtil;
import vectorwing.farmersdelight.common.item.SkilletItem;

@Mixin(SkilletItem.class)
public class SkilletItemMixin {

    @Inject(method = "lambda$finishUsingItem$0", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerInventory;insertStack(Lnet/minecraft/item/ItemStack;)Z"), locals = LocalCapture.CAPTURE_FAILSOFT, require = 0)
    private static void finishUsingMixin(ItemStack cookingStack, World world, PlayerEntity player, ItemStack skilletStack, RecipeEntry<?> recipe, CallbackInfo info, ItemStack resultStack) {
        SpoiledUtil.setItemStackSpoilage(world, resultStack, List.of(cookingStack));
    }
}
