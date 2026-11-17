package net.spoiledz.util;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CandleCakeBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.world.level.storage.SavedDataStorage;
import net.spoiledz.SpoiledZMain;
import net.spoiledz.access.ServerWorldAccess;
import net.spoiledz.mixin.access.DataComponentPatchAccessor;
import net.spoiledz.init.ComponentInit;
import net.spoiledz.init.ConfigInit;
import net.spoiledz.init.TagInit;
import org.jetbrains.annotations.Nullable;

import sereneseasons.api.season.SeasonHelper;

public class SpoiledUtil {

    public static final BooleanProperty SPOILED = BooleanProperty.create("spoiled");

    private static final int INVALID_SEASON_COUNT = Integer.MIN_VALUE;

    private static volatile SeasonCountCache clientSeasonCountCache = null;
    private static volatile SeasonCountCache serverSeasonCountCache = null;

    private record SeasonCountCache(WeakReference<Level> world, long time, int seasonCount) {
    }

    // 0 = 0%, 1=25%, 2=50%, 3=75%, 4=100%
    public static int getSpoilingTime(Level world, ItemStack stack) {
        if (stack == null) {
            return -1;
        }
        return getSpoilingTime(world, getSpoiledComponent(stack));
    }

    public static int getSpoilingTime(Level world, @Nullable SpoiledComponent spoiledComponent) {
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
        if (stack.has(ComponentInit.LEGACY_SPOILED)) {
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
        if (stack.has(ComponentInit.LEGACY_POTTED)) {
            stack.remove(ComponentInit.LEGACY_POTTED);
        }
    }

    public static boolean migrateLegacyComponents(ItemStack stack) {
        boolean changed = false;
        if (stack.has(ComponentInit.LEGACY_SPOILED)) {
            SpoiledComponent legacySpoiledComponent = stack.get(ComponentInit.LEGACY_SPOILED);
            stack.remove(ComponentInit.LEGACY_SPOILED);
            if (legacySpoiledComponent != null && !stack.has(ComponentInit.SPOILED)) {
                stack.set(ComponentInit.SPOILED, legacySpoiledComponent);
            }
            changed = true;
        }
        if (stack.has(ComponentInit.LEGACY_POTTED)) {
            SpoiledComponent legacyPottedComponent = stack.get(ComponentInit.LEGACY_POTTED);
            stack.remove(ComponentInit.LEGACY_POTTED);
            if (legacyPottedComponent != null && !stack.has(ComponentInit.POTTED)) {
                stack.set(ComponentInit.POTTED, legacyPottedComponent);
            }
            changed = true;
        }
        return changed;
    }

    public static void setItemStackSpoilage(Level world, ItemStack stack, @Nullable List<ItemStack> recipeStacks) {
        if (!world.isClientSide() && isSpoilable(stack)) {
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

    public static boolean onStackSeen(Level world, ItemStack stack) {
        return onStackSeen(world, stack, true);
    }

    public static boolean onStackSeen(Level world, ItemStack stack, boolean removePotted) {
        if (world == null || world.isClientSide() || stack == null || stack.isEmpty()) {
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

    public static boolean removePotted(Level world, ItemStack stack) {
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
    public static Level getInventoryWorld(@Nullable Container inventory) {
        if (inventory instanceof BlockEntity blockEntity) {
            return blockEntity.getLevel();
        } else if (inventory instanceof Entity entity) {
            return entity.level();
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

    public static int getSeasonsPassedBy(Level world, String storedSeason, int storedYear) {
        return getSeasonsPassedBy(world, new SpoiledComponent(storedSeason, storedYear));
    }

    public static int getSeasonsPassedBy(Level world, SpoiledComponent spoiledComponent) {
        int storedSeasonCount = getSeasonCount(spoiledComponent);
        if (storedSeasonCount == INVALID_SEASON_COUNT) {
            return 0;
        }
        return Math.max(0, getCurrentSeasonCount(world) - storedSeasonCount);
    }

    public static List<String> getSeasons() {
        return SpoiledZMain.SERENE_SEASONS;
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

    public static SpoiledComponent getCurrentSpoiledComponent(Level world) {
        return fromSeasonCount(getCurrentSeasonCount(world));
    }

    public static int getCurrentSeasonCount(Level world) {
        long time = world.getGameTime();
        SeasonCountCache cache = world.isClientSide() ? clientSeasonCountCache : serverSeasonCountCache;
        if (cache != null && cache.time() == time && cache.world().get() == world) {
            return cache.seasonCount();
        }
        int seasonCount = calculateCurrentSeasonCount(world);
        SeasonCountCache newCache = new SeasonCountCache(new WeakReference<>(world), time, seasonCount);
        if (world.isClientSide()) {
            clientSeasonCountCache = newCache;
        } else {
            serverSeasonCountCache = newCache;
        }
        return seasonCount;
    }

    private static int calculateCurrentSeasonCount(Level world) {
        int seasonIndex = Math.max(0, getSeasonIndex(getCurrentSeason(world)));
        long seasonLength = SeasonHelper.getSeasonState(world).getSeasonDuration();
        double estimatedSeasonCount = seasonLength > 0 ? world.getOverworldClockTime() / (double) seasonLength : 0.0D;
        return seasonIndex + 4 * (int) Math.round((estimatedSeasonCount - seasonIndex) / 4.0D);
    }

    public static String getCurrentSeason(Level world) {
        return SeasonHelper.getSeasonState(world).getSeason().name();
    }

    public static int getCurrentYear(Level world) {
        return Math.floorDiv(getCurrentSeasonCount(world), 4);
    }

    public static boolean hasSpoilage(ItemStack stack) {
        return stack.has(ComponentInit.SPOILED) || stack.has(ComponentInit.LEGACY_SPOILED);
    }

    public static boolean isSpoilable(ItemStack stack) {
        return (stack.has(DataComponents.FOOD) || stack.is(TagInit.SPOILING_ITEMS)) && !stack.is(TagInit.NON_SPOILING_ITEMS);
    }

    public static boolean isSpoilageComponent(DataComponentType<?> type) {
        return type == ComponentInit.SPOILED || type == ComponentInit.POTTED || type == ComponentInit.LEGACY_SPOILED || type == ComponentInit.LEGACY_POTTED;
    }

    private static Map<DataComponentType<?>, Object> getPatchMap(ItemStack stack) {
        return ((DataComponentPatchAccessor) (Object) stack.getComponentsPatch()).getMap();
    }

    public static boolean areComponentsEqualIgnoringSpoilage(ItemStack itemStack, ItemStack itemStack2) {
        Map<DataComponentType<?>, Object> changes = getPatchMap(itemStack);
        Map<DataComponentType<?>, Object> changes2 = getPatchMap(itemStack2);
        int count = 0;
        for (Map.Entry<DataComponentType<?>, Object> entry : changes.entrySet()) {
            if (isSpoilageComponent(entry.getKey())) {
                continue;
            }
            if (!changes2.containsKey(entry.getKey()) || !Objects.equals(entry.getValue(), changes2.get(entry.getKey()))) {
                return false;
            }
            count++;
        }
        int count2 = 0;
        for (DataComponentType<?> type : changes2.keySet()) {
            if (!isSpoilageComponent(type)) {
                count2++;
            }
        }
        return count == count2;
    }

    public static int hashComponentsIgnoringSpoilage(ItemStack stack) {
        int hash = 0;
        for (Map.Entry<DataComponentType<?>, Object> entry : getPatchMap(stack).entrySet()) {
            if (!isSpoilageComponent(entry.getKey())) {
                hash += System.identityHashCode(entry.getKey()) ^ Objects.hashCode(entry.getValue());
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

    public static FoodProperties getSpoiledFoodComponent(FoodProperties original, int spoiledTime) {
        if (spoiledTime < 3) {
            return original;
        }
        float factor = getSpoiledFoodFactor(spoiledTime);
        return new FoodProperties((int) (original.nutrition() * factor), original.saturation() * factor, original.canAlwaysEat());
    }

    public static void applySpoiledEffects(LivingEntity livingEntity, int spoiledTime) {
        if (livingEntity == null || livingEntity.level().isClientSide()) {
            return;
        }
        if (spoiledTime == 3 && livingEntity.level().getRandom().nextFloat() < (float) (ConfigInit.CONFIG.effectChance / 100f)) {
            livingEntity.addEffect(new MobEffectInstance(MobEffects.POISON, (int) (ConfigInit.CONFIG.effectDuration * 0.66f), 1));
        } else if (spoiledTime >= 4) {
            livingEntity.addEffect(new MobEffectInstance(MobEffects.NAUSEA, ConfigInit.CONFIG.effectDuration / 2, 0));
            livingEntity.addEffect(new MobEffectInstance(MobEffects.POISON, ConfigInit.CONFIG.effectDuration, 1));
            if (livingEntity.level().getRandom().nextFloat() < 0.05f) {
                livingEntity.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, ConfigInit.CONFIG.effectDuration / 3, 0));
            }
        }
    }

    public static class FoodBlockState extends SavedData {

        private record FoodBlock(BlockPos pos, SpoiledComponent spoiled) {
            private static final Codec<FoodBlock> CODEC = RecordCodecBuilder.create(instance -> instance
                    .group(BlockPos.CODEC.fieldOf("pos").forGetter(FoodBlock::pos), SpoiledComponent.CODEC.fieldOf("spoiled").forGetter(FoodBlock::spoiled))
                    .apply(instance, FoodBlock::new));
        }

        private static final Codec<FoodBlockState> CODEC = FoodBlock.CODEC.listOf().fieldOf("food_blocks").codec().xmap(FoodBlockState::new,
                foodBlockState -> foodBlockState.FOOD_BLOCK_MAP.entrySet().stream().map(entry -> new FoodBlock(entry.getKey(), entry.getValue())).toList());

        public static final SavedDataType<FoodBlockState> TYPE = new SavedDataType<>(SpoiledZMain.identifierOf("food_blocks"), FoodBlockState::new, CODEC, null);

        private static final List<String> LEGACY_FILES = List.of("spoiledz_food_blocks.dat", "food_block_map.dat");

        private final HashMap<BlockPos, SpoiledComponent> FOOD_BLOCK_MAP = new HashMap<>();

        public FoodBlockState() {
        }

        private FoodBlockState(List<FoodBlock> foodBlocks) {
            for (FoodBlock foodBlock : foodBlocks) {
                this.FOOD_BLOCK_MAP.put(foodBlock.pos(), foodBlock.spoiled());
            }
        }

        public static FoodBlockState getOrCreate(ServerLevel world) {
            SavedDataStorage dataStorage = world.getDataStorage();
            FoodBlockState foodBlockState = dataStorage.get(TYPE);
            if (foodBlockState == null) {
                foodBlockState = dataStorage.computeIfAbsent(TYPE);
                if (world.dimension() == Level.OVERWORLD) {
                    foodBlockState.readLegacyFiles(world.getServer().getWorldPath(LevelResource.DATA));
                }
            }
            return foodBlockState;
        }

        private void readLegacyFiles(Path dataFolder) {
            for (String legacyFile : LEGACY_FILES) {
                Path path = dataFolder.resolve(legacyFile);
                if (!Files.exists(path)) {
                    continue;
                }
                try {
                    CompoundTag nbt = NbtIo.readCompressed(path, NbtAccounter.unlimitedHeap()).getCompoundOrEmpty("data");
                    ListTag foodBlocks = nbt.getListOrEmpty("FoodBlocks");
                    for (int i = 0; i < foodBlocks.size(); i++) {
                        CompoundTag foodBlock = foodBlocks.getCompoundOrEmpty(i);
                        this.FOOD_BLOCK_MAP.putIfAbsent(new BlockPos(foodBlock.getIntOr("X", 0), foodBlock.getIntOr("Y", 0), foodBlock.getIntOr("Z", 0)),
                                new SpoiledComponent(foodBlock.getStringOr("Season", ""), foodBlock.getIntOr("Year", 0)));
                    }
                    for (int i = 0; i < nbt.getIntOr("FoodBlockMapSize", 0); i++) {
                        if (nbt.getString("FoodBlockSeason" + i).isPresent()) {
                            this.FOOD_BLOCK_MAP.putIfAbsent(new BlockPos(nbt.getIntOr("FoodBlockX" + i, 0), nbt.getIntOr("FoodBlockY" + i, 0), nbt.getIntOr("FoodBlockZ" + i, 0)),
                                    new SpoiledComponent(nbt.getStringOr("FoodBlockSeason" + i, ""), nbt.getIntOr("FoodBlockYear" + i, 0)));
                        }
                    }
                    this.setDirty();
                } catch (IOException exception) {
                    SpoiledZMain.LOGGER.warn("Failed to read legacy SpoiledZ food block data from {}", path, exception);
                }
            }
        }

        public void addFoodBlock(BlockPos pos, SpoiledComponent spoiledComponent) {
            this.FOOD_BLOCK_MAP.put(pos.immutable(), spoiledComponent);
            this.setDirty();
        }

        public void removeFoodBlock(BlockPos pos) {
            if (this.FOOD_BLOCK_MAP.remove(pos) != null) {
                this.setDirty();
            }
        }

        public HashMap<BlockPos, SpoiledComponent> getFoodBlockMap() {
            return this.FOOD_BLOCK_MAP;
        }

        public void tick(ServerLevel world) {
            if (this.FOOD_BLOCK_MAP.isEmpty()) {
                return;
            }
            Iterator<Map.Entry<BlockPos, SpoiledComponent>> iterator = this.FOOD_BLOCK_MAP.entrySet().iterator();
            List<BlockPos> spoiledPositions = null;
            while (iterator.hasNext()) {
                Map.Entry<BlockPos, SpoiledComponent> entry = iterator.next();
                BlockPos pos = entry.getKey();
                if (!world.hasChunk(SectionPos.blockToSectionCoord(pos.getX()), SectionPos.blockToSectionCoord(pos.getZ()))) {
                    continue;
                }
                BlockState state = world.getBlockState(pos);
                if (state.hasProperty(SPOILED)) {
                    if (state.getValue(SPOILED)) {
                        iterator.remove();
                        this.setDirty();
                    } else if (getSpoilingTime(world, entry.getValue()) >= 4) {
                        iterator.remove();
                        this.setDirty();
                        if (spoiledPositions == null) {
                            spoiledPositions = new ArrayList<>();
                        }
                        spoiledPositions.add(pos);
                    }
                } else if (!(state.getBlock() instanceof CandleCakeBlock)) {
                    iterator.remove();
                    this.setDirty();
                }
            }
            if (spoiledPositions != null) {
                for (BlockPos pos : spoiledPositions) {
                    BlockState state = world.getBlockState(pos);
                    if (state.hasProperty(SPOILED)) {
                        world.setBlockAndUpdate(pos, state.setValue(SPOILED, true));
                    }
                }
            }
        }

        public static int getFoodBlockSpoilingTime(ServerLevel world, BlockPos pos, BlockState state) {
            if (state.hasProperty(SPOILED) && state.getValue(SPOILED)) {
                return 4;
            }
            SpoiledComponent spoiledComponent = ((ServerWorldAccess) world).getFoodBlockState().getFoodBlockMap().get(pos);
            return getSpoilingTime(world, spoiledComponent);
        }

        public static void onRemovedFoodBlock(Level world, BlockPos pos, BlockState newState) {
            if (world instanceof ServerWorldAccess serverWorldAccess && serverWorldAccess.getFoodBlockState().getFoodBlockMap().containsKey(pos)) {
                if (newState.getBlock() instanceof CandleCakeBlock) {
                    return;
                }
                if (newState.hasProperty(SPOILED) && !newState.getValue(SPOILED)) {
                    return;
                }
                serverWorldAccess.getFoodBlockState().removeFoodBlock(pos);
            }
        }

        public static void onPlacedFoodBlock(Level world, BlockPos pos, BlockState state, ItemStack itemStack) {
            if (world instanceof ServerLevel serverWorld) {
                SpoiledComponent spoiledComponent = getSpoiledComponent(itemStack);
                if (spoiledComponent == null) {
                    spoiledComponent = getCurrentSpoiledComponent(serverWorld);
                }
                if (getSpoilingTime(serverWorld, spoiledComponent) >= 4) {
                    serverWorld.setBlockAndUpdate(pos, state.setValue(SpoiledUtil.SPOILED, true));
                } else {
                    ((ServerWorldAccess) serverWorld).getFoodBlockState().addFoodBlock(pos, spoiledComponent);
                    if (state.getValue(SpoiledUtil.SPOILED)) {
                        serverWorld.setBlockAndUpdate(pos, state.setValue(SpoiledUtil.SPOILED, false));
                    }
                }
            }
        }

    }

}
