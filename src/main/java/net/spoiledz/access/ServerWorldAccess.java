package net.spoiledz.access;

import net.spoiledz.util.SpoiledUtil.FoodBlockState;
import org.jetbrains.annotations.Nullable;

public interface ServerWorldAccess {

    public FoodBlockState getFoodBlockState();

    public void setCurrentSeason(@Nullable String currentSeason);

    @Nullable
    public String getCurrentSeason();

}
