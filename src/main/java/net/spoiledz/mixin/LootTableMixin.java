package net.spoiledz.mixin;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.spoiledz.util.SpoiledUtil;

@SuppressWarnings("rawtypes")
@Mixin(LootTable.class)
public class LootTableMixin {

    @Inject(method = "fill", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/Container;setItem(ILnet/minecraft/world/item/ItemStack;)V", ordinal = 1), locals = LocalCapture.CAPTURE_FAILSOFT)
    private void supplyInventoryMixin(Container inventory, LootParams parameters, long seed, CallbackInfo info, LootContext lootContext, ObjectArrayList objectArrayList, RandomSource random,
            List list, ObjectListIterator var9, ItemStack itemStack) {
        SpoiledUtil.setItemStackSpoilage(parameters.getLevel(), itemStack, null);
    }
}
