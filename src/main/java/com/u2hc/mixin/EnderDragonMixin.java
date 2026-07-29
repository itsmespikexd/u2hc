package com.u2hc.mixin;

import com.u2hc.U2HCMod;
import com.u2hc.U2HCState;
import com.u2hc.U2HCPayload;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EnderDragon.class)
public class EnderDragonMixin {

    @Inject(method = "tickDeath", at = @At("HEAD"))
    private void win(CallbackInfo ci) {
        EnderDragon dragon = (EnderDragon)(Object)this;

        if (dragon.level() instanceof ServerLevel serverLevel && !U2HCMod.isComplete) {
            U2HCMod.isComplete = true;
            U2HCState.frozenTicks = serverLevel.getGameTime();
            U2HCState.frozenDeaths = U2HCState.deathCount;
            U2HCState.fireworkTimer = 300;

            for (ServerPlayer player : serverLevel.players()) {
                ServerPlayNetworking.send(player, new U2HCPayload(U2HCState.frozenTicks, U2HCState.frozenDeaths));
            }

            U2HCMod.exportLog(serverLevel, "WIN LMAO");
        }
    }
}