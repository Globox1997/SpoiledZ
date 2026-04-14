package net.spoiledz.util;

import java.util.*;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.registry.RegistryWrapper;
import net.spoiledz.init.ComponentInit;
import net.spoiledz.init.EventInit;
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
import sereneseasons.api.season.SeasonHelper;

public class SpoiledUtil {

    public static final BooleanProperty SPOILED = BooleanProperty.of("spoiled");

    // 0 = 0%, 1=25%, 2=50%, 3=75%, 4=100%
    public static int getSpoilingTime(World world, ItemStack stack) {
        if (world != null && stack != null && stack.get(ComponentInit.SPOILED) != null) {

            String itemSeason = stack.get(ComponentInit.SPOILED).season();
            int itemYear = stack.get(ComponentInit.SPOILED).year();

            int returnSpoilage = (int) (SpoiledUtil.getSeasonsPassedBy(world, itemSeason, itemYear) / ((float) ConfigInit.CONFIG.seasonSpoilage / 4));

            return Math.min(returnSpoilage, 4);
        } else {
            return -1;
        }
    }

    public static void setItemStackSpoilage(World world, ItemStack stack, @Nullable List<ItemStack> recipeStacks) {
        if (!world.isClient() && isSpoilable(stack)) {
            if (recipeStacks != null && !recipeStacks.isEmpty() && !ConfigInit.CONFIG.freshCrafting) {
                int year = SpoiledUtil.getCurrentYear(world);
                String season = SpoiledUtil.getCurrentSeason(world);

                for (ItemStack inputStack : recipeStacks) {
                    if (inputStack != null && !inputStack.isEmpty() && inputStack.get(ComponentInit.SPOILED) != null) {
                        int itemStackYear = inputStack.get(ComponentInit.SPOILED).year();
                        String itemStackSeason = inputStack.get(ComponentInit.SPOILED).season();

                        if (itemStackYear < year) {
                            year = itemStackYear;
                            season = itemStackSeason;
                        } else {
                            if (EventInit.isSereneSeasonsLoaded) {
                                if (SpoiledZMain.SERENE_SEASONS.indexOf(itemStackSeason) < SpoiledZMain.SERENE_SEASONS.indexOf(season)) {
                                    season = itemStackSeason;
                                }
                            } else if (SpoiledZMain.FABRIC_SEASONS.indexOf(itemStackSeason) < SpoiledZMain.FABRIC_SEASONS.indexOf(season)) {
                                season = itemStackSeason;
                            }
                        }
                    }
                }

                stack.set(ComponentInit.SPOILED, new SpoiledComponent(season, year));
            } else if (!hasSpoilage(stack) || ConfigInit.CONFIG.freshCrafting) {
                int year = SpoiledUtil.getCurrentYear(world);
                String season = SpoiledUtil.getCurrentSeason(world);

                stack.set(ComponentInit.SPOILED, new SpoiledComponent(season, year));
            }
        }
    }

    public static void setSeasonsPassedBy(ItemStack stack, int seasonsPassedBy) {
        if (isSpoilable(stack)) {

            String season = stack.get(ComponentInit.SPOILED).season();
            int year = stack.get(ComponentInit.SPOILED).year();

            int currentSeasonIndex;
            if (EventInit.isSereneSeasonsLoaded) {
                currentSeasonIndex = SpoiledZMain.SERENE_SEASONS.indexOf(season);
            } else {
                currentSeasonIndex = SpoiledZMain.FABRIC_SEASONS.indexOf(season);
            }
            if (year > 0) {
                year += seasonsPassedBy / 4;
            }
            int newSeasonIndex = (currentSeasonIndex + seasonsPassedBy % 4) % 4;
            if (EventInit.isSereneSeasonsLoaded) {
                season = SpoiledZMain.SERENE_SEASONS.get(newSeasonIndex);
            } else {
                season = SpoiledZMain.FABRIC_SEASONS.get(newSeasonIndex);
            }
            stack.set(ComponentInit.SPOILED, new SpoiledComponent(season, year));
        }
    }

