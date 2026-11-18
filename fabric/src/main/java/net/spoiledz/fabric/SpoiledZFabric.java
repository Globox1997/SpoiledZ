package net.spoiledz.fabric;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.spoiledz.SpoiledZMain;
import net.spoiledz.init.CompatInit;
import net.spoiledz.init.ComponentInit;
import net.spoiledz.init.EventInit;

public class SpoiledZFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        SpoiledZMain.init();
        ComponentInit.register((id, type) -> Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, id, type), true);
        ServerTickEvents.END_LEVEL_TICK.register(EventInit::onServerLevelTick);
        for (CompatInit.CompatPack compatPack : CompatInit.getActiveCompatPacks()) {
            ResourceManagerHelper.registerBuiltinResourcePack(compatPack.id(), FabricLoader.getInstance().getModContainer(SpoiledZMain.MOD_ID).orElseThrow(),
                    ResourcePackActivationType.DEFAULT_ENABLED);
        }
    }

}
