package net.spoiledz.mixin.compat;

import java.util.List;

import com.nhoryzon.mc.farmersdelight.entity.block.StoveBlockEntity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.spoiledz.util.SpoiledUtil;

@Mixin(StoveBlockEntity.class)
public abstract class StoveBlockEntityMixin extends BlockEntity {

    public StoveBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Inject(method = "cookAndDrop", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;spawnEntity(Lnet/minecraft/entity/Entity;)Z"), locals = LocalCapture.CAPTURE_FAILSOFT)
    private void cookAndDropMixin(CallbackInfo info, int i, ItemStack itemstack, Inventory cookInventory, ItemStack result, ItemEntity entity) {
        SpoiledUtil.setItemStackSpoilage(world, entity.getStack(), List.of(itemstack));
    }
}
