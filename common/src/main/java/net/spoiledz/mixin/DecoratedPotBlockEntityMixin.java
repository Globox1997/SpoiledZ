package net.spoiledz.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.DecoratedPotBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
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
    private ItemStack item;

    public DecoratedPotBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Inject(method = "setTheItem", at = @At("HEAD"))
    private void setStackMixin(ItemStack stack, CallbackInfo info) {
        if (this.getLevel() != null && !this.getLevel().isClientSide() && this.item.isEmpty() && SpoiledUtil.getPottedComponent(stack) == null && SpoiledUtil.isSpoilable(stack)) {
            SpoiledUtil.onStackSeen(this.getLevel(), stack, false);
            SpoiledUtil.setPottedComponent(stack, SpoiledUtil.getCurrentSpoiledComponent(this.getLevel()));
        }
    }

    @Inject(method = "splitTheItem", at = @At("RETURN"))
    private void decreaseStackMixin(int count, CallbackInfoReturnable<ItemStack> info) {
        if (this.getLevel() != null && !this.getLevel().isClientSide()) {
            SpoiledUtil.removePotted(this.getLevel(), info.getReturnValue());
        }
    }

    @Override
    public void preRemoveSideEffects(BlockPos pos, BlockState state) {
        if (this.getLevel() != null && !this.getLevel().isClientSide()) {
            SpoiledUtil.removePotted(this.getLevel(), this.item);
        }
        super.preRemoveSideEffects(pos, state);
    }

}
