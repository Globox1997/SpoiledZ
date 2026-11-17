package net.spoiledz.init;

import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.spoiledz.SpoiledZMain;
import net.spoiledz.util.SpoiledComponent;

import java.util.function.UnaryOperator;

public class ComponentInit {

    public static final DataComponentType<SpoiledComponent> SPOILED = registerComponent(SpoiledZMain.identifierOf("spoiled"), builder -> builder.persistent(SpoiledComponent.CODEC).networkSynchronized(SpoiledComponent.PACKET_CODEC));
    public static final DataComponentType<SpoiledComponent> POTTED = registerComponent(SpoiledZMain.identifierOf("potted"), builder -> builder.persistent(SpoiledComponent.CODEC).networkSynchronized(SpoiledComponent.PACKET_CODEC));

    public static final DataComponentType<SpoiledComponent> LEGACY_SPOILED = registerComponent(Identifier.withDefaultNamespace("spoiled"), builder -> builder.persistent(SpoiledComponent.CODEC).networkSynchronized(SpoiledComponent.PACKET_CODEC));
    public static final DataComponentType<SpoiledComponent> LEGACY_POTTED = registerComponent(Identifier.withDefaultNamespace("potted"), builder -> builder.persistent(SpoiledComponent.CODEC).networkSynchronized(SpoiledComponent.PACKET_CODEC));

    private static <T> DataComponentType<T> registerComponent(Identifier id, UnaryOperator<DataComponentType.Builder<T>> builderOperator) {
        return Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, id, builderOperator.apply(DataComponentType.builder()).build());
    }

    public static void init() {
    }
}
