package net.spoiledz.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodConstants;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CakeBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;
import net.spoiledz.util.SpoiledUtil;

@Mixin(CakeBlock.class)
public abstract class CakeBlockMixin extends Block {

    public CakeBlockMixin(Properties settings) {
        super(settings);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void initMixin(BlockBehaviour.Properties settings, CallbackInfo info) {
        this.registerDefaultState(this.stateDefinition.any().setValue(SpoiledUtil.SPOILED, false));
    }

    @Inject(method = "createBlockStateDefinition", at = @At("TAIL"))
    protected void appendPropertiesMixin(StateDefinition.Builder<Block, BlockState> builder, CallbackInfo info) {
        builder.add(SpoiledUtil.SPOILED);
    }

    @Inject(method = "useItemOn", at = @At("HEAD"), cancellable = true)
    private void onUseWithItemMixin(ItemStack stack, BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit, CallbackInfoReturnable<InteractionResult> info) {
        if (stack.is(ItemTags.CANDLES) && state.hasProperty(SpoiledUtil.SPOILED) && state.getValue(SpoiledUtil.SPOILED)) {
            info.setReturnValue(InteractionResult.TRY_WITH_EMPTY_HAND);
        }
    }

    @Redirect(method = "eat", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/food/FoodData;eat(IF)V"))
    private static void tryEatMixin(FoodData hungerManager, int food, float saturationModifier, LevelAccessor world, BlockPos pos, BlockState state, Player player) {
        if (world instanceof ServerLevel serverWorld) {
            int spoiledTime = SpoiledUtil.FoodBlockState.getFoodBlockSpoilingTime(serverWorld, pos, state);
            if (spoiledTime >= 3) {
                float factor = SpoiledUtil.getSpoiledFoodFactor(spoiledTime);
                int spoiledFood = Math.max(1, (int) (food * factor));
                float spoiledSaturation = FoodConstants.saturationByModifier(food, saturationModifier) * factor;
                hungerManager.eat(spoiledFood, spoiledSaturation / (2.0F * spoiledFood));
                SpoiledUtil.applySpoiledEffects(player, spoiledTime);
                return;
            }
        }
        hungerManager.eat(food, saturationModifier);
    }

    @Override
    public void setPlacedBy(Level world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
        super.setPlacedBy(world, pos, state, placer, itemStack);
        SpoiledUtil.FoodBlockState.onPlacedFoodBlock(world, pos, state, itemStack);
    }

    @Override
    protected void affectNeighborsAfterRemoval(BlockState state, ServerLevel world, BlockPos pos, boolean movedByPiston) {
        super.affectNeighborsAfterRemoval(state, world, pos, movedByPiston);
        SpoiledUtil.FoodBlockState.onRemovedFoodBlock(world, pos, world.getBlockState(pos));
    }

}
