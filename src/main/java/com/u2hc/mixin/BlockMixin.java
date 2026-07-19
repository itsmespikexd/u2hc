package com.u2hc.mixin;

import com.u2hc.U2HCMod;
import com.u2hc.U2HCState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Block.class)
public class BlockMixin {

    @Inject(method = "popResource(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/item/ItemStack;)V",
            at = @At("HEAD"),
            cancellable = true)
    private static void rule22_AtomicThirdRate(Level level, BlockPos pos, ItemStack stack, CallbackInfo ci) {

        if (U2HCMod.ACTIVE_CONFIG != null && U2HCMod.ACTIVE_CONFIG.globalExplosionDropRate && U2HCState.IS_EXPLODING.get()) {

            int originalCount = stack.getCount();
            int remainingCount = 0;

            for (int i = 0; i < originalCount; i++) {
                if (level.random.nextInt(3) == 0) {
                    remainingCount++;
                }
            }

            if (remainingCount <= 0) {
                ci.cancel();
            } else {
                stack.setCount(remainingCount);
            }
        }
    }
}