package com.u2hc.mixin;

import com.u2hc.U2HCMod;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mob.class)
public abstract class MobMixin {

    @Inject(method = "<init>", at = @At("RETURN"))
    private void rule12_13_Multipliers(EntityType<? extends Mob> type, net.minecraft.world.level.Level level, CallbackInfo ci) {
        Mob mob = (Mob)(Object)this;
        if (U2HCMod.ACTIVE_CONFIG == null) return;

        var hp = mob.getAttribute(Attributes.MAX_HEALTH);
        if (U2HCMod.ACTIVE_CONFIG.doubleMobHealth && hp != null) {
            double val = hp.getBaseValue() * 2.5;
            hp.setBaseValue(val);
            mob.setHealth((float) val);
        }

        var dmg = mob.getAttribute(Attributes.ATTACK_DAMAGE);
        if (U2HCMod.ACTIVE_CONFIG.increasedMobDamage && dmg != null) {
            dmg.setBaseValue(dmg.getBaseValue() * 1.5);
        }
    }

    @Redirect(
            method = "populateDefaultEquipmentSlots",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/DifficultyInstance;getSpecialMultiplier()F")
    )
    private float rule17_forceMaxDifficulty(DifficultyInstance instance) {
        if (U2HCMod.ACTIVE_CONFIG != null && U2HCMod.ACTIVE_CONFIG.highArmorChance) {
            return 1.0F;
        }
        return instance.getSpecialMultiplier();
    }

    @Redirect(
            method = "populateDefaultEquipmentSlots",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/util/RandomSource;nextFloat()F", ordinal = 0)
    )
    private float rule17_ninetyPercentArmor(RandomSource random) {
        float roll = random.nextFloat();
        if (U2HCMod.ACTIVE_CONFIG != null && U2HCMod.ACTIVE_CONFIG.highArmorChance) {

            return (roll < 0.90F) ? 0.01F : 1.0F;
        }
        return roll;
    }

}