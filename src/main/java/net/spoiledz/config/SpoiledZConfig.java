package net.spoiledz.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

@Config(name = "spoiledz")
@Config.Gui.Background("minecraft:textures/block/stone.png")
public class SpoiledZConfig implements ConfigData {

    @Comment("On spoiled craft, reset spoiling time")
    public boolean freshCrafting = false;
    @ConfigEntry.BoundedDiscrete(min = 0, max = 100)
    public int effectChance = 35;
    @Comment("In ticks")
    public int effectDuration = 400;

}