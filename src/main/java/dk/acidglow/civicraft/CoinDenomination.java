package dk.acidglow.civicraft;

import net.minecraft.world.item.Item;

public enum CoinDenomination {
    GOLD("gold"),
    SILVER("silver"),
    COPPER("copper");

    private final String id;

    CoinDenomination(String id) {
        this.id = id;
    }

    public String id() {
        return id;
    }

    public Item item() {
        return switch (this) {
            case GOLD -> CiviCraft.GOLD_COIN.get();
            case SILVER -> CiviCraft.SILVER_COIN.get();
            case COPPER -> CiviCraft.COPPER_COIN.get();
        };
    }

    public static CoinDenomination fromItem(Item item) {
        if (item == CiviCraft.GOLD_COIN.get()) {
            return GOLD;
        }
        if (item == CiviCraft.SILVER_COIN.get()) {
            return SILVER;
        }
        if (item == CiviCraft.COPPER_COIN.get()) {
            return COPPER;
        }
        return null;
    }

    public static CoinDenomination fromOrdinal(int ordinal) {
        if (ordinal < 0 || ordinal >= values().length) {
            return null;
        }
        return values()[ordinal];
    }
}
