package net.spoiledz.mixin.compat;

import java.util.List;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.spoiledz.util.SpoiledUtil;
import satisfyu.vinery.block.entity.CookingPotEntity;

@Mixin(CookingPotEntity.class)
public abstract class CookingPotEntityMixin extends BlockEntity {

    @Shadow
    @Mutable
    @Final
    private DefaultedList<ItemStack> inventory;

    public CookingPotEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Inject(method = "craft", at = @At(value = "INVOKE", target = "Lsatisfyu/vinery/block/entity/CookingPotEntity;setStack(ILnet/minecraft/item/ItemStack;)V", ordinal = 0, shift = Shift.AFTER))
    private void craftMixin(Recipe<?> recipe, CallbackInfo info) {
        SpoiledUtil.setItemStackSpoilage(world, this.inventory.get(7),
                List.of(this.inventory.get(0), this.inventory.get(1), this.inventory.get(2), this.inventory.get(3), this.inventory.get(4), this.inventory.get(5)));
    }

}
