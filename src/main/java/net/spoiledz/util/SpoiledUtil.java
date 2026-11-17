package net.spoiledz.util;

import java.lang.ref.WeakReference;
import java.util.*;

import net.minecraft.block.BlockState;
import net.minecraft.block.CandleCakeBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.component.ComponentChanges;
import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.World;
import net.spoiledz.SpoiledZMain;
import net.spoiledz.access.ServerWorldAccess;
import net.spoiledz.init.ComponentInit;
import net.spoiledz.init.ConfigInit;
import net.spoiledz.init.EventInit;
import net.spoiledz.init.TagInit;
import org.jetbrains.annotations.Nullable;

import io.github.lucaargolo.seasons.FabricSeasons;
import sereneseasons.api.season.SeasonHelper;

public class SpoiledUtil {

    public static final BooleanProperty SPOILED = BooleanProperty.of("spoiled");

    private static final int INVALID_SEASON_COUNT = Integer.MIN_VALUE;

    private static volatile SeasonCountCache clientSeasonCountCache = null;
    private static volatile SeasonCountCache serverSeasonCountCache = null;

    private record SeasonCountCache(WeakReference<World> world, long time, int seasonCount) {
    }

    // 0 = 0%, 1=25%, 2=50%, 3=75%, 4=100%
    public static int getSpoilingTime(World world, ItemStack stack) {
        if (stack == null) {
            return -1;
        }
        return getSpoilingTime(world, getSpoiledComponent(stack));
    }

    public static int getSpoilingTime(World world, @Nullable SpoiledComponent spoiledComponent) {
        if (world != null && spoiledComponent != null) {
            int seasonSpoilage = Math.max(1, ConfigInit.CONFIG.seasonSpoilage);
            int returnSpoilage = (int) (getSeasonsPassedBy(world, spoiledComponent) / ((float) seasonSpoilage / 4));

            return Math.min(returnSpoilage, 4);
        } else {
            return -1;
        }
    }

    @Nullable
    public static SpoiledComponent getSpoiledComponent(ItemStack stack) {
        SpoiledComponent spoiledComponent = stack.get(ComponentInit.SPOILED);
        return spoiledComponent != null ? spoiledComponent : stack.get(ComponentInit.LEGACY_SPOILED);
    }

    public static void setSpoiledComponent(ItemStack stack, SpoiledComponent spoiledComponent) {
        stack.set(ComponentInit.SPOILED, spoiledComponent);
        if (stack.contains(ComponentInit.LEGACY_SPOILED)) {
            stack.remove(ComponentInit.LEGACY_SPOILED);
        }
    }

    @Nullable
    public static SpoiledComponent getPottedComponent(ItemStack stack) {
        SpoiledComponent pottedComponent = stack.get(ComponentInit.POTTED);
        return pottedComponent != null ? pottedComponent : stack.get(ComponentInit.LEGACY_POTTED);
    }

    public static void setPottedComponent(ItemStack stack, SpoiledComponent pottedComponent) {
        stack.set(ComponentInit.POTTED, pottedComponent);
        if (stack.contains(ComponentInit.LEGACY_POTTED)) {
            stack.remove(ComponentInit.LEGACY_POTTED);
        }
    }

    public static boolean migrateLegacyComponents(ItemStack stack) {
        boolean changed = false;
        if (stack.contains(ComponentInit.LEGACY_SPOILED)) {
            SpoiledComponent legacySpoiledComponent = stack.get(ComponentInit.LEGACY_SPOILED);
            stack.remove(ComponentInit.LEGACY_SPOILED);
            if (legacySpoiledComponent != null && !stack.contains(ComponentInit.SPOILED)) {
                stack.set(ComponentInit.SPOILED, legacySpoiledComponent);
            }
            changed = true;
        }
        if (stack.contains(ComponentInit.LEGACY_POTTED)) {
            SpoiledComponent legacyPottedComponent = stack.get(ComponentInit.LEGACY_POTTED);
            stack.remove(ComponentInit.LEGACY_POTTED);
            if (legacyPottedComponent != null && !stack.contains(ComponentInit.POTTED)) {
                stack.set(ComponentInit.POTTED, legacyPottedComponent);
            }
            changed = true;
        }
        return changed;
    }

