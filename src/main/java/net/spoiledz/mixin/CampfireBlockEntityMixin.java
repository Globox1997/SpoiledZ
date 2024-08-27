package net.spoiledz.mixin;

import java.util.List;

import net.minecraft.recipe.input.SingleStackRecipeInput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.CampfireBlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.spoiledz.util.SpoiledUtil;

@Mixin(CampfireBlockEntity.class)
public class CampfireBlockEntityMixin {

    @Inject(method = "litServerTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/ItemScatterer;spawn(Lnet/minecraft/world/World;DDDLnet/minecraft/item/ItemStack;)V"), locals = LocalCapture.CAPTURE_FAILSOFT)
    private static void litServerTickMixin(World world, BlockPos pos, BlockState state, CampfireBlockEntity campfire, CallbackInfo ci, boolean bl, int i, ItemStack itemStack, SingleStackRecipeInput singleStackRecipeInput, ItemStack itemStack2) {
        SpoiledUtil.setItemStackSpoilage(world, itemStack2, List.of(itemStack.copy()));
    }
}
