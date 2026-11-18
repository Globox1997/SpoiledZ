package net.spoiledz;

import java.util.List;

import net.minecraft.resources.Identifier;
import net.spoiledz.init.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SpoiledZMain {

    public static final String MOD_ID = "spoiledz";

    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static final List<String> FABRIC_SEASONS = List.of("spring", "summer", "fall", "winter");
    public static final List<String> SERENE_SEASONS = List.of("SPRING", "SUMMER", "AUTUMN", "WINTER");

    public static void init() {
        ConfigInit.init();
        TagInit.init();
    }

    public static Identifier identifierOf(String name) {
        return Identifier.fromNamespaceAndPath(MOD_ID, name);
    }

}
