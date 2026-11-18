package net.spoiledz.neoforge.client;

import me.shedaniel.autoconfig.AutoConfigClient;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterRangeSelectItemModelPropertyEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.spoiledz.SpoiledZMain;
import net.spoiledz.config.SpoiledZConfig;
import net.spoiledz.init.ModelInit;
import net.spoiledz.neoforge.compat.AppleSkinNeoForgeCompat;
import net.spoiledz.platform.Services;

@Mod(value = SpoiledZMain.MOD_ID, dist = Dist.CLIENT)
public class SpoiledZNeoForgeClient {

    public SpoiledZNeoForgeClient(IEventBus modBus, ModContainer container) {
        modBus.addListener(RegisterRangeSelectItemModelPropertyEvent.class, event -> ModelInit.registerRangeSelectProperties(event::register));
        container.registerExtensionPoint(IConfigScreenFactory.class, (modContainer, parent) -> AutoConfigClient.getConfigScreen(SpoiledZConfig.class, parent).get());
        if (Services.PLATFORM.isModLoaded("appleskin")) {
            AppleSkinNeoForgeCompat.register();
        }
    }
}
