package net.spoiledz.init;

import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.spoiledz.SpoiledZMain;

public class CompatInit {

    public static void init(){
        if (FabricLoader.getInstance().isModLoaded("adventurez")) {
            ResourceManagerHelper.registerBuiltinResourcePack(SpoiledZMain.identifierOf("adventurez_spoiledz_compat"), FabricLoader.getInstance().getModContainer("spoiledz").orElseThrow(),
                    ResourcePackActivationType.DEFAULT_ENABLED);
        }
        if (FabricLoader.getInstance().isModLoaded("farmz")) {
            ResourceManagerHelper.registerBuiltinResourcePack(SpoiledZMain.identifierOf("farmz_spoiledz_compat"), FabricLoader.getInstance().getModContainer("spoiledz").orElseThrow(),
                    ResourcePackActivationType.DEFAULT_ENABLED);
        }
        if (FabricLoader.getInstance().isModLoaded("oblivion")) {
            ResourceManagerHelper.registerBuiltinResourcePack(SpoiledZMain.identifierOf("oblivion_spoiledz_compat"), FabricLoader.getInstance().getModContainer("spoiledz").orElseThrow(),
                    ResourcePackActivationType.DEFAULT_ENABLED);
        }
    }
}
