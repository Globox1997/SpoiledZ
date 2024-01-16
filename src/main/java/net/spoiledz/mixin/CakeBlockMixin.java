package net.spoiledz.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CakeBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.spoiledz.util.SpoiledUtil;

@Mixin(CakeBlock.class)
public abstract class CakeBlockMixin extends Block {

    public CakeBlockMixin(Settings settings) {
        super(settings);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void initMixin(AbstractBlock.Settings settings, CallbackInfo info) {
        this.setDefaultState(this.stateManager.getDefaultState().with(SpoiledUtil.SPOILED, false));
    }

    @Inject(method = "appendProperties", at = @At("TAIL"))
    protected void appendPropertiesMixin(StateManager.Builder<Block, BlockState> builder, CallbackInfo info) {
        builder.add(SpoiledUtil.SPOILED);
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
        super.onPlaced(world, pos, state, placer, itemStack);
        SpoiledUtil.FoodBlockMap.onPlacedFoodBlock(world, pos, state, itemStack);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        super.onStateReplaced(state, world, pos, newState, moved);
        SpoiledUtil.FoodBlockMap.onRemovedFoodBlock(world, pos, newState);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        super.scheduledTick(state, world, pos, random);
        SpoiledUtil.FoodBlockMap.scheduledTickFoodBlock(world, pos, state);
    }

}
