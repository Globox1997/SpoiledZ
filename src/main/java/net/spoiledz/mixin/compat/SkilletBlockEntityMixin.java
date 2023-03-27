package net.spoiledz.mixin.compat;

import java.util.List;
import java.util.Optional;

import com.nhoryzon.mc.farmersdelight.entity.block.SkilletBlockEntity;
import com.nhoryzon.mc.farmersdelight.entity.block.SyncedBlockEntity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.spoiledz.util.SpoiledUtil;

@SuppressWarnings("rawtypes")
@Mixin(SkilletBlockEntity.class)
public abstract class SkilletBlockEntityMixin extends SyncedBlockEntity {

    public SkilletBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Inject(method = "cookAndOutputItems", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;spawnEntity(Lnet/minecraft/entity/Entity;)Z"), locals = LocalCapture.CAPTURE_FAILSOFT)
    private void cookAndOutputItemsMixin(ItemStack cookingStack, CallbackInfo info, SimpleInventory wrapper, Optional recipe, ItemStack resultStack, Direction direction, ItemEntity entity) {
        SpoiledUtil.setItemStackSpoilage(this.world, entity.getStack(), List.of(cookingStack));
    }
}
