package net.spoiledz.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jetbrains.annotations.Nullable;

import io.github.lucaargolo.seasons.FabricSeasons;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.PersistentState;
import net.minecraft.world.World;
import net.spoiledz.SpoiledZMain;
import net.spoiledz.access.ServerWorldAccess;
import net.spoiledz.init.ConfigInit;
import net.spoiledz.init.TagInit;

public class SpoiledUtil {

    public static final BooleanProperty SPOILED = BooleanProperty.of("spoiled");

    // 0 = 0%, 1=25%, 2=50%, 3=75%, 4=100%
    public static int getSpoilingTime(World world, ItemStack stack) {
        if (world != null && stack != null && stack.hasNbt() && stack.getNbt().contains("Season")) {
            String itemSeason = stack.getNbt().getString("Season");
            int itemYear = stack.getNbt().getInt("Year");

            int currentYear = (int) (world.getTimeOfDay() / (FabricSeasons.getCurrentSeason(world).getSeasonLength() * 4));
            String currentSeason = FabricSeasons.getCurrentSeason(world).asString();

            int yearDiff = currentYear - itemYear;

            // Determine the number of seasons within the years that have passed
            int seasonsPassed = 0;

            int oldSeasonIndex = SpoiledZMain.SEASONS.indexOf(itemSeason);
            int currentSeasonIndex = SpoiledZMain.SEASONS.indexOf(currentSeason);

            if (oldSeasonIndex != -1 && currentSeasonIndex != -1) {
                if (yearDiff > 0) {
                    seasonsPassed += yearDiff * SpoiledZMain.SEASONS.size();
                    seasonsPassed += (currentSeasonIndex - oldSeasonIndex + SpoiledZMain.SEASONS.size()) % SpoiledZMain.SEASONS.size();
                } else if (yearDiff == 0) {
                    seasonsPassed = (currentSeasonIndex - oldSeasonIndex + SpoiledZMain.SEASONS.size()) % SpoiledZMain.SEASONS.size();
                }
            }
            int returnSpoilage = (int) (seasonsPassed / ((float) ConfigInit.CONFIG.seasonSpoilage / 4));
            return returnSpoilage > 4 ? 4 : returnSpoilage;
        } else {
            return -1;
        }
    }

    public static void setItemStackSpoilage(World world, ItemStack stack, @Nullable List<ItemStack> recipeStacks) {
        if (!world.isClient && ((stack.isFood() || stack.isIn(TagInit.SPOILING_ITEMS)) && !stack.isIn(TagInit.NON_SPOILING_ITEMS))) {
            if (recipeStacks != null && !recipeStacks.isEmpty() && !ConfigInit.CONFIG.freshCrafting) {
                int year = (int) (world.getTimeOfDay() / (FabricSeasons.getCurrentSeason(world).getSeasonLength() * 4));
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
                nbtCompound.putInt("Year", (int) (world.getTimeOfDay() / (FabricSeasons.getCurrentSeason(world).getSeasonLength() * 4)));
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

    public static class FoodBlockMap extends PersistentState {

        private HashMap<BlockPos, ItemStack> FOOD_BLOCK_MAP = new HashMap<BlockPos, ItemStack>();

        public FoodBlockMap() {
        }

        @Override
        public NbtCompound writeNbt(NbtCompound nbt) {
            nbt.putInt("FoodBlockMapSize", this.FOOD_BLOCK_MAP.size());
            Iterator<Map.Entry<BlockPos, ItemStack>> iterator = this.FOOD_BLOCK_MAP.entrySet().iterator();
            int count = 0;
            while (iterator.hasNext()) {
                Map.Entry<BlockPos, ItemStack> entry = iterator.next();
                if (entry.getValue().getNbt() != null) {
                    nbt.putInt("FoodBlockX" + count, entry.getKey().getX());
                    nbt.putInt("FoodBlockY" + count, entry.getKey().getY());
                    nbt.putInt("FoodBlockZ" + count, entry.getKey().getZ());
                    nbt.putInt("FoodBlockYear" + count, entry.getValue().getNbt().getInt("Year"));
                    nbt.putString("FoodBlockSeason" + count, entry.getValue().getNbt().getString("Season"));
                    count++;
                }
            }
            return nbt;
        }

        public static FoodBlockMap fromNbt(ServerWorld world, NbtCompound nbt) {
            FoodBlockMap foodBlockMap = new FoodBlockMap();
            for (int i = 0; i < nbt.getInt("FoodBlockMapSize"); i++) {
                ItemStack itemStack = new ItemStack(Items.APPLE);
                NbtCompound stackNbt = new NbtCompound();
                stackNbt.putString("Season", nbt.getString("FoodBlockSeason" + i));
                stackNbt.putInt("Year", nbt.getInt("FoodBlockYear" + i));
                itemStack.setNbt(stackNbt);
                foodBlockMap.FOOD_BLOCK_MAP.put(new BlockPos(nbt.getInt("FoodBlockX" + i), nbt.getInt("FoodBlockY" + i), nbt.getInt("FoodBlockZ" + i)), itemStack);
            }
            return foodBlockMap;
        }

        public void addFoodBlock(BlockPos pos, ItemStack stack) {
            this.FOOD_BLOCK_MAP.put(pos, stack);
            this.markDirty();
        }

        public void removeFoodBlock(BlockPos pos) {
            if (this.FOOD_BLOCK_MAP.containsKey(pos)) {
                this.FOOD_BLOCK_MAP.remove(pos);
                this.markDirty();
            }
        }

        public HashMap<BlockPos, ItemStack> getFoodBlockMap() {
            return this.FOOD_BLOCK_MAP;
        }

        public static void onRemovedFoodBlock(World world, BlockPos pos, @Nullable BlockState newState) {
            if (((ServerWorldAccess) world).getFoodBlockMap().getFoodBlockMap().containsKey(pos)) {
                if (newState != null && !newState.isAir() && newState.contains(SPOILED)
                        && SpoiledUtil.getSpoilingTime(world, ((ServerWorldAccess) world).getFoodBlockMap().getFoodBlockMap().get(pos)) < 4) {
                    return;
                }
                ((ServerWorldAccess) world).getFoodBlockMap().removeFoodBlock(pos);
            }
        }

        public static void onPlacedFoodBlock(World world, BlockPos pos, BlockState state, ItemStack itemStack) {
            if (!world.isClient()) {
                int spoiledTime = SpoiledUtil.getSpoilingTime(world, itemStack);
                if (spoiledTime >= 4) {
                    world.setBlockState(pos, state.with(SpoiledUtil.SPOILED, true));
                } else {
                    ((ServerWorldAccess) world).getFoodBlockMap().addFoodBlock(pos, itemStack);
                    if (state.get(SpoiledUtil.SPOILED)) {
                        world.setBlockState(pos, state.with(SpoiledUtil.SPOILED, false));
                    }
                }
            }
        }

        public static void scheduledTickFoodBlock(World world, BlockPos pos, BlockState state) {
            if (((ServerWorldAccess) world).getFoodBlockMap().getFoodBlockMap().containsKey(pos)) {
                int spoiledTime = SpoiledUtil.getSpoilingTime(world, ((ServerWorldAccess) world).getFoodBlockMap().getFoodBlockMap().get(pos));
                if (spoiledTime >= 4) {
                    world.setBlockState(pos, state.with(SpoiledUtil.SPOILED, true));
                    ((ServerWorldAccess) world).getFoodBlockMap().removeFoodBlock(pos);
                }
            }
        }

    }

}
