package com.u2hc;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record U2HCPayload(long ticks, int deathCount) implements CustomPacketPayload {
    public static final Type<U2HCPayload> ID = new Type<>(ResourceLocation.fromNamespaceAndPath("u2hc", "win_sync"));

    public static final StreamCodec<FriendlyByteBuf, U2HCPayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_LONG, U2HCPayload::ticks,
            ByteBufCodecs.VAR_INT, U2HCPayload::deathCount,
            U2HCPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}