package com.u2hc;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;

public class U2HCClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null || U2HCMod.ACTIVE_CONFIG == null) return;
            if (U2HCMod.ACTIVE_CONFIG.noSprint) {
                unpress(client.options.keySprint);
                client.player.setSprinting(false);
            }
            if (U2HCMod.ACTIVE_CONFIG.noLeftClick) {
                unpress(client.options.keyAttack);
            }
        });

        ClientPlayNetworking.registerGlobalReceiver(U2HCPayload.ID, (payload, context) -> {
            context.client().execute(() -> {
                if (payload.ticks() != -1) {
                    U2HCState.frozenTicks = payload.ticks();
                    U2HCState.frozenDeaths = payload.deathCount();
                    U2HCMod.isComplete = true;

                    context.client().gui.setTitle(net.minecraft.network.chat.Component.literal("You did it...!")
                            .withStyle(net.minecraft.ChatFormatting.YELLOW));
                    context.client().gui.setTimes(10, 70, 20);
                }
           });
        });

        HudRenderCallback.EVENT.register((guiGraphics, tickDelta) -> {
            Minecraft client = Minecraft.getInstance();
            if (client.level == null || client.player == null || client.options.hideGui) return;

            long ticks = U2HCState.frozenTicks != -1 ? U2HCState.frozenTicks : client.level.getGameTime();
            int deathCount = U2HCState.frozenDeaths != -1 ? U2HCState.frozenDeaths : U2HCState.deathCount;

            long totalMs = ticks * 50;
            String timeStr = String.format("%02d:%02d:%02d:%03d", (totalMs/3600000), (totalMs%3600000)/60000, (totalMs%60000)/1000, totalMs%1000);
            Font f = client.font;
            int sw = guiGraphics.guiWidth();

            drawOutlined(guiGraphics, f, "Day: " + (client.level.getDayTime()/24000), 10, 10, 0xFFFF00);
            drawOutlined(guiGraphics, f, "Time: " + timeStr, 10, 22, 0xFFFFFF);

            String dText = "Deaths: " + deathCount;
            drawOutlined(guiGraphics, f, dText, sw - f.width(dText) - 10, 10, 0xFF0000);

            if (U2HCState.fireworkTimer > 0) {
                U2HCState.fireworkTimer--;
            }
        });
    }

    private void unpress(KeyMapping key) {
        if (key != null && key.isDown()) {
            key.setDown(false);
        }
    }

    private void drawOutlined(GuiGraphics g, Font f, String t, int x, int y, int c) {
        g.drawString(f, t, x + 1, y, 0, false);
        g.drawString(f, t, x - 1, y, 0, false);
        g.drawString(f, t, x, y + 1, 0, false);
        g.drawString(f, t, x, y - 1, 0, false);
        g.drawString(f, t, x, y, c, false);
    }
}

