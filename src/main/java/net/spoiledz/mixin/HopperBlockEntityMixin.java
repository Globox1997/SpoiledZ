package net.spoiledz.mixin;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.block.entity.DecoratedPotBlockEntity;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.spoiledz.util.SpoiledUtil;

@Mixin(HopperBlockEntity.class)
public class HopperBlockEntityMixin {

    @Inject(method = "transfer(Lnet/minecraft/inventory/Inventory;Lnet/minecraft/inventory/Inventory;Lnet/minecraft/item/ItemStack;ILnet/minecraft/util/math/Direction;)Lnet/minecraft/item/ItemStack;", at = @At("HEAD"))
    private static void transferMixin(@Nullable Inventory from, Inventory to, ItemStack stack, int slot, @Nullable Direction side, CallbackInfoReturnable<ItemStack> info) {
        World world = SpoiledUtil.getInventoryWorld(to);
        if (world == null) {
            world = SpoiledUtil.getInventoryWorld(from);
        }
        if (world != null) {
            SpoiledUtil.onStackSeen(world, stack);
            if (SpoiledUtil.onStackSeen(world, to.getStack(slot), !(to instanceof DecoratedPotBlockEntity))) {
                to.markDirty();
            }
        }
    }
}
