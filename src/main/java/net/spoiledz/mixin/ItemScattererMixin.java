package net.spoiledz.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ItemScatterer;
import net.minecraft.world.World;
import net.spoiledz.util.SpoiledUtil;

@Mixin(ItemScatterer.class)
public class ItemScattererMixin {

    @Inject(method = "Lnet/minecraft/util/ItemScatterer;spawn(Lnet/minecraft/world/World;DDDLnet/minecraft/item/ItemStack;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;spawnEntity(Lnet/minecraft/entity/Entity;)Z"), locals = LocalCapture.CAPTURE_FAILSOFT)
    private static void spawnMixin(World world, double x, double y, double z, ItemStack stack, CallbackInfo info, double d, double e, double f, double g, double h, double i, ItemEntity itemEntity) {
        SpoiledUtil.setItemStackSpoilage(world, itemEntity.getStack());
    }
}