    public static void setItemStackSpoilage(World world, ItemStack stack, @Nullable List<ItemStack> recipeStacks) {
        if (!world.isClient() && isSpoilable(stack)) {
            if (recipeStacks == null) {
                if (!hasSpoilage(stack)) {
                    setSpoiledComponent(stack, getCurrentSpoiledComponent(world));
                }
            } else {
                int seasonCount = getCurrentSeasonCount(world);
                if (!ConfigInit.CONFIG.freshCrafting) {
                    for (ItemStack inputStack : recipeStacks) {
                        if (inputStack != null && !inputStack.isEmpty()) {
                            SpoiledComponent inputSpoiledComponent = getSpoiledComponent(inputStack);
                            if (inputSpoiledComponent != null) {
                                int inputSeasonCount = getSeasonCount(inputSpoiledComponent);
                                if (inputSeasonCount != INVALID_SEASON_COUNT && inputSeasonCount < seasonCount) {
                                    seasonCount = inputSeasonCount;
                                }
                            }
                        }
                    }
                }
                setSpoiledComponent(stack, fromSeasonCount(seasonCount));
            }
        }
    }

    public static void keepOlderSpoilage(ItemStack stack, @Nullable SpoiledComponent previousSpoiledComponent) {
        SpoiledComponent spoiledComponent = getSpoiledComponent(stack);
        if (previousSpoiledComponent != null && spoiledComponent != null) {
            int previousSeasonCount = getSeasonCount(previousSpoiledComponent);
            int currentSeasonCount = getSeasonCount(spoiledComponent);
            if (previousSeasonCount != INVALID_SEASON_COUNT && (currentSeasonCount == INVALID_SEASON_COUNT || previousSeasonCount < currentSeasonCount)) {
                setSpoiledComponent(stack, previousSpoiledComponent);
            }
        }
    }

    public static boolean onStackSeen(World world, ItemStack stack) {
        return onStackSeen(world, stack, true);
    }

    public static boolean onStackSeen(World world, ItemStack stack, boolean removePotted) {
        if (world == null || world.isClient() || stack == null || stack.isEmpty()) {
            return false;
        }
        boolean changed = migrateLegacyComponents(stack);
        if (removePotted && removePotted(world, stack)) {
            changed = true;
        }
        if (!hasSpoilage(stack) && isSpoilable(stack)) {
            setSpoiledComponent(stack, getCurrentSpoiledComponent(world));
            changed = true;
        }
        return changed;
    }

    public static boolean removePotted(World world, ItemStack stack) {
        SpoiledComponent pottedComponent = getPottedComponent(stack);
        if (pottedComponent == null) {
            return false;
        }
        int seasonsPassedBy = getSeasonsPassedBy(world, pottedComponent);
        if (seasonsPassedBy > 0) {
            setSeasonsPassedBy(stack, seasonsPassedBy);
        }
        stack.remove(ComponentInit.POTTED);
        stack.remove(ComponentInit.LEGACY_POTTED);
        return true;
    }

    @Nullable
    public static World getInventoryWorld(@Nullable Inventory inventory) {
        if (inventory instanceof BlockEntity blockEntity) {
            return blockEntity.getWorld();
        } else if (inventory instanceof Entity entity) {
            return entity.getWorld();
        }
        return null;
    }

    public static void setSeasonsPassedBy(ItemStack stack, int seasonsPassedBy) {
        SpoiledComponent spoiledComponent = getSpoiledComponent(stack);
        if (spoiledComponent != null && isSpoilable(stack)) {
            int seasonCount = getSeasonCount(spoiledComponent);
            if (seasonCount != INVALID_SEASON_COUNT) {
                setSpoiledComponent(stack, fromSeasonCount(seasonCount + seasonsPassedBy));
            }
        }
    }

    public static int getSeasonsPassedBy(World world, String storedSeason, int storedYear) {
        return getSeasonsPassedBy(world, new SpoiledComponent(storedSeason, storedYear));
    }

    public static int getSeasonsPassedBy(World world, SpoiledComponent spoiledComponent) {
        int storedSeasonCount = getSeasonCount(spoiledComponent);
        if (storedSeasonCount == INVALID_SEASON_COUNT) {
            return 0;
        }
        return Math.max(0, getCurrentSeasonCount(world) - storedSeasonCount);
    }

    public static List<String> getSeasons() {
        return EventInit.isSereneSeasonsLoaded ? SpoiledZMain.SERENE_SEASONS : SpoiledZMain.FABRIC_SEASONS;
    }

    public static int getSeasonIndex(String season) {
        int seasonIndex = SpoiledZMain.FABRIC_SEASONS.indexOf(season);
        if (seasonIndex == -1) {
            seasonIndex = SpoiledZMain.SERENE_SEASONS.indexOf(season);
        }
        return seasonIndex;
    }

    public static int getSeasonCount(SpoiledComponent spoiledComponent) {
        int seasonIndex = getSeasonIndex(spoiledComponent.season());
        if (seasonIndex == -1) {
            return INVALID_SEASON_COUNT;
        }
        return spoiledComponent.year() * 4 + seasonIndex;
    }

