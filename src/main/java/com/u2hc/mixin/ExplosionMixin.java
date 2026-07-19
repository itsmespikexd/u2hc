package com.u2hc.mixin;

import com.u2hc.U2HCState;
import net.minecraft.world.level.Explosion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Explosion.class)
public class ExplosionMixin {
    @Inject(method = "finalizeExplosion(Z)V", at = @At("HEAD"))
    private void startExplosionContext(boolean spawnParticles, CallbackInfo ci) {
        U2HCState.IS_EXPLODING.set(true);
    }

    @Inject(method = "finalizeExplosion(Z)V", at = @At("RETURN"))
    private void stopExplosionContext(boolean spawnParticles, CallbackInfo ci) {
        U2HCState.IS_EXPLODING.set(false);
    }
}