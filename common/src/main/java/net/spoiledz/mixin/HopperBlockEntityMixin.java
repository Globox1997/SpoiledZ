package net.spoiledz.mixin;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import net.minecraft.core.Direction;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.DecoratedPotBlockEntity;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.spoiledz.util.SpoiledUtil;

@Mixin(HopperBlockEntity.class)
public class HopperBlockEntityMixin {

    @Inject(method = "tryMoveInItem(Lnet/minecraft/world/Container;Lnet/minecraft/world/Container;Lnet/minecraft/world/item/ItemStack;ILnet/minecraft/core/Direction;)Lnet/minecraft/world/item/ItemStack;", at = @At("HEAD"))
    private static void transferMixin(@Nullable Container from, Container to, ItemStack stack, int slot, @Nullable Direction side, CallbackInfoReturnable<ItemStack> info) {
        Level world = SpoiledUtil.getInventoryWorld(to);
        if (world == null) {
            world = SpoiledUtil.getInventoryWorld(from);
        }
        if (world != null) {
            SpoiledUtil.onStackSeen(world, stack);
            if (SpoiledUtil.onStackSeen(world, to.getItem(slot), !(to instanceof DecoratedPotBlockEntity))) {
                to.setChanged();
            }
        }
    }
}
