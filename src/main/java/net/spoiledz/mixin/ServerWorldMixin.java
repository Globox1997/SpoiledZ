package net.spoiledz.mixin;

import java.util.function.Supplier;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.storage.WritableLevelData;
import net.spoiledz.access.ServerWorldAccess;
import net.spoiledz.util.SpoiledUtil.FoodBlockState;

@Mixin(ServerLevel.class)
public abstract class ServerWorldMixin extends Level implements ServerWorldAccess {

    @Unique
    private FoodBlockState foodBlockState;

    public ServerWorldMixin(WritableLevelData properties, ResourceKey<Level> registryRef, RegistryAccess registryManager, Holder<DimensionType> dimensionEntry,
                            Supplier<ProfilerFiller> profiler, boolean isClient, boolean debugWorld, long biomeAccess, int maxChainedNeighborUpdates) {
        super(properties, registryRef, registryManager, dimensionEntry, profiler, isClient, debugWorld, biomeAccess, maxChainedNeighborUpdates);
    }

    @Inject(at = @At("TAIL"), method = "<init>")
    private void initMixin(CallbackInfo info) {
        this.foodBlockState = FoodBlockState.getOrCreate((ServerLevel) (Object) this);
    }

    @Override
    public FoodBlockState getFoodBlockState() {
        return this.foodBlockState;
    }

}
