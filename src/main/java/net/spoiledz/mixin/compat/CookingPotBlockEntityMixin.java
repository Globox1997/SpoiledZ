package net.spoiledz.mixin.compat;

import java.util.List;

import com.nhoryzon.mc.farmersdelight.entity.block.CookingPotBlockEntity;
import com.nhoryzon.mc.farmersdelight.entity.block.SyncedBlockEntity;
import com.nhoryzon.mc.farmersdelight.recipe.CookingPotRecipe;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.spoiledz.util.SpoiledUtil;

@Mixin(CookingPotBlockEntity.class)
public abstract class CookingPotBlockEntityMixin extends SyncedBlockEntity {

    @Shadow
    @Mutable
    @Final
    private DefaultedList<ItemStack> inventory;

    public CookingPotBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Inject(method = "processCooking", at = @At(value = "INVOKE", target = "Lcom/nhoryzon/mc/farmersdelight/entity/block/CookingPotBlockEntity;setStack(ILnet/minecraft/item/ItemStack;)V", ordinal = 0, shift = Shift.AFTER))
    private void processCookingMixin(CookingPotRecipe recipe, CallbackInfoReturnable<Boolean> info) {
        SpoiledUtil.setItemStackSpoilage(world, this.inventory.get(6),
                List.of(this.inventory.get(0), this.inventory.get(1), this.inventory.get(2), this.inventory.get(3), this.inventory.get(4), this.inventory.get(5)));
    }

}
