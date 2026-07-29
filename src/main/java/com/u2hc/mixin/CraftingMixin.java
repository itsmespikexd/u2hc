package com.u2hc.mixin;

import com.u2hc.U2HCMod;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ResultContainer.class)
public class CraftingMixin {

    @ModifyVariable(method = "setItem", at = @At("HEAD"), argsOnly = true)
    private ItemStack rule20_stopUtilityCrafting(ItemStack stack) {
        if (U2HCMod.ACTIVE_CONFIG != null && U2HCMod.ACTIVE_CONFIG.blockUtilityCrafting) {
            if (stack.is(Items.FURNACE) ||
                stack.is(Items.ENCHANTING_TABLE) ||
                stack.is(Items.BREWING_STAND)) {
                return ItemStack.EMPTY;
            }
        }
        return stack;
    }
}