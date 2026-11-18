package net.spoiledz.compat;

import io.github.lucaargolo.seasons.FabricSeasons;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.world.level.Level;

public class FabricSeasonsCompat {

    public static final boolean isFabricSeasonsLoaded = FabricLoader.getInstance().isModLoaded("seasons");

    public static String getCurrentSeason(Level world) {
        return FabricSeasons.getCurrentSeason(world).getSerializedName();
    }

    public static long getSeasonLength(Level world) {
        return FabricSeasons.getCurrentSeason(world).getSeasonLength();
    }

}
