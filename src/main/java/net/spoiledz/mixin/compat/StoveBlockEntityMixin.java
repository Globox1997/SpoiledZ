package net.spoiledz.mixin.compat;

import java.util.List;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.spoiledz.util.SpoiledUtil;
import vectorwing.farmersdelight.common.block.entity.StoveBlockEntity;
import vectorwing.farmersdelight.refabricated.inventory.ItemStackHandler;

@Mixin(StoveBlockEntity.class)
public class StoveBlockEntityMixin {

    @Shadow(remap = false)
    @Final
    private int[] cookingTimes;

    @Shadow(remap = false)
    @Final
    private int[] cookingTimesTotal;

    @ModifyArg(method = "cookAndOutputItems", at = @At(value = "INVOKE", target = "Lvectorwing/farmersdelight/common/utility/ItemUtils;spawnItemEntity(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;DDDDDD)V"), index = 1, require = 0)
    private ItemStack cookAndOutputItemsMixin(ItemStack stack) {
        StoveBlockEntity entity = (StoveBlockEntity) (Object) this;
        Level world = entity.getLevel();
        if (world != null) {
            ItemStackHandler inventory = entity.getInventory();
            for (int i = 0; i < inventory.getSlotCount(); i++) {
                ItemStack inputStack = inventory.getStackInSlot(i);
                if (!inputStack.isEmpty() && this.cookingTimes[i] >= this.cookingTimesTotal[i]) {
                    SpoiledUtil.setItemStackSpoilage(world, stack, List.of(inputStack));
                    break;
                }
            }
        }
        return stack;
    }
}
