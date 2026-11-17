package net.spoiledz.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DecoratedPotBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.spoiledz.util.SpoiledUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DecoratedPotBlock.class)
public class DecoratedPotBlockMixin {

    @Inject(method = "onRemove", at = @At("HEAD"))
    private void onStateReplacedMixin(BlockState state, Level world, BlockPos pos, BlockState newState, boolean moved, CallbackInfo info) {
        if (!state.is(newState.getBlock()) && world.getBlockEntity(pos) instanceof Container inventory) {
            for (int i = 0; i < inventory.getContainerSize(); i++) {
                SpoiledUtil.removePotted(world, inventory.getItem(i));
            }
        }
    }
}