    public static SpoiledComponent fromSeasonCount(int seasonCount) {
        return new SpoiledComponent(getSeasons().get(Math.floorMod(seasonCount, 4)), Math.floorDiv(seasonCount, 4));
    }

    public static SpoiledComponent getCurrentSpoiledComponent(World world) {
        return fromSeasonCount(getCurrentSeasonCount(world));
    }

    public static int getCurrentSeasonCount(World world) {
        long time = world.getTime();
        SeasonCountCache cache = world.isClient() ? clientSeasonCountCache : serverSeasonCountCache;
        if (cache != null && cache.time() == time && cache.world().get() == world) {
            return cache.seasonCount();
        }
        int seasonCount = calculateCurrentSeasonCount(world);
        SeasonCountCache newCache = new SeasonCountCache(new WeakReference<>(world), time, seasonCount);
        if (world.isClient()) {
            clientSeasonCountCache = newCache;
        } else {
            serverSeasonCountCache = newCache;
        }
        return seasonCount;
    }

    private static int calculateCurrentSeasonCount(World world) {
        int seasonIndex = Math.max(0, getSeasonIndex(getCurrentSeason(world)));
        long seasonLength;
        if (EventInit.isSereneSeasonsLoaded) {
            seasonLength = SeasonHelper.getSeasonState(world).getSeasonDuration();
        } else {
            seasonLength = FabricSeasons.getCurrentSeason(world).getSeasonLength();
        }
        double estimatedSeasonCount = seasonLength > 0 ? world.getTimeOfDay() / (double) seasonLength : 0.0D;
        return seasonIndex + 4 * (int) Math.round((estimatedSeasonCount - seasonIndex) / 4.0D);
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
        return Math.floorDiv(getCurrentSeasonCount(world), 4);
    }

    public static boolean hasSpoilage(ItemStack stack) {
        return stack.contains(ComponentInit.SPOILED) || stack.contains(ComponentInit.LEGACY_SPOILED);
    }

    public static boolean isSpoilable(ItemStack stack) {
        return (stack.contains(DataComponentTypes.FOOD) || stack.isIn(TagInit.SPOILING_ITEMS)) && !stack.isIn(TagInit.NON_SPOILING_ITEMS);
    }

    public static boolean isSpoilageComponent(ComponentType<?> type) {
        return type == ComponentInit.SPOILED || type == ComponentInit.POTTED || type == ComponentInit.LEGACY_SPOILED || type == ComponentInit.LEGACY_POTTED;
    }

    public static boolean areComponentsEqualIgnoringSpoilage(ItemStack itemStack, ItemStack itemStack2) {
        ComponentChanges changes = itemStack.getComponentChanges();
        ComponentChanges changes2 = itemStack2.getComponentChanges();
        int count = 0;
        for (Map.Entry<ComponentType<?>, Optional<?>> entry : changes.entrySet()) {
            if (isSpoilageComponent(entry.getKey())) {
                continue;
            }
            if (!entry.getValue().equals(changes2.get(entry.getKey()))) {
                return false;
            }
            count++;
        }
        int count2 = 0;
        for (Map.Entry<ComponentType<?>, Optional<?>> entry : changes2.entrySet()) {
            if (!isSpoilageComponent(entry.getKey())) {
                count2++;
            }
        }
        return count == count2;
    }

    public static int hashComponentsIgnoringSpoilage(ItemStack stack) {
        int hash = 0;
        for (Map.Entry<ComponentType<?>, Optional<?>> entry : stack.getComponentChanges().entrySet()) {
            if (!isSpoilageComponent(entry.getKey())) {
                hash += entry.hashCode();
            }
        }
        return hash;
    }

    public static boolean isSpoilageEqual(ItemStack itemStack, ItemStack itemStack2) {
        if (itemStack != null && itemStack2 != null && isSpoilable(itemStack) && isSpoilable(itemStack2)) {
            SpoiledComponent spoiledComponent = getSpoiledComponent(itemStack);
            SpoiledComponent spoiledComponent2 = getSpoiledComponent(itemStack2);
            if (spoiledComponent != null && spoiledComponent2 != null) {
                int seasonCount = getSeasonCount(spoiledComponent);
                int seasonCount2 = getSeasonCount(spoiledComponent2);
                return seasonCount != INVALID_SEASON_COUNT && seasonCount2 != INVALID_SEASON_COUNT && Math.abs(seasonCount - seasonCount2) < 2;
            }
            return true;
        }
        return false;
    }

