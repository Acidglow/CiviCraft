package dk.acidglow.civicraft.network;

import dk.acidglow.civicraft.CiviCraft;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record WithdrawCoinPayload(int denominationOrdinal, boolean withdrawStack) implements CustomPacketPayload {
    public static final Type<WithdrawCoinPayload> TYPE = new Type<>(Identifier.fromNamespaceAndPath(CiviCraft.MODID, "withdraw_coin"));
    public static final StreamCodec<RegistryFriendlyByteBuf, WithdrawCoinPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT,
            WithdrawCoinPayload::denominationOrdinal,
            ByteBufCodecs.BOOL,
            WithdrawCoinPayload::withdrawStack,
            WithdrawCoinPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
