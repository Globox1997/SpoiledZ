package net.spoiledz.mixin.compat;

import java.util.List;
import java.util.Optional;

import com.nhoryzon.mc.farmersdelight.item.SkilletItem;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.CampfireCookingRecipe;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;
import net.spoiledz.util.SpoiledUtil;

@Mixin(SkilletItem.class)
public class SkilletItemMixin {

    @Inject(method = "finishUsing", at = @At(value = "INVOKE_ASSIGN", target = "Lcom/nhoryzon/mc/farmersdelight/item/SkilletItem;getCookingRecipe(Lnet/minecraft/item/ItemStack;Lnet/minecraft/world/World;)Ljava/util/Optional;"), cancellable = true, locals = LocalCapture.CAPTURE_FAILSOFT)
    private void finishUsingMixin(ItemStack stack, World world, LivingEntity user, CallbackInfoReturnable<ItemStack> info, PlayerEntity player, NbtCompound tag, ItemStack cookingStack,
            Optional<CampfireCookingRecipe> recipe) {
        if (!world.isClient) {
            recipe.ifPresent((campfireCookingRecipe) -> {
                ItemStack resultStack = campfireCookingRecipe.craft(new SimpleInventory(new ItemStack[0]), world.getRegistryManager());
                SpoiledUtil.setItemStackSpoilage(world, resultStack, List.of(cookingStack));
                if (!player.getInventory().insertStack(resultStack)) {
                    player.dropItem(resultStack, false);
                }

                if (player instanceof ServerPlayerEntity) {
                    Criteria.CONSUME_ITEM.trigger((ServerPlayerEntity) player, stack);
                }

            });
        }
        tag.remove("Cooking");
        tag.remove("CookTimeHandheld");
        info.setReturnValue(stack);
    }

}
