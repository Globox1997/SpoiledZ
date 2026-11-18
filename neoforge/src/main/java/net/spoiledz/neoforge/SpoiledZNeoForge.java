package net.spoiledz.neoforge;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AddPackFindersEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.neoforged.neoforge.registries.RegisterEvent;
import net.spoiledz.SpoiledZMain;
import net.spoiledz.init.CompatInit;
import net.spoiledz.init.ComponentInit;
import net.spoiledz.init.EventInit;

@Mod(SpoiledZMain.MOD_ID)
public class SpoiledZNeoForge {

    public SpoiledZNeoForge(IEventBus modBus) {
        SpoiledZMain.init();
        modBus.addListener(RegisterEvent.class, event -> event.register(Registries.DATA_COMPONENT_TYPE, helper -> ComponentInit.register(helper::register, false)));
        modBus.addListener(AddPackFindersEvent.class, SpoiledZNeoForge::addPackFinders);
        NeoForge.EVENT_BUS.addListener(LevelTickEvent.Post.class, event -> {
            if (event.getLevel() instanceof ServerLevel serverLevel) {
                EventInit.onServerLevelTick(serverLevel);
            }
        });
    }

    private static void addPackFinders(AddPackFindersEvent event) {
        for (CompatInit.CompatPack compatPack : CompatInit.getActiveCompatPacks()) {
            event.addPackFinders(Identifier.fromNamespaceAndPath(SpoiledZMain.MOD_ID, "resourcepacks/" + compatPack.id().getPath()), event.getPackType(),
                    Component.translatable("pack." + SpoiledZMain.MOD_ID + "." + compatPack.id().getPath()), PackSource.BUILT_IN, true, Pack.Position.TOP);
        }
    }
}
