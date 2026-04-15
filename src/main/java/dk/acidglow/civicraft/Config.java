package dk.acidglow.civicraft;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.IntValue COPPER_COIN_VALUE = BUILDER
            .comment("How much one copper coin is worth. Must be at least 1.")
            .translation("civicraft.configuration.copperCoinValue")
            .defineInRange("copperCoinValue", CiviCraft.DEFAULT_COPPER_COIN_VALUE, 1, Integer.MAX_VALUE);

    public static final ModConfigSpec.IntValue SILVER_COIN_VALUE = BUILDER
            .comment("How much one silver coin is worth in copper coin value.")
            .translation("civicraft.configuration.silverCoinValue")
            .defineInRange("silverCoinValue", CiviCraft.DEFAULT_SILVER_COIN_VALUE, 1, Integer.MAX_VALUE);

    public static final ModConfigSpec.IntValue GOLD_COIN_VALUE = BUILDER
            .comment("How much one gold coin is worth in copper coin value.")
            .translation("civicraft.configuration.goldCoinValue")
            .defineInRange("goldCoinValue", CiviCraft.DEFAULT_GOLD_COIN_VALUE, 1, Integer.MAX_VALUE);

    static final ModConfigSpec SPEC = BUILDER.build();

    private Config() {
    }

    public static int getCopperCoinValue() {
        return COPPER_COIN_VALUE.getAsInt();
    }

    public static int getSilverCoinValue() {
        return SILVER_COIN_VALUE.getAsInt();
    }

    public static int getGoldCoinValue() {
        return GOLD_COIN_VALUE.getAsInt();
    }
}
