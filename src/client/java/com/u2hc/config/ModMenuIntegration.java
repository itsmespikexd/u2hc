package com.u2hc.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class ModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<Screen> getModConfigScreenFactory() {
        return parent -> {
            U2HCConfig cfg = AutoConfig.getConfigHolder(U2HCConfig.class).getConfig();

            ConfigBuilder builder = ConfigBuilder.create()
                    .setParentScreen(parent)
                    .setTitle(Component.literal("U2HC Global Defaults"))
                    .setSavingRunnable(() -> AutoConfig.getConfigHolder(U2HCConfig.class).save());

            ConfigCategory cat = builder.getOrCreateCategory(Component.literal("Default Rules"));
            ConfigEntryBuilder eb = builder.entryBuilder();

            cat.addEntry(eb.startBooleanToggle(Component.literal("No Sprint"), cfg.noSprint).setSaveConsumer(v -> cfg.noSprint = v).setDefaultValue(true).build());
            cat.addEntry(eb.startBooleanToggle(Component.literal("No Left Click"), cfg.noLeftClick).setSaveConsumer(v -> cfg.noLeftClick = v).setDefaultValue(true).build());
            cat.addEntry(eb.startBooleanToggle(Component.literal("No Jump"), cfg.noJump).setSaveConsumer(v -> cfg.noJump = v).setDefaultValue(true).build());
            cat.addEntry(eb.startBooleanToggle(Component.literal("No Shift"), cfg.noShift).setSaveConsumer(v -> cfg.noShift = v).setDefaultValue(true).build());
            cat.addEntry(eb.startBooleanToggle(Component.literal("No F3"), cfg.noF3).setSaveConsumer(v -> cfg.noF3 = v).setDefaultValue(true).build());
            cat.addEntry(eb.startBooleanToggle(Component.literal("No Natural Regen"), cfg.noNaturalRegen).setSaveConsumer(v -> cfg.noNaturalRegen = v).setDefaultValue(true).build());
            cat.addEntry(eb.startBooleanToggle(Component.literal("Food Effectiveness Halved"), cfg.foodHalved).setSaveConsumer(v -> cfg.foodHalved = v).setDefaultValue(true).build());
            cat.addEntry(eb.startBooleanToggle(Component.literal("Negative Effects at Night"), cfg.negativeEffectsAtNight).setSaveConsumer(v -> cfg.negativeEffectsAtNight = v).setDefaultValue(true).build());
            cat.addEntry(eb.startBooleanToggle(Component.literal("6x Mob Cap"), cfg.increasedSpawnRate).setSaveConsumer(v -> cfg.increasedSpawnRate = v).setDefaultValue(true).build());
            cat.addEntry(eb.startBooleanToggle(Component.literal("1.5x Mob Damage"), cfg.increasedMobDamage).setSaveConsumer(v -> cfg.increasedMobDamage = v).setDefaultValue(true).build());
            cat.addEntry(eb.startBooleanToggle(Component.literal("2.5x Mob Health"), cfg.doubleMobHealth).setSaveConsumer(v -> cfg.doubleMobHealth = v).setDefaultValue(true).build());
            cat.addEntry(eb.startBooleanToggle(Component.literal("Hostile Enderman"), cfg.endermanAlwaysHostile).setSaveConsumer(v -> cfg.endermanAlwaysHostile = v).setDefaultValue(true).build());
            cat.addEntry(eb.startBooleanToggle(Component.literal("Hostile Wolf"), cfg.hostileWolves).setSaveConsumer(v -> cfg.hostileWolves = v).setDefaultValue(true).build());
            cat.addEntry(eb.startBooleanToggle(Component.literal("Increased Mob Armor Rate"), cfg.highArmorChance).setSaveConsumer(v -> cfg.highArmorChance = v).setDefaultValue(true).build());
            cat.addEntry(eb.startBooleanToggle(Component.literal("Increased Weather Intensity"), cfg.increasedWeatherIntensity).setSaveConsumer(v -> cfg.increasedWeatherIntensity = v).setDefaultValue(true).build());
            cat.addEntry(eb.startBooleanToggle(Component.literal("Utility Block Crafting Disabled"), cfg.blockUtilityCrafting).setSaveConsumer(v -> cfg.blockUtilityCrafting = v).setDefaultValue(true).build());
            cat.addEntry(eb.startBooleanToggle(Component.literal("Global 1/3 Explosion Drops"), cfg.globalExplosionDropRate).setSaveConsumer(v -> cfg.globalExplosionDropRate = v).setDefaultValue(true).build());
            cat.addEntry(eb.startBooleanToggle(Component.literal("No Villager Trading"), cfg.noVillagerTrading).setSaveConsumer(v -> cfg.noVillagerTrading = v).setDefaultValue(true).build());
            cat.addEntry(eb.startBooleanToggle(Component.literal("No Armor"), cfg.noArmor).setSaveConsumer(v -> cfg.noArmor = v).setDefaultValue(true).build());
            cat.addEntry(eb.startBooleanToggle(Component.literal("Release 1.0 Items Only"), cfg.versionLock10).setSaveConsumer(v -> cfg.versionLock10 = v).setDefaultValue(true).build());

            return builder.build();
        };
    }
}