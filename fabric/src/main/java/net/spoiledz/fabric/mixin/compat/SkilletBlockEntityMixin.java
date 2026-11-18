package net.spoiledz.fabric.mixin.compat;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.spoiledz.util.SpoiledUtil;
import vectorwing.farmersdelight.common.block.entity.SkilletBlockEntity;

@Mixin(SkilletBlockEntity.class)
public class SkilletBlockEntityMixin {

    @ModifyArg(method = "cookAndOutputItems", at = @At(value = "INVOKE", target = "Lvectorwing/farmersdelight/common/utility/ItemUtils;spawnItemEntity(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;DDDDDD)V"), index = 1, require = 0)
    private ItemStack cookAndOutputItemsMixin(ItemStack stack) {
        SkilletBlockEntity entity = (SkilletBlockEntity) (Object) this;
        Level world = entity.getLevel();
        if (world != null) {
            SpoiledUtil.setItemStackSpoilage(world, stack, List.of(entity.getStoredStack()));
        }
        return stack;
    }
}
