package net.spoiledz;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.spoiledz.init.ModelInit;

@Environment(EnvType.CLIENT)
public class SpoiledZClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ModelInit.init();
    }

}
