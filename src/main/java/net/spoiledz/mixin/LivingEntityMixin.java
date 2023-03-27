package net.spoiledz.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import io.github.lucaargolo.seasons.FabricSeasons;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.spoiledz.init.TagInit;
import net.spoiledz.util.SpoiledUtil;

@SuppressWarnings("unused")
@Mixin(value = LivingEntity.class, priority = 1001)
public abstract class LivingEntityMixin extends Entity {

    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    // @Inject(method = "dropLoot", at = @At(value = "INVOKE", target = "Lnet/minecraft/loot/LootTable;generateLoot(Lnet/minecraft/loot/context/LootContext;Ljava/util/function/Consumer;)V"),
    // cancellable = true, locals = LocalCapture.CAPTURE_FAILSOFT)
    // private void dropLootMixin(DamageSource source, boolean causedByPlayer, CallbackInfo info, Identifier identifier, LootTable lootTable, LootContext.Builder builder) {
    // if (isEntityInstanceOfMobEnity) {
    // lootTable.generateLoot(builder.build(LootContextTypes.ENTITY), this::addingInventoryItems);
    // info.cancel();
    // }

    // }

    @Override
    public ItemEntity dropStack(ItemStack stack) {
        SpoiledUtil.setItemStackSpoilage(world, stack, null);

        return super.dropStack(stack);
    }

}
