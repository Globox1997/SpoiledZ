package net.spoiledz.init;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.resources.Identifier;
import net.spoiledz.SpoiledZMain;
import net.spoiledz.util.SpoiledComponent;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;

public class ComponentInit {

    public static DataComponentType<SpoiledComponent> SPOILED;
    public static DataComponentType<SpoiledComponent> POTTED;

    @Nullable
    public static DataComponentType<SpoiledComponent> LEGACY_SPOILED;
    @Nullable
    public static DataComponentType<SpoiledComponent> LEGACY_POTTED;

    public static void register(BiConsumer<Identifier, DataComponentType<?>> registrar, boolean registerLegacyComponents) {
        SPOILED = createComponent();
        registrar.accept(SpoiledZMain.identifierOf("spoiled"), SPOILED);
        POTTED = createComponent();
        registrar.accept(SpoiledZMain.identifierOf("potted"), POTTED);
        if (registerLegacyComponents) {
            LEGACY_SPOILED = createComponent();
            registrar.accept(Identifier.withDefaultNamespace("spoiled"), LEGACY_SPOILED);
            LEGACY_POTTED = createComponent();
            registrar.accept(Identifier.withDefaultNamespace("potted"), LEGACY_POTTED);
        }
    }

    private static DataComponentType<SpoiledComponent> createComponent() {
        return DataComponentType.<SpoiledComponent>builder().persistent(SpoiledComponent.CODEC).networkSynchronized(SpoiledComponent.PACKET_CODEC).build();
    }
}
