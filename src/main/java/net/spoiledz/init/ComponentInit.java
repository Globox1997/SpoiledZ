package net.spoiledz.init;

import net.minecraft.component.ComponentType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.spoiledz.SpoiledZMain;
import net.spoiledz.util.SpoiledComponent;

import java.util.function.UnaryOperator;

public class ComponentInit {

    public static final ComponentType<SpoiledComponent> SPOILED = registerComponent(SpoiledZMain.identifierOf("spoiled"), builder -> builder.codec(SpoiledComponent.CODEC).packetCodec(SpoiledComponent.PACKET_CODEC));
    public static final ComponentType<SpoiledComponent> POTTED = registerComponent(SpoiledZMain.identifierOf("potted"), builder -> builder.codec(SpoiledComponent.CODEC).packetCodec(SpoiledComponent.PACKET_CODEC));

    public static final ComponentType<SpoiledComponent> LEGACY_SPOILED = registerComponent(Identifier.ofVanilla("spoiled"), builder -> builder.codec(SpoiledComponent.CODEC).packetCodec(SpoiledComponent.PACKET_CODEC));
    public static final ComponentType<SpoiledComponent> LEGACY_POTTED = registerComponent(Identifier.ofVanilla("potted"), builder -> builder.codec(SpoiledComponent.CODEC).packetCodec(SpoiledComponent.PACKET_CODEC));

    private static <T> ComponentType<T> registerComponent(Identifier id, UnaryOperator<ComponentType.Builder<T>> builderOperator) {
        return Registry.register(Registries.DATA_COMPONENT_TYPE, id, builderOperator.apply(ComponentType.builder()).build());
    }

    public static void init() {
    }
}
