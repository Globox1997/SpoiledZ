package net.spoiledz.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.DecoratedPotBlock;
import net.minecraft.inventory.Inventory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.spoiledz.init.ComponentInit;
import net.spoiledz.util.SpoiledUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DecoratedPotBlock.class)
public class DecoratedPotBlockMixin {

    @Inject(method = "onStateReplaced", at = @At("HEAD"))
    private void onStateReplacedMixin(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved, CallbackInfo info) {
        if (!state.isOf(newState.getBlock()) && world.getBlockEntity(pos) instanceof Inventory inventory) {
            for (int i = 0; i < inventory.size(); i++) {
                if (inventory.getStack(i).get(ComponentInit.POTTED) != null) {
                    int seasonsPassedBy = SpoiledUtil.getSeasonsPassedBy(world, inventory.getStack(i).get(ComponentInit.POTTED).season(), inventory.getStack(i).get(ComponentInit.POTTED).year());
                    if (seasonsPassedBy > 0) {
                        SpoiledUtil.setSeasonsPassedBy(inventory.getStack(i), seasonsPassedBy);
                    }
                    inventory.getStack(i).remove(ComponentInit.POTTED);
                }
            }
        }
    }
}
