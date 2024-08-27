package net.spoiledz.mixin;

import java.util.ArrayList;
import java.util.Map;
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
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.MutableWorldProperties;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.spoiledz.access.ServerWorldAccess;
import net.spoiledz.util.SpoiledUtil;
import net.spoiledz.util.SpoiledUtil.FoodBlockState;

@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin extends World implements ServerWorldAccess {

    @Nullable
    @Unique
    private String currentSeason = null;
    @Unique
    private FoodBlockState foodBlockState;

    public ServerWorldMixin(MutableWorldProperties properties, RegistryKey<World> registryRef, DynamicRegistryManager registryManager, RegistryEntry<DimensionType> dimensionEntry,
                            Supplier<Profiler> profiler, boolean isClient, boolean debugWorld, long biomeAccess, int maxChainedNeighborUpdates) {
        super(properties, registryRef, registryManager, dimensionEntry, profiler, isClient, debugWorld, biomeAccess, maxChainedNeighborUpdates);
    }

    @Inject(at = @At("TAIL"), method = "<init>")
    private void initMixin(CallbackInfo info) {
        this.foodBlockState = this.getPersistentStateManager().getOrCreate(FoodBlockState.getPersistentStateType((ServerWorld) (Object) this), "food_block_map");
    }

    @Inject(method = "setTimeOfDay", at = @At("HEAD"))
    private void setTimeOfDayMixin(long timeOfDay, CallbackInfo info) {
        SpoiledUtil.onSeasonChange((ServerWorld) (Object) this);
    }

    @Override
    public FoodBlockState getFoodBlockState() {
        return this.foodBlockState;
    }

    @Override
    public void setCurrentSeason(@Nullable String currentSeason) {
        this.currentSeason = currentSeason;
    }

    @Nullable
    @Override
    public String getCurrentSeason() {
        return currentSeason;
    }

    @Shadow
    public abstract PersistentStateManager getPersistentStateManager();

}