    public static int getSeasonsPassedBy(World world, String storedSeason, int storedYear) {
        int currentYear = SpoiledUtil.getCurrentYear(world);

        int yearDiff = currentYear - storedYear;

        int seasonsPassed = 0;

        int oldSeasonIndex;
        int currentSeasonIndex;
        if (EventInit.isSereneSeasonsLoaded) {
            oldSeasonIndex = SpoiledZMain.SERENE_SEASONS.indexOf(storedSeason);
            currentSeasonIndex = SpoiledZMain.SERENE_SEASONS.indexOf(SpoiledUtil.getCurrentSeason(world));
        } else {
            oldSeasonIndex = SpoiledZMain.FABRIC_SEASONS.indexOf(storedSeason);
            currentSeasonIndex = SpoiledZMain.FABRIC_SEASONS.indexOf(SpoiledUtil.getCurrentSeason(world));
        }

        if (oldSeasonIndex != -1 && currentSeasonIndex != -1) {
            if (yearDiff > 0) {
                seasonsPassed += yearDiff * 4;
                seasonsPassed += (currentSeasonIndex - oldSeasonIndex + 4) % 4;
            } else if (yearDiff == 0) {
                seasonsPassed = (currentSeasonIndex - oldSeasonIndex + 4) % 4;
            }
        }
        return seasonsPassed;
    }

    public static String getCurrentSeason(World world) {
        String season;
        if (EventInit.isSereneSeasonsLoaded) {
            season = SeasonHelper.getSeasonState(world).getSeason().name();
        } else {
            season = FabricSeasons.getCurrentSeason(world).asString();
        }
        return season;
    }

    public static int getCurrentYear(World world) {
        int year;
        if (EventInit.isSereneSeasonsLoaded) {
            year = (int) (world.getTimeOfDay() / (SeasonHelper.getSeasonState(world).getSeasonDuration() * 4));
        } else {
            year = (int) (world.getTimeOfDay() / (FabricSeasons.getCurrentSeason(world).getSeasonLength() * 4));
        }
        return year;
    }

    public static boolean hasSpoilage(ItemStack stack) {
        return stack.get(ComponentInit.SPOILED) != null;
    }

    public static boolean isSpoilable(ItemStack stack) {
        return (stack.get(DataComponentTypes.FOOD) != null || stack.isIn(TagInit.SPOILING_ITEMS)) && !stack.isIn(TagInit.NON_SPOILING_ITEMS);
    }

