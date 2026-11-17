package net.spoiledz.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.DecoratedPotBlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.spoiledz.util.SpoiledUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DecoratedPotBlockEntity.class)
public abstract class DecoratedPotBlockEntityMixin extends BlockEntity {

    @Shadow
    private ItemStack stack;

    public DecoratedPotBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Inject(method = "setStack", at = @At("HEAD"))
    private void setStackMixin(ItemStack stack, CallbackInfo info) {
        if (this.getWorld() != null && !this.getWorld().isClient() && this.stack.isEmpty() && SpoiledUtil.getPottedComponent(stack) == null && SpoiledUtil.isSpoilable(stack)) {
            SpoiledUtil.onStackSeen(this.getWorld(), stack, false);
            SpoiledUtil.setPottedComponent(stack, SpoiledUtil.getCurrentSpoiledComponent(this.getWorld()));
        }
    }

    @Inject(method = "decreaseStack", at = @At("RETURN"))
    private void decreaseStackMixin(int count, CallbackInfoReturnable<ItemStack> info) {
        if (this.getWorld() != null && !this.getWorld().isClient()) {
            SpoiledUtil.removePotted(this.getWorld(), info.getReturnValue());
        }
    }

}