    public static float getSpoiledFoodFactor(int spoiledTime) {
        if (spoiledTime == 3) {
            return 0.8f;
        } else if (spoiledTime >= 4) {
            return 0.5f;
        }
        return 1.0f;
    }

    public static FoodComponent getSpoiledFoodComponent(FoodComponent original, int spoiledTime) {
        if (spoiledTime < 3) {
            return original;
        }
        float factor = getSpoiledFoodFactor(spoiledTime);
        return new FoodComponent((int) (original.nutrition() * factor), original.saturation() * factor, original.canAlwaysEat(), original.eatSeconds(), original.usingConvertsTo(), original.effects());
    }

    public static void applySpoiledEffects(LivingEntity livingEntity, int spoiledTime) {
        if (livingEntity == null || livingEntity.getWorld().isClient()) {
            return;
        }
        if (spoiledTime == 3 && livingEntity.getWorld().getRandom().nextFloat() < (float) (ConfigInit.CONFIG.effectChance / 100f)) {
            livingEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.POISON, (int) (ConfigInit.CONFIG.effectDuration * 0.66f), 1));
        } else if (spoiledTime >= 4) {
            livingEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.NAUSEA, ConfigInit.CONFIG.effectDuration / 2, 0));
            livingEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.POISON, ConfigInit.CONFIG.effectDuration, 1));
            if (livingEntity.getWorld().getRandom().nextFloat() < 0.05f) {
                livingEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS, ConfigInit.CONFIG.effectDuration / 3, 0));
            }
        }
    }

    public static class FoodBlockState extends PersistentState {

        private static final String ID = "spoiledz_food_blocks";
        private static final String LEGACY_ID = "food_block_map";

        private final HashMap<BlockPos, SpoiledComponent> FOOD_BLOCK_MAP = new HashMap<>();

        private final ServerWorld world;

        public FoodBlockState(ServerWorld world) {
            this.world = world;
        }

        public static PersistentState.Type<FoodBlockState> getPersistentStateType(ServerWorld world) {
            return new PersistentState.Type<>(() -> new FoodBlockState(world), (nbt, registryLookup) -> fromNbt(world, nbt), null);
        }

        public static FoodBlockState getOrCreate(ServerWorld world) {
            PersistentStateManager persistentStateManager = world.getPersistentStateManager();
            PersistentState.Type<FoodBlockState> type = getPersistentStateType(world);
            FoodBlockState foodBlockState = persistentStateManager.get(type, ID);
            if (foodBlockState == null) {
                foodBlockState = persistentStateManager.getOrCreate(type, ID);
                FoodBlockState legacyFoodBlockState = persistentStateManager.get(type, LEGACY_ID);
                if (legacyFoodBlockState != null && !legacyFoodBlockState.FOOD_BLOCK_MAP.isEmpty()) {
                    foodBlockState.FOOD_BLOCK_MAP.putAll(legacyFoodBlockState.FOOD_BLOCK_MAP);
                    foodBlockState.markDirty();
                }
            }
            return foodBlockState;
        }

        public ServerWorld getWorld() {
            return this.world;
        }

        @Override
        public NbtCompound writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
            NbtList foodBlocks = new NbtList();
            for (Map.Entry<BlockPos, SpoiledComponent> entry : this.FOOD_BLOCK_MAP.entrySet()) {
                NbtCompound foodBlock = new NbtCompound();
                foodBlock.putInt("X", entry.getKey().getX());
                foodBlock.putInt("Y", entry.getKey().getY());
                foodBlock.putInt("Z", entry.getKey().getZ());
                foodBlock.putInt("Year", entry.getValue().year());
                foodBlock.putString("Season", entry.getValue().season());
                foodBlocks.add(foodBlock);
            }
            nbt.put("FoodBlocks", foodBlocks);
            return nbt;
        }

        public static FoodBlockState fromNbt(ServerWorld world, NbtCompound nbt) {
            FoodBlockState foodBlockState = new FoodBlockState(world);
            if (nbt.contains("FoodBlocks", NbtElement.LIST_TYPE)) {
                NbtList foodBlocks = nbt.getList("FoodBlocks", NbtElement.COMPOUND_TYPE);
                for (int i = 0; i < foodBlocks.size(); i++) {
                    NbtCompound foodBlock = foodBlocks.getCompound(i);
                    foodBlockState.FOOD_BLOCK_MAP.put(new BlockPos(foodBlock.getInt("X"), foodBlock.getInt("Y"), foodBlock.getInt("Z")),
                            new SpoiledComponent(foodBlock.getString("Season"), foodBlock.getInt("Year")));
                }
            } else {
                for (int i = 0; i < nbt.getInt("FoodBlockMapSize"); i++) {
                    if (nbt.contains("FoodBlockSeason" + i, NbtElement.STRING_TYPE)) {
                        foodBlockState.FOOD_BLOCK_MAP.put(new BlockPos(nbt.getInt("FoodBlockX" + i), nbt.getInt("FoodBlockY" + i), nbt.getInt("FoodBlockZ" + i)),
                                new SpoiledComponent(nbt.getString("FoodBlockSeason" + i), nbt.getInt("FoodBlockYear" + i)));
                    }
                }
            }
            return foodBlockState;
        }

        public void addFoodBlock(BlockPos pos, SpoiledComponent spoiledComponent) {
            this.FOOD_BLOCK_MAP.put(pos.toImmutable(), spoiledComponent);
            this.markDirty();
        }

        public void removeFoodBlock(BlockPos pos) {
            if (this.FOOD_BLOCK_MAP.remove(pos) != null) {
                this.markDirty();
            }
        }

        public HashMap<BlockPos, SpoiledComponent> getFoodBlockMap() {
            return this.FOOD_BLOCK_MAP;
        }

        public void tick() {
            if (this.FOOD_BLOCK_MAP.isEmpty()) {
                return;
            }
            Iterator<Map.Entry<BlockPos, SpoiledComponent>> iterator = this.FOOD_BLOCK_MAP.entrySet().iterator();
            List<BlockPos> spoiledPositions = null;
            while (iterator.hasNext()) {
                Map.Entry<BlockPos, SpoiledComponent> entry = iterator.next();
                BlockPos pos = entry.getKey();
                if (!this.world.isChunkLoaded(ChunkSectionPos.getSectionCoord(pos.getX()), ChunkSectionPos.getSectionCoord(pos.getZ()))) {
                    continue;
                }
                BlockState state = this.world.getBlockState(pos);
                if (state.contains(SPOILED)) {
                    if (state.get(SPOILED)) {
                        iterator.remove();
                        this.markDirty();
                    } else if (getSpoilingTime(this.world, entry.getValue()) >= 4) {
                        iterator.remove();
                        this.markDirty();
                        if (spoiledPositions == null) {
                            spoiledPositions = new ArrayList<>();
                        }
                        spoiledPositions.add(pos);
                    }
                } else if (!(state.getBlock() instanceof CandleCakeBlock)) {
                    iterator.remove();
                    this.markDirty();
                }
            }
            if (spoiledPositions != null) {
                for (BlockPos pos : spoiledPositions) {
                    BlockState state = this.world.getBlockState(pos);
                    if (state.contains(SPOILED)) {
                        this.world.setBlockState(pos, state.with(SPOILED, true));
                    }
                }
            }
        }

        public static int getFoodBlockSpoilingTime(ServerWorld world, BlockPos pos, BlockState state) {
            if (state.contains(SPOILED) && state.get(SPOILED)) {
                return 4;
            }
            SpoiledComponent spoiledComponent = ((ServerWorldAccess) world).getFoodBlockState().getFoodBlockMap().get(pos);
            return getSpoilingTime(world, spoiledComponent);
        }

        public static void onRemovedFoodBlock(World world, BlockPos pos, BlockState newState) {
            if (world instanceof ServerWorldAccess serverWorldAccess && serverWorldAccess.getFoodBlockState().getFoodBlockMap().containsKey(pos)) {
                if (newState.getBlock() instanceof CandleCakeBlock) {
                    return;
                }
                if (newState.contains(SPOILED) && !newState.get(SPOILED)) {
                    return;
                }
                serverWorldAccess.getFoodBlockState().removeFoodBlock(pos);
            }
        }

        public static void onPlacedFoodBlock(World world, BlockPos pos, BlockState state, ItemStack itemStack) {
            if (world instanceof ServerWorld serverWorld) {
                SpoiledComponent spoiledComponent = getSpoiledComponent(itemStack);
                if (spoiledComponent == null) {
                    spoiledComponent = getCurrentSpoiledComponent(serverWorld);
                }
                if (getSpoilingTime(serverWorld, spoiledComponent) >= 4) {
                    serverWorld.setBlockState(pos, state.with(SpoiledUtil.SPOILED, true));
                } else {
                    ((ServerWorldAccess) serverWorld).getFoodBlockState().addFoodBlock(pos, spoiledComponent);
                    if (state.get(SpoiledUtil.SPOILED)) {
                        serverWorld.setBlockState(pos, state.with(SpoiledUtil.SPOILED, false));
                    }
                }
            }
        }

    }

}
