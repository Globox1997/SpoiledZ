package net.spoiledz.mixin;

import java.util.ArrayList;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import net.minecraft.block.BlockState;
import net.minecraft.block.CrafterBlock;
import net.minecraft.block.entity.CrafterBlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.spoiledz.util.SpoiledUtil;

@Mixin(CrafterBlock.class)
public class CrafterBlockMixin {

    @ModifyArg(method = "craft", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/CrafterBlock;transferOrSpawnStack(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/entity/CrafterBlockEntity;Lnet/minecraft/item/ItemStack;Lnet/minecraft/block/BlockState;Lnet/minecraft/recipe/RecipeEntry;)V", ordinal = 0), index = 3)
    private ItemStack craftMixin(ServerWorld world, BlockPos pos, CrafterBlockEntity blockEntity, ItemStack stack, BlockState state, RecipeEntry<CraftingRecipe> recipe) {
        SpoiledUtil.setItemStackSpoilage(world, stack, new ArrayList<>(blockEntity.getHeldStacks()));
        return stack;
    }
}
