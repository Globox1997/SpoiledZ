package net.spoiledz.mixin;

import java.util.Iterator;
import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContext;
import net.spoiledz.util.SpoiledUtil;

@SuppressWarnings("rawtypes")
@Mixin(FishingBobberEntity.class)
public class FishingBobberEntityMixin {

    @Inject(method = "use", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;spawnEntity(Lnet/minecraft/entity/Entity;)Z", ordinal = 0), locals = LocalCapture.CAPTURE_FAILSOFT)
    public void useMixin(ItemStack usedItem, CallbackInfoReturnable<Integer> info, PlayerEntity playerEntity, int i, LootContext.Builder builder, LootTable lootTable, List list, Iterator var7,
            ItemStack itemStack, ItemEntity itemEntity) {
        SpoiledUtil.setItemStackSpoilage(playerEntity.world, itemEntity.getStack());
    }
}
