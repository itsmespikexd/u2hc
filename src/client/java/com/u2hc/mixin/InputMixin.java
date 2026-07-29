package com.u2hc.mixin;

import com.u2hc.U2HCMod;
import net.minecraft.client.player.Input;
import net.minecraft.client.player.KeyboardInput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyboardInput.class)
public class InputMixin {
    @Inject(method = "tick", at = @At("TAIL"))
    private void hardBlockInput(boolean slowDown, float slowDownFactor, CallbackInfo ci) {
        Input input = (Input) (Object) this;
        if (U2HCMod.ACTIVE_CONFIG == null) return;

        if (U2HCMod.ACTIVE_CONFIG.noJump) input.jumping = false;
        if (U2HCMod.ACTIVE_CONFIG.noShift) input.shiftKeyDown = false;
    }
}