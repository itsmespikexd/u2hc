package com.u2hc.mixin;

import com.u2hc.U2HCMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Minecraft.class)
public class ClientInputMixin {
    @Shadow public Screen screen;

    @Inject(method = "startAttack", at = @At("HEAD"), cancellable = true)
    private void rule2_stopAttackHead(CallbackInfoReturnable<Boolean> cir) {
        if (U2HCMod.ACTIVE_CONFIG != null && U2HCMod.ACTIVE_CONFIG.noLeftClick && this.screen == null) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "continueAttack", at = @At("HEAD"), cancellable = true)
    private void rule2_stopMiningHead(boolean leftClick, CallbackInfo ci) {
        if (U2HCMod.ACTIVE_CONFIG != null && U2HCMod.ACTIVE_CONFIG.noLeftClick && this.screen == null) {
            ci.cancel();
        }
    }
}