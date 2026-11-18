package net.spoiledz.neoforge.platform;

import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLLoader;
import net.spoiledz.platform.services.IPlatformHelper;

public class NeoForgePlatformHelper implements IPlatformHelper {

    @Override
    public boolean isModLoaded(String modId) {
        ModList modList = ModList.get();
        if (modList != null) {
            return modList.isLoaded(modId);
        }
        return FMLLoader.getCurrent().getLoadingModList().getModFileById(modId) != null;
    }
}
