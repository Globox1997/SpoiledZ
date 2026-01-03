package net.spoiledz.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.DecoratedPotBlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.spoiledz.init.ComponentInit;
import net.spoiledz.util.SpoiledComponent;
import net.spoiledz.util.SpoiledUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DecoratedPotBlockEntity.class)
public abstract class DecoratedPotBlockEntityMixin extends BlockEntity {

    @Shadow
    private ItemStack stack;

    public DecoratedPotBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Inject(method = "setStack", at = @At("HEAD"))
    private void setStackMixin(ItemStack stack, CallbackInfo info) {
        if (!this.getWorld().isClient() && this.stack.isEmpty() && SpoiledUtil.isSpoilable(stack)) {
            stack.set(ComponentInit.POTTED, new SpoiledComponent(SpoiledUtil.getCurrentSeason(this.getWorld()), SpoiledUtil.getCurrentYear(this.getWorld())));
        }
    }

}
