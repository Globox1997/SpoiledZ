package net.spoiledz.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import io.netty.buffer.ByteBuf;

import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

public record SpoiledComponent(String season, int year) {

    public static final Codec<SpoiledComponent> CODEC = RecordCodecBuilder
            .create(instance -> instance.group(Codec.STRING.fieldOf("season").forGetter(SpoiledComponent::season), Codec.INT.fieldOf("year").forGetter(SpoiledComponent::year))
                    .apply(instance, SpoiledComponent::new));

    public static final PacketCodec<ByteBuf, SpoiledComponent> PACKET_CODEC = PacketCodec.tuple(PacketCodecs.STRING, SpoiledComponent::season, PacketCodecs.INTEGER, SpoiledComponent::year,
            SpoiledComponent::new);

}

