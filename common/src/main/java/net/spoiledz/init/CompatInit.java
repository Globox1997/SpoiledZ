package net.spoiledz.init;

import java.util.List;

import net.minecraft.resources.Identifier;
import net.spoiledz.SpoiledZMain;
import net.spoiledz.platform.Services;

public class CompatInit {

    public record CompatPack(String modId, Identifier id) {
    }

    private static final List<CompatPack> COMPAT_PACKS = List.of(new CompatPack("adventurez", SpoiledZMain.identifierOf("adventurez_spoiledz_compat")),
            new CompatPack("farmz", SpoiledZMain.identifierOf("farmz_spoiledz_compat")), new CompatPack("oblivion", SpoiledZMain.identifierOf("oblivion_spoiledz_compat")));

    public static List<CompatPack> getActiveCompatPacks() {
        return COMPAT_PACKS.stream().filter(pack -> Services.PLATFORM.isModLoaded(pack.modId())).toList();
    }
}
