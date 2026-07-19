package com.u2hc.mixin;

import com.u2hc.U2HCMod;
import com.u2hc.config.U2HCConfig;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CreateWorldScreen.class)
public abstract class CreateWorldScreenMixin extends Screen {
    protected CreateWorldScreenMixin(Component title) { super(title); }

    @Inject(method = "init", at = @At("TAIL"))
    private void addU2HCButton(CallbackInfo ci) {
        if (U2HCMod.PENDING_CONFIG == null) {
            U2HCMod.PENDING_CONFIG = AutoConfig.getConfigHolder(U2HCConfig.class).getConfig().copy();
        }

        int buttonWidth = 150;
        int buttonHeight = 20;

        int centerX = (this.width / 2) - (buttonWidth / 2);
        int centerY = 185;

        this.addRenderableWidget(Button.builder(Component.literal("U2HC World Config"), (button) -> {
            this.minecraft.setScreen(createIsolatedConfigScreen(this));
        }).bounds(centerX, centerY, buttonWidth, buttonHeight).build());
    }

    private Screen createIsolatedConfigScreen(Screen parent) {
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Component.literal("U2HC World Config"));

        ConfigCategory general = builder.getOrCreateCategory(Component.literal("Rules"));
        ConfigEntryBuilder eb = builder.entryBuilder();
        U2HCConfig cfg = U2HCMod.PENDING_CONFIG;

        general.addEntry(eb.startBooleanToggle(Component.literal("No Sprint"), cfg.noSprint).setSaveConsumer(v -> cfg.noSprint = v).build());
        general.addEntry(eb.startBooleanToggle(Component.literal("No Left Click"), cfg.noLeftClick).setSaveConsumer(v -> cfg.noLeftClick = v).build());
        general.addEntry(eb.startBooleanToggle(Component.literal("No Jump"), cfg.noJump).setSaveConsumer(v -> cfg.noJump = v).build());
        general.addEntry(eb.startBooleanToggle(Component.literal("No Shift"), cfg.noShift).setSaveConsumer(v -> cfg.noShift = v).build());
        general.addEntry(eb.startBooleanToggle(Component.literal("No F3"), cfg.noF3).setSaveConsumer(v -> cfg.noF3 = v).build());
        general.addEntry(eb.startBooleanToggle(Component.literal("No Natural Regen"), cfg.noNaturalRegen).setSaveConsumer(v -> cfg.noNaturalRegen = v).build());
        general.addEntry(eb.startBooleanToggle(Component.literal("Food Effectiveness Halved"), cfg.foodHalved).setSaveConsumer(v -> cfg.foodHalved = v).build());
        general.addEntry(eb.startBooleanToggle(Component.literal("Negative Effects at Night"), cfg.negativeEffectsAtNight).setSaveConsumer(v -> cfg.negativeEffectsAtNight = v).build());
        general.addEntry(eb.startBooleanToggle(Component.literal("6x Mob Cap"), cfg.increasedSpawnRate).setSaveConsumer(v -> cfg.increasedSpawnRate = v).build());
        general.addEntry(eb.startBooleanToggle(Component.literal("1.5x Mob Damage"), cfg.increasedMobDamage).setSaveConsumer(v -> cfg.increasedMobDamage = v).build());
        general.addEntry(eb.startBooleanToggle(Component.literal("2.5x Mob Health"), cfg.doubleMobHealth).setSaveConsumer(v -> cfg.doubleMobHealth = v).build());
        general.addEntry(eb.startBooleanToggle(Component.literal("Hostile Enderman"), cfg.endermanAlwaysHostile).setSaveConsumer(v -> cfg.endermanAlwaysHostile = v).build());
        general.addEntry(eb.startBooleanToggle(Component.literal("Hostile Wolf"), cfg.hostileWolves).setSaveConsumer(v -> cfg.hostileWolves = v).build());
        general.addEntry(eb.startBooleanToggle(Component.literal("Increased Mob Armor Rate"), cfg.highArmorChance).setSaveConsumer(v -> cfg.highArmorChance = v).build());
        general.addEntry(eb.startBooleanToggle(Component.literal("Increased Weather Intensity"), cfg.increasedWeatherIntensity).setSaveConsumer(v -> cfg.increasedWeatherIntensity = v).build());
        general.addEntry(eb.startBooleanToggle(Component.literal("Utility Block Crafting Disabled"), cfg.blockUtilityCrafting).setSaveConsumer(v -> cfg.blockUtilityCrafting = v).build());
        general.addEntry(eb.startBooleanToggle(Component.literal("Global 1/3 Explosion Drops"), cfg.globalExplosionDropRate).setSaveConsumer(v -> cfg.globalExplosionDropRate = v).build());
        general.addEntry(eb.startBooleanToggle(Component.literal("No Villager Trading"), cfg.noVillagerTrading).setSaveConsumer(v -> cfg.noVillagerTrading = v).build());
        general.addEntry(eb.startBooleanToggle(Component.literal("No Armor"), cfg.noArmor).setSaveConsumer(v -> cfg.noArmor = v).build());
        general.addEntry(eb.startBooleanToggle(Component.literal("Release 1.0 Items Only"), cfg.versionLock10).setSaveConsumer(v -> cfg.versionLock10 = v).build());
        return builder.build();
    }
}