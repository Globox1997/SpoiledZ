package net.spoiledz.mixin;

import java.util.function.Supplier;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.block.Block;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.spoiledz.util.SpoiledUtil;

@Mixin(Block.class)
public class BlockMixin {

    @Inject(method = "Lnet/minecraft/block/Block;dropStack(Lnet/minecraft/world/World;Ljava/util/function/Supplier;Lnet/minecraft/item/ItemStack;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/ItemEntity;setToDefaultPickupDelay()V"), locals = LocalCapture.CAPTURE_FAILSOFT)
    private static void dropStackMixin(World world, Supplier<ItemEntity> itemEntitySupplier, ItemStack stack, CallbackInfo info, ItemEntity itemEntity) {
        SpoiledUtil.setItemStackSpoilage(world, itemEntity.getStack(), null);
    }
}
