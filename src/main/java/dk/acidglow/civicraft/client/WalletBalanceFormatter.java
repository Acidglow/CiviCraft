package dk.acidglow.civicraft.client;

import dk.acidglow.civicraft.CoinDenomination;
import dk.acidglow.civicraft.wallet.WalletAttachments;
import dk.acidglow.civicraft.wallet.WalletData;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;

public final class WalletBalanceFormatter {
    private static final int SILVER_COLOR = 0xD8D8D8;
    private static final int COPPER_COLOR = 0xC87533;

    private WalletBalanceFormatter() {
    }

    public static WalletData getWalletData(Player player) {
        if (player == null) {
            return WalletData.EMPTY;
        }
        return player.getData(WalletAttachments.WALLET);
    }

    public static Component formatBalance(WalletData walletData) {
        MutableComponent component = Component.empty();
        component.append(Component.literal(walletData.count(CoinDenomination.GOLD) + "g").withStyle(ChatFormatting.GOLD));
        component.append(Component.literal(" "));
        component.append(Component.literal(walletData.count(CoinDenomination.SILVER) + "s").withStyle(style -> style.withColor(SILVER_COLOR)));
        component.append(Component.literal(" "));
        component.append(Component.literal(walletData.count(CoinDenomination.COPPER) + "c").withStyle(style -> style.withColor(COPPER_COLOR)));
        return component;
    }
}
