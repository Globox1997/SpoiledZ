package net.spoiledz.init;

import com.mojang.serialization.MapCodec;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.numeric.RangeSelectItemModelProperty;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.item.ItemStack;
import net.spoiledz.SpoiledZMain;
import net.spoiledz.util.SpoiledUtil;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;

public class ModelInit {

    public static final Identifier SPOILED_PROPERTY_ID = SpoiledZMain.identifierOf("spoiled");

    public static void registerRangeSelectProperties(BiConsumer<Identifier, MapCodec<? extends RangeSelectItemModelProperty>> registrar) {
        registrar.accept(SPOILED_PROPERTY_ID, SpoiledProperty.MAP_CODEC);
    }

    public record SpoiledProperty() implements RangeSelectItemModelProperty {

        public static final MapCodec<SpoiledProperty> MAP_CODEC = MapCodec.unit(new SpoiledProperty());

        @Override
        public float get(ItemStack stack, @Nullable ClientLevel world, @Nullable ItemOwner owner, int seed) {
            if (world != null) {
                return (SpoiledUtil.getSpoilingTime(world, stack) / 4f);
            } else if (owner != null) {
                return (SpoiledUtil.getSpoilingTime(owner.level(), stack) / 4f);
            } else {
                return 0f;
            }
        }

        @Override
        public MapCodec<SpoiledProperty> type() {
            return MAP_CODEC;
        }
    }
}