    public static boolean isSpoilageEqual(ItemStack itemStack, ItemStack itemStack2) {
        if (itemStack != null && itemStack2 != null && isSpoilable(itemStack) && isSpoilable(itemStack2)) {
            if (itemStack.get(ComponentInit.SPOILED) != null && itemStack2.get(ComponentInit.SPOILED) != null) {
                String itemSeason = itemStack.get(ComponentInit.SPOILED).season();
                int itemYear = itemStack.get(ComponentInit.SPOILED).year();

                String item2Season = itemStack2.get(ComponentInit.SPOILED).season();
                int item2Year = itemStack2.get(ComponentInit.SPOILED).year();

                int seasonDifference;

                if (EventInit.isSereneSeasonsLoaded) {
                    seasonDifference = Math.abs(SpoiledZMain.SERENE_SEASONS.indexOf(item2Season) - SpoiledZMain.SERENE_SEASONS.indexOf(itemSeason));
                } else {
                    seasonDifference = Math.abs(SpoiledZMain.FABRIC_SEASONS.indexOf(item2Season) - SpoiledZMain.FABRIC_SEASONS.indexOf(itemSeason));
                }
                int yearDifference = Math.abs(itemYear - item2Year);

                if (yearDifference == 0 && seasonDifference < 2) {
                    return true;
                }

                if (yearDifference == 1) {
                    if (itemYear < item2Year) {
                        if (EventInit.isSereneSeasonsLoaded) {
                            seasonDifference = SpoiledZMain.SERENE_SEASONS.indexOf(item2Season) - SpoiledZMain.SERENE_SEASONS.indexOf(itemSeason);
                        } else {
                            seasonDifference = SpoiledZMain.FABRIC_SEASONS.indexOf(item2Season) - SpoiledZMain.FABRIC_SEASONS.indexOf(itemSeason);
                        }
                    } else {
                        if (EventInit.isSereneSeasonsLoaded) {
                            seasonDifference = SpoiledZMain.SERENE_SEASONS.indexOf(itemSeason) - SpoiledZMain.SERENE_SEASONS.indexOf(item2Season);
                        } else {
                            seasonDifference = SpoiledZMain.FABRIC_SEASONS.indexOf(itemSeason) - SpoiledZMain.FABRIC_SEASONS.indexOf(item2Season);
                        }
                    }
                    if (seasonDifference < 0) {
                        seasonDifference = yearDifference * 4 + seasonDifference;
                    }
                    if (seasonDifference < 2) {
                        return true;
                    }

                }
                if (yearDifference > 2) {
                    return true;
                }

                return false;
            } else {
                if (itemStack.get(ComponentInit.SPOILED) != null && itemStack2.get(ComponentInit.SPOILED) == null) {
                    return true;
                }
                // Maybe outcomment the following line cause admin item (non spoiled food) could get replicated with it
                // If outcommented, delete SlotMixin
                else if (itemStack.get(ComponentInit.SPOILED) == null && itemStack2.get(ComponentInit.SPOILED) != null) {
                    return true;
                }
            }
        }
        return false;
    }


    public static void onSeasonChange(ServerWorld serverWorld) {
        if ((int) serverWorld.getTimeOfDay() % 20 == 0 && serverWorld instanceof ServerWorldAccess serverWorldAccess) {
            if (serverWorldAccess.getCurrentSeason() == null || ((EventInit.isSereneSeasonsLoaded && !serverWorldAccess.getCurrentSeason().equals(SeasonHelper.getSeasonState(serverWorld).getSeason().name())) || (EventInit.isFabricSeasonsLoaded && !serverWorldAccess.getCurrentSeason().equals(FabricSeasons.getCurrentSeason(serverWorld).name())))) {
                if (serverWorldAccess.getCurrentSeason() != null) {
                    for (Map.Entry<BlockPos, ItemStack> entry : new ArrayList<>(serverWorldAccess.getFoodBlockState().getFoodBlockMap().entrySet())) {
                        if (serverWorld.getBlockState(entry.getKey()).contains(SpoiledUtil.SPOILED)) {
                            int spoilTime = SpoiledUtil.getSpoilingTime(serverWorld, entry.getValue());
                            if (spoilTime != -1 && spoilTime >= 4) {
                                serverWorld.getBlockState(entry.getKey()).scheduledTick(serverWorld, entry.getKey(), serverWorld.getRandom());
                                serverWorldAccess.getFoodBlockState().removeFoodBlock(entry.getKey());
                            }
                        } else {
                            serverWorldAccess.getFoodBlockState().removeFoodBlock(entry.getKey());
                        }
                    }
                }
                if (EventInit.isSereneSeasonsLoaded) {
                    serverWorldAccess.setCurrentSeason(SeasonHelper.getSeasonState(serverWorld).getSeason().name());
                } else {
                    serverWorldAccess.setCurrentSeason(FabricSeasons.getCurrentSeason(serverWorld).name());
                }
            }
        }
    }

    public static class FoodBlockState extends PersistentState {

        private final HashMap<BlockPos, ItemStack> FOOD_BLOCK_MAP = new HashMap<>();

        private final ServerWorld world;

        public FoodBlockState(ServerWorld world) {
            this.world = world;
        }

