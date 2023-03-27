package net.spoiledz.mixin.compat;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import com.nhoryzon.mc.farmersdelight.advancement.CuttingBoardTrigger;
import com.nhoryzon.mc.farmersdelight.block.CuttingBoardBlock;
import com.nhoryzon.mc.farmersdelight.entity.block.CuttingBoardBlockEntity;
import com.nhoryzon.mc.farmersdelight.entity.block.SyncedBlockEntity;
import com.nhoryzon.mc.farmersdelight.recipe.CuttingBoardRecipe;
import com.nhoryzon.mc.farmersdelight.registry.AdvancementsRegistry;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.spoiledz.util.SpoiledUtil;

@SuppressWarnings("rawtypes")
@Mixin(CuttingBoardBlockEntity.class)
public abstract class CuttingBoardBlockEntityMixin extends SyncedBlockEntity {

    public CuttingBoardBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Inject(method = "processItemUsingTool", at = @At(value = "INVOKE_ASSIGN", target = "Lcom/nhoryzon/mc/farmersdelight/entity/block/CuttingBoardBlockEntity;getMatchingRecipe(Lcom/nhoryzon/mc/farmersdelight/entity/block/inventory/RecipeWrapper;Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/player/PlayerEntity;)Ljava/util/Optional;"), cancellable = true, locals = LocalCapture.CAPTURE_FAILSOFT)
    private void processItemUsingToolMixin(ItemStack tool, PlayerEntity player, CallbackInfoReturnable<Boolean> info, Optional<CuttingBoardRecipe> matchingRecipe) {
        if (!this.world.isClient) {
            matchingRecipe.ifPresent((recipe) -> {
                List<ItemStack> results = recipe.getRolledResults(this.world.getRandom(), EnchantmentHelper.getLevel(Enchantments.FORTUNE, tool));
                Iterator var5 = results.iterator();

                while (var5.hasNext()) {
                    ItemStack result = (ItemStack) var5.next();
                    Direction direction = ((Direction) this.getCachedState().get(CuttingBoardBlock.FACING)).rotateYCounterclockwise();
                    ItemEntity entity = new ItemEntity(this.world, (double) this.pos.getX() + 0.5D + (double) direction.getOffsetX() * 0.2D, (double) this.pos.getY() + 0.2D,
                            (double) this.pos.getZ() + 0.5D + (double) direction.getOffsetZ() * 0.2D, result.copy());
                    entity.setVelocity((double) ((float) direction.getOffsetX() * 0.2F), 0.0D, (double) ((float) direction.getOffsetZ() * 0.2F));

                    SpoiledUtil.setItemStackSpoilage(world, entity.getStack(), List.of(this.getStoredItem()));
                    this.world.spawnEntity(entity);
                }

                if (player != null) {
                    tool.damage(1, player, (user) -> {
                        user.sendToolBreakStatus(Hand.MAIN_HAND);
                    });
                } else if (tool.damage(1, this.world.getRandom(), (ServerPlayerEntity) null)) {
                    tool.setCount(0);
                }

                this.playProcessingSound(recipe.getSoundEvent(), tool.getItem(), this.getStoredItem().getItem());
                this.removeItem();
                if (player instanceof ServerPlayerEntity) {
                    ServerPlayerEntity serverPlayer = (ServerPlayerEntity) player;
                    ((CuttingBoardTrigger) AdvancementsRegistry.CUTTING_BOARD.get()).trigger(serverPlayer);
                }

            });
        }
        info.setReturnValue(matchingRecipe.isPresent());
    }

    @Shadow
    public void playProcessingSound(String soundEventID, Item tool, Item boardItem) {
    }

    @Shadow
    public ItemStack getStoredItem() {
        return null;
    }

    @Shadow
    public ItemStack removeItem() {
        return null;
    }
}
