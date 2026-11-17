package net.spoiledz.mixin.client;

import java.util.List;

import net.minecraft.item.Item;
import net.minecraft.item.tooltip.TooltipType;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.spoiledz.util.SpoiledUtil;

@Environment(EnvType.CLIENT)
@Mixin(ItemStack.class)
public class ItemStackMixin {

    @Inject(method = "getTooltip", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;appendTooltip(Lnet/minecraft/component/ComponentType;Lnet/minecraft/item/Item$TooltipContext;Ljava/util/function/Consumer;Lnet/minecraft/item/tooltip/TooltipType;)V", ordinal = 0), locals = LocalCapture.CAPTURE_FAILSOFT)
    private void getTooltipMixin(Item.TooltipContext context, @Nullable PlayerEntity player, TooltipType type, CallbackInfoReturnable<List<Text>> info, List<Text> list) {
        ItemStack stack = (ItemStack) (Object) this;
        if (player != null && SpoiledUtil.hasSpoilage(stack) && SpoiledUtil.isSpoilable(stack)) {
            list.add(Text.translatable("item.spoiledz.tooltip", SpoiledUtil.getSpoilingTime(player.getWorld(), stack) * 25));
        }
    }


}
