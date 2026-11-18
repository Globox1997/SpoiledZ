package net.spoiledz.mixin.access;

import it.unimi.dsi.fastutil.objects.Reference2ObjectMap;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(DataComponentPatch.class)
public interface DataComponentPatchAccessor {

    @Accessor("map")
    Reference2ObjectMap<DataComponentType<?>, Object> getMap();

}