        public static PersistentState.Type<FoodBlockState> getPersistentStateType(ServerWorld world) {
            return new PersistentState.Type<>(() -> new FoodBlockState(world), (nbt, registryLookup) -> fromNbt(world, (NbtCompound) nbt), null);
        }

        public ServerWorld getWorld() {
            return this.world;
        }

        @Override
        public NbtCompound writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
            nbt.putInt("FoodBlockMapSize", this.FOOD_BLOCK_MAP.size());
            Iterator<Map.Entry<BlockPos, ItemStack>> iterator = this.FOOD_BLOCK_MAP.entrySet().iterator();
            int count = 0;
            while (iterator.hasNext()) {
                Map.Entry<BlockPos, ItemStack> entry = iterator.next();
                if (entry.getValue().get(ComponentInit.SPOILED) != null) {
                    nbt.putInt("FoodBlockX" + count, entry.getKey().getX());
                    nbt.putInt("FoodBlockY" + count, entry.getKey().getY());
                    nbt.putInt("FoodBlockZ" + count, entry.getKey().getZ());
                    nbt.putInt("FoodBlockYear" + count, entry.getValue().get(ComponentInit.SPOILED).year());
                    nbt.putString("FoodBlockSeason" + count, entry.getValue().get(ComponentInit.SPOILED).season());
                    count++;
                }
            }
            return nbt;
        }

        public static FoodBlockState fromNbt(ServerWorld world, NbtCompound nbt) {
            FoodBlockState foodBlockState = new FoodBlockState(world);
            for (int i = 0; i < nbt.getInt("FoodBlockMapSize"); i++) {
                ItemStack itemStack = new ItemStack(Items.APPLE);
                itemStack.set(ComponentInit.SPOILED, new SpoiledComponent(nbt.getString("FoodBlockSeason" + i), nbt.getInt("FoodBlockYear" + i)));

                foodBlockState.FOOD_BLOCK_MAP.put(new BlockPos(nbt.getInt("FoodBlockX" + i), nbt.getInt("FoodBlockY" + i), nbt.getInt("FoodBlockZ" + i)), itemStack);
            }
            return foodBlockState;
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
            if (((ServerWorldAccess) world).getFoodBlockState().getFoodBlockMap().containsKey(pos)) {
                if (newState != null && !newState.isAir() && newState.contains(SPOILED)
                        && SpoiledUtil.getSpoilingTime(world, ((ServerWorldAccess) world).getFoodBlockState().getFoodBlockMap().get(pos)) < 4) {
                    return;
                }
                ((ServerWorldAccess) world).getFoodBlockState().removeFoodBlock(pos);
            }
        }

        public static void onPlacedFoodBlock(World world, BlockPos pos, BlockState state, ItemStack itemStack) {
            if (!world.isClient()) {
                int spoiledTime = SpoiledUtil.getSpoilingTime(world, itemStack);
                if (spoiledTime >= 4) {
                    world.setBlockState(pos, state.with(SpoiledUtil.SPOILED, true));
                } else {
                    ((ServerWorldAccess) world).getFoodBlockState().addFoodBlock(pos, itemStack);
                    if (state.get(SpoiledUtil.SPOILED)) {
                        world.setBlockState(pos, state.with(SpoiledUtil.SPOILED, false));
                    }
                }
            }
        }

        public static void scheduledTickFoodBlock(World world, BlockPos pos, BlockState state) {
            if (((ServerWorldAccess) world).getFoodBlockState().getFoodBlockMap().containsKey(pos)) {
                int spoiledTime = SpoiledUtil.getSpoilingTime(world, ((ServerWorldAccess) world).getFoodBlockState().getFoodBlockMap().get(pos));
                if (spoiledTime >= 4) {
                    world.setBlockState(pos, state.with(SpoiledUtil.SPOILED, true));
                    ((ServerWorldAccess) world).getFoodBlockState().removeFoodBlock(pos);
                }
            }
        }

    }

}
