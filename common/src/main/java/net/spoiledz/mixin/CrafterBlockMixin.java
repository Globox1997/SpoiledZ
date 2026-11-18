package net.spoiledz.mixin;

import java.util.ArrayList;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.CrafterBlock;
import net.minecraft.world.level.block.entity.CrafterBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.spoiledz.util.SpoiledUtil;

@Mixin(CrafterBlock.class)
public class CrafterBlockMixin {

    @ModifyArg(method = "dispenseFrom", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/CrafterBlock;dispenseItem(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/entity/CrafterBlockEntity;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/item/crafting/RecipeHolder;)V", ordinal = 0), index = 3)
    private ItemStack craftMixin(ServerLevel world, BlockPos pos, CrafterBlockEntity blockEntity, ItemStack stack, BlockState state, RecipeHolder<?> recipe) {
        SpoiledUtil.setItemStackSpoilage(world, stack, new ArrayList<>(blockEntity.getItems()));
        return stack;
    }
}
