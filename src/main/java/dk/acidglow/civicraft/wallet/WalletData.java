package dk.acidglow.civicraft.wallet;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dk.acidglow.civicraft.CoinDenomination;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record WalletData(int gold, int silver, int copper) {
    private static final int COPPER_PER_SILVER = 20;
    private static final int SILVER_PER_GOLD = 20;
    private static final int COPPER_PER_GOLD = COPPER_PER_SILVER * SILVER_PER_GOLD;

    public static final WalletData EMPTY = new WalletData(0, 0, 0);

    public static final Codec<WalletData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("gold").forGetter(WalletData::gold),
            Codec.INT.fieldOf("silver").forGetter(WalletData::silver),
            Codec.INT.fieldOf("copper").forGetter(WalletData::copper)
    ).apply(instance, WalletData::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, WalletData> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT,
            WalletData::gold,
            ByteBufCodecs.VAR_INT,
            WalletData::silver,
            ByteBufCodecs.VAR_INT,
            WalletData::copper,
            WalletData::new
    );

    public static WalletData empty() {
        return EMPTY;
    }

    public int count(CoinDenomination denomination) {
        return switch (denomination) {
            case GOLD -> gold;
            case SILVER -> silver;
            case COPPER -> copper;
        };
    }

    public int totalValueInCopper() {
        return gold * COPPER_PER_GOLD + silver * COPPER_PER_SILVER + copper;
    }

    public int maxWithdrawable(CoinDenomination denomination) {
        return this.totalValueInCopper() / valueInCopper(denomination);
    }

    public WalletData deposit(CoinDenomination denomination, int amount) {
        if (amount <= 0) {
            return this;
        }
        return fromTotalCopperValue(this.totalValueInCopper() + valueInCopper(denomination) * amount);
    }

    public WalletData withdraw(CoinDenomination denomination, int amount) {
        int withdrawalValue = valueInCopper(denomination) * amount;
        if (amount <= 0 || this.totalValueInCopper() < withdrawalValue) {
            return this;
        }
        return fromTotalCopperValue(this.totalValueInCopper() - withdrawalValue);
    }

    private static WalletData fromTotalCopperValue(int totalCopperValue) {
        if (totalCopperValue <= 0) {
            return EMPTY;
        }

        int gold = totalCopperValue / COPPER_PER_GOLD;
        int remainder = totalCopperValue % COPPER_PER_GOLD;
        int silver = remainder / COPPER_PER_SILVER;
        int copper = remainder % COPPER_PER_SILVER;
        return new WalletData(gold, silver, copper);
    }

    private static int valueInCopper(CoinDenomination denomination) {
        return switch (denomination) {
            case GOLD -> COPPER_PER_GOLD;
            case SILVER -> COPPER_PER_SILVER;
            case COPPER -> 1;
        };
    }
}
