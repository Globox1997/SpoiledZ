package net.spoiledz.mixin.compat;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.spoiledz.util.SpoiledUtil;
import vectorwing.farmersdelight.common.block.entity.CuttingBoardBlockEntity;

@Mixin(CuttingBoardBlockEntity.class)
public class CuttingBoardBlockEntityMixin {

    @ModifyArg(method = "lambda$processStoredItemUsingTool$1", at = @At(value = "INVOKE", target = "Lvectorwing/farmersdelight/common/utility/ItemUtils;spawnItemEntity(Lnet/minecraft/world/World;Lnet/minecraft/item/ItemStack;DDDDDD)V"), index = 1, require = 0)
    private ItemStack processStoredItemUsingToolMixin(ItemStack stack) {
        CuttingBoardBlockEntity entity = (CuttingBoardBlockEntity) (Object) this;
        World world = entity.getWorld();
        if (world != null) {
            SpoiledUtil.setItemStackSpoilage(world, stack, List.of(entity.getStoredItem()));
        }
        return stack;
    }
}
