package net.spoiledz.mixin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.lucaargolo.seasons.FabricSeasons;
import io.github.lucaargolo.seasons.utils.Season;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.WorldGenerationProgressListener;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.RandomSequencesState;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.MutableWorldProperties;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.level.ServerWorldProperties;
import net.minecraft.world.level.storage.LevelStorage;
import net.minecraft.world.spawner.Spawner;
import net.spoiledz.access.ServerWorldAccess;
import net.spoiledz.util.SpoiledUtil;
import net.spoiledz.util.SpoiledUtil.FoodBlockMap;

@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin extends World implements ServerWorldAccess {

    @Unique
    private Season currentSeason = null;
    @Unique
    private FoodBlockMap foodBlockMap;

    public ServerWorldMixin(MutableWorldProperties properties, RegistryKey<World> registryRef, DynamicRegistryManager registryManager, RegistryEntry<DimensionType> dimensionEntry,
            Supplier<Profiler> profiler, boolean isClient, boolean debugWorld, long biomeAccess, int maxChainedNeighborUpdates) {
        super(properties, registryRef, registryManager, dimensionEntry, profiler, isClient, debugWorld, biomeAccess, maxChainedNeighborUpdates);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void initMixin(MinecraftServer server, Executor workerExecutor, LevelStorage.Session session, ServerWorldProperties properties, RegistryKey<World> worldKey,
            DimensionOptions dimensionOptions, WorldGenerationProgressListener worldGenerationProgressListener, boolean debugWorld, long seed, List<Spawner> spawners, boolean shouldTickTime,
            @Nullable RandomSequencesState randomSequencesState, CallbackInfo info) {
        this.foodBlockMap = this.getPersistentStateManager().getOrCreate(nbt -> SpoiledUtil.FoodBlockMap.fromNbt((ServerWorld) (Object) this, nbt), SpoiledUtil.FoodBlockMap::new, "food_block_map");
    }

    @Inject(method = "setTimeOfDay", at = @At("HEAD"))
    private void setTimeOfDayMixin(long timeOfDay, CallbackInfo info) {
        if ((int) timeOfDay % 20 == 0 && this.currentSeason == null || this.currentSeason != FabricSeasons.getCurrentSeason()) {
            if (this.currentSeason != null) {
                for (Map.Entry<BlockPos, ItemStack> entry : new ArrayList<>(this.foodBlockMap.getFoodBlockMap().entrySet())) {
                    if (this.getBlockState(entry.getKey()).contains(SpoiledUtil.SPOILED)) {
                        int spoilTime = SpoiledUtil.getSpoilingTime(this, entry.getValue());
                        if (spoilTime != -1 && spoilTime >= 4) {
                            this.getBlockState(entry.getKey()).scheduledTick((ServerWorld) (Object) this, entry.getKey(), random);
                            this.foodBlockMap.removeFoodBlock(entry.getKey());
                        }
                    } else {
                        this.foodBlockMap.removeFoodBlock(entry.getKey());
                    }
                }
            }
            this.currentSeason = FabricSeasons.getCurrentSeason();
        }
    }

    @Override
    public FoodBlockMap getFoodBlockMap() {
        return this.foodBlockMap;
    }

    @Shadow
    public PersistentStateManager getPersistentStateManager() {
        return null;
    }

}
