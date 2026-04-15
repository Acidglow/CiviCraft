package dk.acidglow.civicraft.network;

import dk.acidglow.civicraft.CiviCraft;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record DepositCoinPayload(int slotIndex) implements CustomPacketPayload {
    public static final Type<DepositCoinPayload> TYPE = new Type<>(Identifier.fromNamespaceAndPath(CiviCraft.MODID, "deposit_coin"));
    public static final StreamCodec<net.minecraft.network.RegistryFriendlyByteBuf, DepositCoinPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT,
            DepositCoinPayload::slotIndex,
            DepositCoinPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
