package com.u2hc.mixin;

import com.u2hc.U2HCMod;
import net.minecraft.world.food.FoodData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(FoodData.class)
public class FoodMixin {

    @ModifyVariable(method = "add(IF)V", at = @At("HEAD"), argsOnly = true, ordinal = 0)
    private int rule11_halveNutritionParam(int nutrition) {
        if (U2HCMod.ACTIVE_CONFIG != null && U2HCMod.ACTIVE_CONFIG.foodHalved) {
            return nutrition / 2;
        }
        return nutrition;
    }

    @ModifyVariable(method = "add(IF)V", at = @At("HEAD"), argsOnly = true, ordinal = 0)
    private float rule11_halveSaturationParam(float saturation) {
        if (U2HCMod.ACTIVE_CONFIG != null && U2HCMod.ACTIVE_CONFIG.foodHalved) {
            return saturation / 2.0f;
        }
        return saturation;
    }
}