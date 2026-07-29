package com.u2hc.mixin;

import com.u2hc.U2HCMod;
import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(ServerLevel.class)
public class WeatherMixin {
    @ModifyConstant(method = "tickChunk", constant = @Constant(intValue = 100000))
    private int lightning(int constant) {
        if (U2HCMod.ACTIVE_CONFIG != null && U2HCMod.ACTIVE_CONFIG.increasedWeatherIntensity) {
            return 10000;
        }
        return constant;
    }
}