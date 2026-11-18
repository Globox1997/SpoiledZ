package net.spoiledz.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record SpoiledComponent(String season, int year) {

    public static final Codec<SpoiledComponent> CODEC = RecordCodecBuilder
            .create(instance -> instance.group(Codec.STRING.fieldOf("season").forGetter(SpoiledComponent::season), Codec.INT.fieldOf("year").forGetter(SpoiledComponent::year))
                    .apply(instance, SpoiledComponent::new));

    public static final StreamCodec<ByteBuf, SpoiledComponent> PACKET_CODEC = StreamCodec.composite(ByteBufCodecs.STRING_UTF8, SpoiledComponent::season, ByteBufCodecs.INT, SpoiledComponent::year,
            SpoiledComponent::new);

}

