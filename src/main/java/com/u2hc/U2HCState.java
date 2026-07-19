package com.u2hc;

import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

public class U2HCState {
    public static final ThreadLocal<Boolean> IS_EXPLODING = ThreadLocal.withInitial(() -> false);
    public static ResourceKey<Level> currentDimension;
    public static long lastDayApplied = -1;
    public static int deathCount = 0;

    public static long frozenTicks = -1;
    public static int frozenDeaths = -1;
    public static int fireworkTimer = 0;

    public static void reset() {
        fireworkTimer = 0;
        frozenTicks = -1;
        frozenDeaths = -1;
        lastDayApplied = -1;
    }
}