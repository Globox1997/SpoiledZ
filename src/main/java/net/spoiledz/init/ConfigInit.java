package net.spoiledz.init;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import net.spoiledz.config.SpoiledZConfig;

public class ConfigInit {

    public static SpoiledZConfig CONFIG = new SpoiledZConfig();

    public static void init() {
        AutoConfig.register(SpoiledZConfig.class, JanksonConfigSerializer::new);
        CONFIG = AutoConfig.getConfigHolder(SpoiledZConfig.class).getConfig();
    }
}
