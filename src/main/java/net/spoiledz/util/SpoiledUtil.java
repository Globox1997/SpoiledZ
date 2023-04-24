package net.spoiledz.util;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import io.github.lucaargolo.seasons.FabricSeasons;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;
import net.spoiledz.SpoiledZMain;
import net.spoiledz.init.ConfigInit;
import net.spoiledz.init.TagInit;

public class SpoiledUtil {

    // 0 = 0%, 1=25%, 2=50%, 3=75%, 4=100%
    public static int getSpoilingTime(World world, ItemStack stack) {
        if (world != null && stack != null && stack.hasNbt() && stack.getNbt().contains("Season")) {
            String itemSeason = stack.getNbt().getString("Season");
            int itemYear = stack.getNbt().getInt("Year");

            int currentYear = (int) (world.getTimeOfDay() / (FabricSeasons.CONFIG.getSeasonLength() * 4));

            int yearDifference = currentYear - itemYear;
            if (yearDifference > 2)
                return 4;

            String currentSeason = FabricSeasons.getCurrentSeason(world).asString();
            int seasonDifference = SpoiledZMain.SEASONS.indexOf(currentSeason) - SpoiledZMain.SEASONS.indexOf(itemSeason);

            if (yearDifference == 0)
                return seasonDifference / 2;

            if (seasonDifference >= 0)
                seasonDifference += yearDifference * 4;
            else
                seasonDifference = yearDifference * 4 + seasonDifference;

            if (seasonDifference >= 10)
                return 4;
            if (seasonDifference < 0)
                return 0;

            // System.out.print(stack + " : " + seasonDifference);

            return seasonDifference / 2;
        } else
            return -1;
    }

    public static void setItemStackSpoilage(World world, ItemStack stack, @Nullable List<ItemStack> recipeStacks) {
        if (!world.isClient && ((stack.isFood() || stack.isIn(TagInit.SPOILING_ITEMS)) && !stack.isIn(TagInit.NON_SPOILING_ITEMS))) {
            if (recipeStacks != null && !recipeStacks.isEmpty() && !ConfigInit.CONFIG.freshCrafting) {
                int year = (int) (world.getTimeOfDay() / (FabricSeasons.CONFIG.getSeasonLength() * 4));
                String season = FabricSeasons.getCurrentSeason(world).asString();

                for (int i = 0; i < recipeStacks.size(); i++) {
                    ItemStack inputStack = recipeStacks.get(i);

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

                stack.setNbt(nbtCompound);
            } else if (!hasSpoilage(stack) || ConfigInit.CONFIG.freshCrafting) {
                NbtCompound nbtCompound = stack.hasNbt() ? stack.getNbt() : new NbtCompound();
                nbtCompound.putString("Season", FabricSeasons.getCurrentSeason(world).asString());
                nbtCompound.putInt("Year", (int) (world.getTimeOfDay() / (FabricSeasons.CONFIG.getSeasonLength() * 4)));
                stack.setNbt(nbtCompound);
            }
        }
    }

    public static boolean hasSpoilage(ItemStack stack) {
        if (stack.hasNbt() && stack.getNbt().contains("Season") && stack.getNbt().contains("Year")) {
            return true;
        }
        return false;
    }

    public static boolean isSpoilable(ItemStack stack) {
        if ((stack.isFood() || stack.isIn(TagInit.SPOILING_ITEMS)) && !stack.isIn(TagInit.NON_SPOILING_ITEMS)) {
            return true;
        }
        return false;
    }

    public static boolean isSpoilageEqual(ItemStack itemStack, ItemStack itemStack2) {
        if (itemStack != null && itemStack.hasNbt() && itemStack.getNbt().contains("Season") && itemStack2 != null && itemStack2.hasNbt() && itemStack2.getNbt().contains("Season")) {
            String itemSeason = itemStack.getNbt().getString("Season");
            int itemYear = itemStack.getNbt().getInt("Year");

            String item2Season = itemStack2.getNbt().getString("Season");
            int item2Year = itemStack2.getNbt().getInt("Year");

            int seasonDifference = Math.abs(SpoiledZMain.SEASONS.indexOf(item2Season) - SpoiledZMain.SEASONS.indexOf(itemSeason));
            int yearDifference = Math.abs(itemYear - item2Year);

            if (yearDifference == 0 && seasonDifference < 2) {
                return true;
            }

            if (yearDifference == 1) {
                if (itemYear < item2Year)
                    seasonDifference = SpoiledZMain.SEASONS.indexOf(item2Season) - SpoiledZMain.SEASONS.indexOf(itemSeason);
                else
                    seasonDifference = SpoiledZMain.SEASONS.indexOf(itemSeason) - SpoiledZMain.SEASONS.indexOf(item2Season);
                if (seasonDifference < 0)
                    seasonDifference = yearDifference * 4 + seasonDifference;
                if (seasonDifference < 2)
                    return true;

            }
            if (yearDifference > 2) {
                return true;
            }

            return false;
        } else
            return false;
    }

    // // 0 = 0%, 1=25%, 2=50%, 3=75%, 4=100%
    // public static int getSpoilingTime(World world, String itemSeason, int itemYear) {
    // int currentYear = (int) (world.getTimeOfDay() / (FabricSeasons.CONFIG.getSeasonLength() * 4));
    // String currentSeason = FabricSeasons.getCurrentSeason(world).asString();
    // if (currentYear > itemYear + 2)
    // return 4;

    // int seasonDifference = 0;
    // if (currentYear != itemYear) {

    // // index
    // // 0 ,1 ,2 ,3
    // // spring,summer,fall,winter

    // // currentSeason
    // // spring(0), winter(3)

    // // itemSeason
    // // fall(2), summer(1)
    // int curentSeasonInt = SpoiledZMain.SEASONS.indexOf(currentSeason);
    // int itemSeasonInt = SpoiledZMain.SEASONS.indexOf(itemSeason);

    // if (currentYear == itemYear + 2) {
    // if (SpoiledZMain.SEASONS.indexOf(currentSeason) < SpoiledZMain.SEASONS.indexOf(itemSeason))
    // return 2 + (SpoiledZMain.SEASONS.indexOf(itemSeason) - SpoiledZMain.SEASONS.indexOf(currentSeason)) / 2;
    // else
    // return 4;
    // }

    // if (itemYear + 1 == currentYear && SpoiledZMain.SEASONS.indexOf(currentSeason) >= SpoiledZMain.SEASONS.indexOf(itemSeason)) {
    // return 4;
    // }
    // if (itemYear + 1 == currentYear)
    // seasonDifference += 4;

    // seasonDifference += 4 - Math.abs(SpoiledZMain.SEASONS.indexOf(currentSeason) - SpoiledZMain.SEASONS.indexOf(itemSeason));

    // // if (seasonDifference > 8)
    // // return 4;
    // } else
    // seasonDifference = Math.abs(SpoiledZMain.SEASONS.indexOf(currentSeason) - SpoiledZMain.SEASONS.indexOf(itemSeason));

    // // if (seasonDifference == 0 && year != currentYear) {
    // // return 4;
    // // }

    // return seasonDifference;
    // }

}
