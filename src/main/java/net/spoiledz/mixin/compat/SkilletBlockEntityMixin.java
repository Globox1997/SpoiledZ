package net.spoiledz.mixin.compat;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.spoiledz.util.SpoiledUtil;
import vectorwing.farmersdelight.common.block.entity.SkilletBlockEntity;

@Mixin(SkilletBlockEntity.class)
public class SkilletBlockEntityMixin {

    @ModifyArg(method = "cookAndOutputItems", at = @At(value = "INVOKE", target = "Lvectorwing/farmersdelight/common/utility/ItemUtils;spawnItemEntity(Lnet/minecraft/world/World;Lnet/minecraft/item/ItemStack;DDDDDD)V"), index = 1, require = 0)
    private ItemStack cookAndOutputItemsMixin(ItemStack stack) {
        SkilletBlockEntity entity = (SkilletBlockEntity) (Object) this;
        World world = entity.getWorld();
        if (world != null) {
            SpoiledUtil.setItemStackSpoilage(world, stack, List.of(entity.getStoredStack()));
        }
        return stack;
    }
}
