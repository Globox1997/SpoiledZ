package net.spoiledz.mixin;

import java.util.List;
import java.util.Set;

import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import net.fabricmc.loader.api.FabricLoader;

public class SpoiledzMixinPlugin implements IMixinConfigPlugin {

    @Override
    public void onLoad(String mixinPackage) {
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        if ((mixinClassName.contains("SkilletItemMixin") || mixinClassName.contains("SkilletBlockEntityMixin") || mixinClassName.contains("CuttingBoardBlockEntityMixin")
                || mixinClassName.contains("StoveBlockEntityMixin") || mixinClassName.contains("CookingPotBlockEntityMixin")) && !FabricLoader.getInstance().isModLoaded("farmersdelight")) {
            return false;
        }
        if (mixinClassName.contains("JuicerBlockEntityMixin") && !FabricLoader.getInstance().isModLoaded("expandeddelight")) {
            return false;
        }
        if ((mixinClassName.contains("CookingPotEntityMixin") || mixinClassName.contains("ApplePressBlockEntityMixin") || mixinClassName.contains("WoodFiredOvenBlockEntityMixin"))
                && !FabricLoader.getInstance().isModLoaded("vinery")) {
            return false;
        }

        return true;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }

}