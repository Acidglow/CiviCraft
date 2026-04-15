package dk.acidglow.civicraft.network;

import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

public final class CiviCraftNetworking {
    private CiviCraftNetworking() {
    }

    public static void registerPayloadHandlers(RegisterPayloadHandlersEvent event) {
        var registrar = event.registrar("2");
        registrar.playToServer(DepositCoinPayload.TYPE, DepositCoinPayload.STREAM_CODEC, WalletPayloadHandler::handleDeposit);
        registrar.playToServer(WithdrawCoinPayload.TYPE, WithdrawCoinPayload.STREAM_CODEC, WalletPayloadHandler::handleWithdraw);
    }
}
