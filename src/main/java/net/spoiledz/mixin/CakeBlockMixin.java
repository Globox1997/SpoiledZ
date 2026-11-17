package net.spoiledz.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CakeBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.HungerConstants;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
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

    @Inject(method = "onUseWithItem", at = @At("HEAD"), cancellable = true)
    private void onUseWithItemMixin(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit, CallbackInfoReturnable<ItemActionResult> info) {
        if (stack.isIn(ItemTags.CANDLES) && state.contains(SpoiledUtil.SPOILED) && state.get(SpoiledUtil.SPOILED)) {
            info.setReturnValue(ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION);
        }
    }

    @Redirect(method = "tryEat", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/HungerManager;add(IF)V"))
    private static void tryEatMixin(HungerManager hungerManager, int food, float saturationModifier, WorldAccess world, BlockPos pos, BlockState state, PlayerEntity player) {
        if (world instanceof ServerWorld serverWorld) {
            int spoiledTime = SpoiledUtil.FoodBlockState.getFoodBlockSpoilingTime(serverWorld, pos, state);
            if (spoiledTime >= 3) {
                float factor = SpoiledUtil.getSpoiledFoodFactor(spoiledTime);
                int spoiledFood = Math.max(1, (int) (food * factor));
                float spoiledSaturation = HungerConstants.calculateSaturation(food, saturationModifier) * factor;
                hungerManager.add(spoiledFood, spoiledSaturation / (2.0F * spoiledFood));
                SpoiledUtil.applySpoiledEffects(player, spoiledTime);
                return;
            }
        }
        hungerManager.add(food, saturationModifier);
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
        super.onPlaced(world, pos, state, placer, itemStack);
        SpoiledUtil.FoodBlockState.onPlacedFoodBlock(world, pos, state, itemStack);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        super.onStateReplaced(state, world, pos, newState, moved);
        SpoiledUtil.FoodBlockState.onRemovedFoodBlock(world, pos, newState);
    }

}
