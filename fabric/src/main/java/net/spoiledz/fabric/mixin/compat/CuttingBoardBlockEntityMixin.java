package net.spoiledz.fabric.mixin.compat;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.spoiledz.util.SpoiledUtil;
import vectorwing.farmersdelight.common.block.entity.CuttingBoardBlockEntity;

@Mixin(CuttingBoardBlockEntity.class)
public class CuttingBoardBlockEntityMixin {

    @ModifyArg(method = "lambda$processStoredItemUsingTool$0", at = @At(value = "INVOKE", target = "Lvectorwing/farmersdelight/common/utility/ItemUtils;spawnItemEntity(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;DDDDDD)V"), index = 1, require = 0)
    private ItemStack processStoredItemUsingToolMixin(ItemStack stack) {
        CuttingBoardBlockEntity entity = (CuttingBoardBlockEntity) (Object) this;
        Level world = entity.getLevel();
        if (world != null) {
            SpoiledUtil.setItemStackSpoilage(world, stack, List.of(entity.getStoredItem()));
        }
        return stack;
    }
}
