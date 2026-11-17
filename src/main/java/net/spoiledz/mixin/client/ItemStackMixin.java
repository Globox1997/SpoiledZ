package net.spoiledz.mixin.client;

import java.util.function.Consumer;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.spoiledz.util.SpoiledUtil;

@Environment(EnvType.CLIENT)
@Mixin(ItemStack.class)
public class ItemStackMixin {

    @Inject(method = "addDetailsToTooltip", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;addToTooltip(Lnet/minecraft/core/component/DataComponentType;Lnet/minecraft/world/item/Item$TooltipContext;Lnet/minecraft/world/item/component/TooltipDisplay;Ljava/util/function/Consumer;Lnet/minecraft/world/item/TooltipFlag;)V", ordinal = 0))
    private void getTooltipMixin(Item.TooltipContext context, TooltipDisplay display, @Nullable Player player, TooltipFlag type, Consumer<Component> builder, CallbackInfo info) {
        ItemStack stack = (ItemStack) (Object) this;
        if (player != null && SpoiledUtil.hasSpoilage(stack) && SpoiledUtil.isSpoilable(stack)) {
            builder.accept(Component.translatable("item.spoiledz.tooltip", SpoiledUtil.getSpoilingTime(player.level(), stack) * 25));
        }
    }

}
