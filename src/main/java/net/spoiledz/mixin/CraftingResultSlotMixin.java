package net.spoiledz.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.lucaargolo.seasons.FabricSeasons;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.slot.CraftingResultSlot;
import net.spoiledz.SpoiledZMain;
import net.spoiledz.init.ConfigInit;
import net.spoiledz.init.TagInit;

@Mixin(CraftingResultSlot.class)
public class CraftingResultSlotMixin {

    @Shadow
    @Final
    @Mutable
    private CraftingInventory input;

    @Inject(method = "onTakeItem", at = @At("HEAD"))
    private void onTakeItemMixin(PlayerEntity player, ItemStack stack, CallbackInfo info) {
        if (!player.world.isClient && !ConfigInit.CONFIG.freshCrafting && ((stack.isFood() || stack.isIn(TagInit.SPOILING_ITEMS)) && !stack.isIn(TagInit.NON_SPOILING_ITEMS))) {
            int year = (int) (player.world.getTimeOfDay() / (FabricSeasons.CONFIG.getSeasonLength() * 4));
            String season = FabricSeasons.getCurrentSeason(player.world).asString();
            for (int i = 0; i < 9; i++) {
                ItemStack inputStack = input.getStack(i);
                if (inputStack != null && !inputStack.isEmpty() && inputStack.hasNbt() && inputStack.getNbt().contains("Season")) {
                    int itemStackYear = inputStack.getNbt().getInt("Year");
                    String itemStackSeason = inputStack.getNbt().getString("Season");
                    if (itemStackYear < year) {
                        year = itemStackYear;
                        season = itemStackSeason;
                    } else {
                        if (SpoiledZMain.SEASONS.indexOf(itemStackSeason) < SpoiledZMain.SEASONS.indexOf(season)) {
                            season = itemStackSeason;
                        }
                    }
                }
            }

            NbtCompound nbtCompound = stack.hasNbt() ? stack.getNbt() : new NbtCompound();
            nbtCompound.putString("Season", season);
            nbtCompound.putInt("Year", year);

            // System.out.println("TAKE: " + stack + " : " + season + " : " + year + " : " + nbtCompound);

            stack.setNbt(nbtCompound);
        }
    }

}
