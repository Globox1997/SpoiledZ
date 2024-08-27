package net.spoiledz.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import io.netty.buffer.ByteBuf;

import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

public record BlockSpoiledComponent(String season, int year, int xPos, int yPos, int zPos) {

    public static final Codec<BlockSpoiledComponent> CODEC = RecordCodecBuilder
            .create(instance -> instance.group(Codec.STRING.fieldOf("season").forGetter(BlockSpoiledComponent::season), Codec.INT.fieldOf("year").forGetter(BlockSpoiledComponent::year), Codec.INT.fieldOf("x_pos").forGetter(BlockSpoiledComponent::xPos), Codec.INT.fieldOf("y_pos").forGetter(BlockSpoiledComponent::yPos), Codec.INT.fieldOf("z_pos").forGetter(BlockSpoiledComponent::zPos))
                    .apply(instance, BlockSpoiledComponent::new));

    public static final PacketCodec<ByteBuf, BlockSpoiledComponent> PACKET_CODEC = PacketCodec.tuple(PacketCodecs.STRING, BlockSpoiledComponent::season, PacketCodecs.INTEGER, BlockSpoiledComponent::year, PacketCodecs.INTEGER, BlockSpoiledComponent::xPos, PacketCodecs.INTEGER, BlockSpoiledComponent::yPos, PacketCodecs.INTEGER, BlockSpoiledComponent::zPos,
            BlockSpoiledComponent::new);

}


