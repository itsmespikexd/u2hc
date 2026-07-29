package com.u2hc.mixin;

import com.u2hc.U2HCMod;
import com.u2hc.U2HCState;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
public class PlayerMixin {

    @Inject(method = "tick", at = @At("TAIL"))
    private void fireworkTicker(CallbackInfo ci) {
        ServerPlayer p = (ServerPlayer)(Object)this;
        if (!p.level().isClientSide && U2HCState.fireworkTimer > 0) {
            if (U2HCState.fireworkTimer % 2 == 0) {
                for (int i = 0; i < 3; i++) {
                    double offsetX = (p.getRandom().nextFloat() * 10.0) - 5.0;
                    double offsetZ = (p.getRandom().nextFloat() * 10.0) - 5.0;
                    double offsetY = p.getRandom().nextFloat() * 2.0;

                    FireworkRocketEntity f = new FireworkRocketEntity(
                            p.level(),
                            p.getX() + offsetX,
                            p.getY() + offsetY,
                            p.getZ() + offsetZ,
                            Items.FIREWORK_ROCKET.getDefaultInstance()
                    );
                    p.level().addFreshEntity(f);
                }

                p.level().playSound(null, p.getX(), p.getY(), p.getZ(),
                        net.minecraft.sounds.SoundEvents.FIREWORK_ROCKET_LAUNCH,
                        net.minecraft.sounds.SoundSource.AMBIENT, 3.0F, 1.0F);
            }
            if (p == p.server.getPlayerList().getPlayers().get(0)) {
                U2HCState.fireworkTimer--;
            }
        }
    }
}