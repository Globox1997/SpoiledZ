package net.spoiledz.init;

import com.mojang.serialization.MapCodec;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.numeric.RangeSelectItemModelProperties;
import net.minecraft.client.renderer.item.properties.numeric.RangeSelectItemModelProperty;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.item.ItemStack;
import net.spoiledz.SpoiledZMain;
import net.spoiledz.util.SpoiledUtil;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class ModelInit {

    public static void init() {
        RangeSelectItemModelProperties.ID_MAPPER.put(SpoiledZMain.identifierOf("spoiled"), SpoiledProperty.MAP_CODEC);
    }

    @Environment(EnvType.CLIENT)
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
