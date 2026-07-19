package com.u2hc.mixin;

import com.u2hc.U2HCMod;
import com.u2hc.U2HCState;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MobCategory.class)
public class SpawnMixin {
    @Inject(method = "getMaxInstancesPerChunk", at = @At("RETURN"), cancellable = true)
    private void rule7_tripleMobCap(CallbackInfoReturnable<Integer> cir) {
        if (U2HCMod.ACTIVE_CONFIG != null && U2HCMod.ACTIVE_CONFIG.increasedSpawnRate) {
            if ((Object)this == MobCategory.MONSTER) {
                if (U2HCState.currentDimension != Level.END) {
                    cir.setReturnValue(cir.getReturnValue() * 6);
                }
            }
        }
    }
}