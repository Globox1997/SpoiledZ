package net.spoiledz.init;

import net.minecraft.component.ComponentType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.spoiledz.util.BlockSpoiledComponent;
import net.spoiledz.util.SpoiledComponent;

import java.util.function.UnaryOperator;

public class ComponentInit {

    public static final ComponentType<SpoiledComponent> SPOILED = registerComponent("spoiled", builder -> builder.codec(SpoiledComponent.CODEC).packetCodec(SpoiledComponent.PACKET_CODEC));
    public static final ComponentType<SpoiledComponent> POTTED = registerComponent("potted", builder -> builder.codec(SpoiledComponent.CODEC).packetCodec(SpoiledComponent.PACKET_CODEC));
  // Unused
    public static final ComponentType<BlockSpoiledComponent> BLOCK_SPOILED = registerComponent("block_spoiled", builder -> builder.codec(BlockSpoiledComponent.CODEC).packetCodec(BlockSpoiledComponent.PACKET_CODEC));

    private static <T> ComponentType<T> registerComponent(String id, UnaryOperator<ComponentType.Builder<T>> builderOperator) {
        return Registry.register(Registries.DATA_COMPONENT_TYPE, id, builderOperator.apply(ComponentType.builder()).build());
    }

    public static void init() {
    }
}
