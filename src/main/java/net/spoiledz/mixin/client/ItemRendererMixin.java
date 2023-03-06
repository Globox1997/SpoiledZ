package net.spoiledz.mixin.client;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.item.ItemStack;

@SuppressWarnings("unused")
@Environment(EnvType.CLIENT)
@Mixin(ItemRenderer.class)
public class ItemRendererMixin {

    // @Inject(method = "renderGuiItemModel", at = @At(""))
    // protected void renderGuiItemModelMixin(ItemStack stack, int x, int y, BakedModel model, CallbackInfo info) {
    // }

    // @ModifyConstant(method = "Lnet/minecraft/client/render/item/ItemRenderer;renderGuiItemOverlay(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V",
    // constant = @Constant(intValue = 255))
    // private int renderGuiItemOverlayMixin(int original) {
    // // System.out.println("TEST");
    // return 0;
    // }

    // @ModifyConstant(method = "Lnet/minecraft/client/render/item/ItemRenderer;renderGuiItemOverlay(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V",
    // constant = @Constant(intValue = 255, ordinal = 0))
    // private int renderGuiItemOverlayMixin(int original) {
    // System.out.println("TEST");
    // return 0;
    // }

    // @Inject(method = "", at = @At(value = "", target = ""))
    // private void renderGuiItemOverlay(TextRenderer renderer, ItemStack stack, int x, int y, @Nullable String countLabel, CallbackInfo info) {
    // }
}
