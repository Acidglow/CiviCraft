package dk.acidglow.civicraft;

import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import dk.acidglow.civicraft.client.WalletInventoryOverlay;

// This class will not load on dedicated servers. Accessing client side code from here is safe.
@Mod(value = CiviCraft.MODID, dist = Dist.CLIENT)
// You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
@EventBusSubscriber(modid = CiviCraft.MODID, value = Dist.CLIENT)
public class CiviCraftClient {
    public CiviCraftClient(ModContainer container) {
        // Allows NeoForge to create a config screen for this mod's configs.
        // The config screen is accessed by going to the Mods screen > clicking on your mod > clicking on config.
        // Do not forget to add translations for your config options to the en_us.json file.
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
        NeoForge.EVENT_BUS.addListener(WalletInventoryOverlay::onScreenOpening);
        NeoForge.EVENT_BUS.addListener(WalletInventoryOverlay::onRender);
        NeoForge.EVENT_BUS.addListener(WalletInventoryOverlay::onMousePressed);
        NeoForge.EVENT_BUS.addListener(WalletInventoryOverlay::onMouseReleased);
        NeoForge.EVENT_BUS.addListener(WalletInventoryOverlay::onMouseDragged);
        NeoForge.EVENT_BUS.addListener(WalletInventoryOverlay::onKeyPressed);
    }

    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {
        // Some client setup code
        CiviCraft.LOGGER.info("HELLO FROM CLIENT SETUP");
        CiviCraft.LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
    }
}
