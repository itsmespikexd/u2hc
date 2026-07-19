package com.u2hc.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;

@Config(name = "u2hc")
public class U2HCConfig implements ConfigData {
    public boolean noSprint = true;
    public boolean noLeftClick = true;
    public boolean noJump = true;
    public boolean noShift = true;
    public boolean noF3 = true;
    public boolean noNaturalRegen = true;
    public boolean foodHalved = true;
    public boolean negativeEffectsAtNight = true;
    public boolean increasedSpawnRate = true;
    public boolean increasedMobDamage = true;
    public boolean doubleMobHealth = true;
    public boolean endermanAlwaysHostile = true;
    public boolean hostileWolves = true;
    public boolean highArmorChance = true;
    public boolean increasedWeatherIntensity = true;
    public boolean blockUtilityCrafting = true;
    public boolean globalExplosionDropRate = true;
    public boolean noVillagerTrading = true;
    public boolean noArmor = true;
    public boolean versionLock10 = true;

    public U2HCConfig copy() {
        U2HCConfig copy = new U2HCConfig();
        copy.noSprint = this.noSprint;
        copy.noLeftClick = this.noLeftClick;
        copy.noJump = this.noJump;
        copy.noShift = this.noShift;
        copy.noF3 = this.noF3;
        copy.noNaturalRegen = this.noNaturalRegen;
        copy.foodHalved = this.foodHalved;
        copy.negativeEffectsAtNight = this.negativeEffectsAtNight;
        copy.increasedSpawnRate = this.increasedSpawnRate;
        copy.increasedMobDamage = this.increasedMobDamage;
        copy.doubleMobHealth = this.doubleMobHealth;
        copy.endermanAlwaysHostile = this.endermanAlwaysHostile;
        copy.hostileWolves = this.hostileWolves;
        copy.highArmorChance = this.highArmorChance;
        copy.increasedWeatherIntensity = this.increasedWeatherIntensity;
        copy.blockUtilityCrafting = this.blockUtilityCrafting;
        copy.globalExplosionDropRate = this.globalExplosionDropRate;
        copy.noVillagerTrading = this.noVillagerTrading;
        copy.noArmor = this.noArmor;
        copy.versionLock10 = this.versionLock10;
        return copy;
    }
}