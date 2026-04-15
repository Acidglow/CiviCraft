package dk.acidglow.civicraft;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import dk.acidglow.civicraft.network.CiviCraftNetworking;
import dk.acidglow.civicraft.wallet.WalletAttachments;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

@Mod(CiviCraft.MODID)
public class CiviCraft {
    public static final String MODID = "civicraft";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final int DEFAULT_COPPER_COIN_VALUE = 1;
    public static final int DEFAULT_SILVER_COIN_VALUE = 20 * DEFAULT_COPPER_COIN_VALUE;
    public static final int DEFAULT_GOLD_COIN_VALUE = 20 * DEFAULT_SILVER_COIN_VALUE;

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(net.minecraft.core.registries.Registries.CREATIVE_MODE_TAB, MODID);

    public static final DeferredItem<CoinItem> COPPER_COIN = ITEMS.registerItem(
            "copper_coin",
            properties -> new CoinItem(properties, Config::getCopperCoinValue));
    public static final DeferredItem<CoinItem> SILVER_COIN = ITEMS.registerItem(
            "silver_coin",
            properties -> new CoinItem(properties, Config::getSilverCoinValue));
    public static final DeferredItem<CoinItem> GOLD_COIN = ITEMS.registerItem(
            "gold_coin",
            properties -> new CoinItem(properties, Config::getGoldCoinValue));

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> CIVICRAFT_TAB = CREATIVE_MODE_TABS.register(
            "civicraft",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.civicraft"))
                    .withTabsBefore(CreativeModeTabs.INGREDIENTS)
                    .icon(() -> GOLD_COIN.get().getDefaultInstance())
                    .displayItems((parameters, output) -> {
                        output.accept(COPPER_COIN.get());
                        output.accept(SILVER_COIN.get());
                        output.accept(GOLD_COIN.get());
                    })
                    .build());

    public CiviCraft(IEventBus modEventBus, ModContainer modContainer) {
        ITEMS.register(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);
        WalletAttachments.register(modEventBus);

        modEventBus.addListener(this::addCreative);
        modEventBus.addListener(CiviCraftNetworking::registerPayloadHandlers);

        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.INGREDIENTS) {
            event.accept(COPPER_COIN);
            event.accept(SILVER_COIN);
            event.accept(GOLD_COIN);
        }
    }
}
