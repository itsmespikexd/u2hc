package com.u2hc.mixin;

import com.u2hc.U2HCMod;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({CraftingMenu.class, InventoryMenu.class})
public abstract class CraftingMixin {

    @Inject(method = "slotsChanged", at = @At("TAIL"))
    private void rule20_blockUtilityCrafting(Container container, CallbackInfo ci) {
        AbstractContainerMenu menu = (AbstractContainerMenu) (Object) this;

        if (U2HCMod.ACTIVE_CONFIG != null && U2HCMod.ACTIVE_CONFIG.blockUtilityCrafting) {
            menu.slots.stream()
                    .filter(slot -> slot.container instanceof ResultContainer)
                    .findFirst()
                    .ifPresent(slot -> {
                        ItemStack result = slot.getItem();
                        if (!result.isEmpty()) {
                            if (result.is(Items.CRAFTING_TABLE) ||
                                    result.is(Items.FURNACE) ||
                                    result.is(Items.ENCHANTING_TABLE) ||
                                    result.is(Items.BREWING_STAND)) {

                                slot.set(ItemStack.EMPTY);
                            }
                        }
                    });
        }
    }
}