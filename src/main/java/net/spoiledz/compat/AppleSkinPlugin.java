package net.spoiledz.compat;

// import squeek.appleskin.api.AppleSkinApi;
// import squeek.appleskin.api.event.FoodValuesEvent;

// //Unused since capsaicin is used
// @SuppressWarnings("unused")
// public class AppleSkinPlugin implements AppleSkinApi {
// @Override
// public void registerEvents() {
// FoodValuesEvent.EVENT.register(event -> {
// if (event.player != null) {
// ItemStack stack = event.itemStack;
// if (FabricLoader.getInstance().isModLoaded("polymer")) {
// Identifier polymerId = SoFPolymer.getPolymerItemId(event.itemStack);
// if (polymerId != null && polymerId.getNamespace().equals(SpiceOfFabric.MOD_ID)) {
// Item item = SoFPolymer.getPolymerItem(polymerId);
// if (item instanceof FoodContainerItem containerItem) {
// NbtCompound nbt = SoFPolymer.getPolymerTag(stack.getOrCreateNbt());
// stack = new ItemStack(item);
// stack.setNbt(nbt);
// stack = processFoodContainer(event, stack, containerItem);
// }
// }
// }

// if (stack.getItem() instanceof FoodContainerItem containerItem) {
// stack = processFoodContainer(event, stack, containerItem);
// }

// int hungerValue = event.modifiedFoodValues.hunger;
// float saturationValue = event.modifiedFoodValues.saturationModifier;
// Config.setHungerExpressionValues(
// ((IHungerManager) event.player.getHungerManager()).spiceOfFabric_getFoodHistory().getTimesEaten(stack),
// hungerValue, saturationValue, event.itemStack.getMaxUseTime()
// );
// event.modifiedFoodValues = new FoodValues(Config.getHungerValue(), Config.getSaturationValue());
// }
// });
// }
// }
