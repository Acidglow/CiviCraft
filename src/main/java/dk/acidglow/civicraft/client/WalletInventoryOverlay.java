package dk.acidglow.civicraft.client;

import com.mojang.blaze3d.platform.InputConstants;
import dk.acidglow.civicraft.CoinDenomination;
import dk.acidglow.civicraft.network.DepositCoinPayload;
import dk.acidglow.civicraft.network.WithdrawCoinPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.world.inventory.Slot;
import net.neoforged.neoforge.client.event.ContainerScreenEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;

public final class WalletInventoryOverlay {
    private static final int BUTTON_WIDTH = 20;
    private static final int BUTTON_HEIGHT = 18;
    private static final WalletButton BUTTON = new WalletButton(0, 0, ignored -> {});
    private static final WalletOverlay OVERLAY = new WalletOverlay();

    private static boolean walletOpen;

    private WalletInventoryOverlay() {
    }

    public static void onScreenOpening(ScreenEvent.Opening event) {
        walletOpen = false;
    }

    public static void onRender(ContainerScreenEvent.Render.Foreground event) {
        OverlayContext context = overlayContext(event.getContainerScreen());
        if (context == null) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        int screenWidth = minecraft.getWindow().getGuiScaledWidth();
        int screenHeight = minecraft.getWindow().getGuiScaledHeight();
        event.getGuiGraphics().pose().pushMatrix();
        event.getGuiGraphics().pose().translate(-context.screen().getGuiLeft(), -context.screen().getGuiTop());
        event.getGuiGraphics().enableScissor(0, 0, screenWidth, screenHeight);
        BUTTON.visible = true;
        BUTTON.active = true;
        BUTTON.setPosition(context.buttonX(), context.buttonY());
        BUTTON.render(event.getGuiGraphics(), event.getMouseX(), event.getMouseY(), 0.0F);

        if (walletOpen) {
            OVERLAY.render(
                    event.getGuiGraphics(),
                    minecraft.font,
                    context.popupCenterX(),
                    context.popupCenterY(),
                    WalletBalanceFormatter.getWalletData(minecraft.player),
                    context.popupYOffset());
        }
        event.getGuiGraphics().disableScissor();
        event.getGuiGraphics().pose().popMatrix();
    }

    public static void onMousePressed(ScreenEvent.MouseButtonPressed.Pre event) {
        OverlayContext context = overlayContext(event.getScreen());
        if (context == null) {
            return;
        }

        MouseButtonEvent mouseEvent = event.getMouseButtonEvent();
        if (isInsideButton(mouseEvent.x(), mouseEvent.y(), context)) {
            if (mouseEvent.button() == 0) {
                walletOpen = !walletOpen;
                event.setCanceled(true);
            }
            return;
        }

        if (!walletOpen) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        if (mouseEvent.button() == 1 && mouseEvent.hasShiftDown()) {
            Slot slot = context.screen().getSlotUnderMouse();
            int depositSlotIndex = resolveDepositSlotIndex(slot);
            if (slot != null && depositSlotIndex >= 0 && slot.hasItem() && CoinDenomination.fromItem(slot.getItem().getItem()) != null && minecraft.getConnection() != null) {
                minecraft.getConnection().send(new DepositCoinPayload(depositSlotIndex).toVanillaServerbound());
                event.setCanceled(true);
                return;
            }
        }

        if (OVERLAY.contains(context.popupCenterX(), context.popupCenterY(), context.popupYOffset(), mouseEvent.x(), mouseEvent.y())) {
            if (mouseEvent.button() == 0 && minecraft.getConnection() != null) {
                CoinDenomination denomination = OVERLAY.denominationAt(
                        minecraft.font,
                        context.popupCenterX(),
                        context.popupCenterY(),
                        WalletBalanceFormatter.getWalletData(minecraft.player),
                        context.popupYOffset(),
                        mouseEvent.x(),
                        mouseEvent.y());
                if (denomination != null) {
                    minecraft.getConnection().send(new WithdrawCoinPayload(denomination.ordinal(), mouseEvent.hasShiftDown()).toVanillaServerbound());
                }
            }
            event.setCanceled(true);
        }
    }

    public static void onMouseReleased(ScreenEvent.MouseButtonReleased.Pre event) {
        OverlayContext context = overlayContext(event.getScreen());
        if (context == null) {
            return;
        }

        MouseButtonEvent mouseEvent = event.getMouseButtonEvent();
        if (isInsideButton(mouseEvent.x(), mouseEvent.y(), context)
                || (walletOpen && OVERLAY.contains(context.popupCenterX(), context.popupCenterY(), context.popupYOffset(), mouseEvent.x(), mouseEvent.y()))) {
            event.setCanceled(true);
        }
    }

    public static void onMouseDragged(ScreenEvent.MouseDragged.Pre event) {
        OverlayContext context = overlayContext(event.getScreen());
        if (context == null || !walletOpen) {
            return;
        }

        MouseButtonEvent mouseEvent = event.getMouseButtonEvent();
        if (OVERLAY.contains(context.popupCenterX(), context.popupCenterY(), context.popupYOffset(), mouseEvent.x(), mouseEvent.y())) {
            event.setCanceled(true);
        }
    }

    public static void onKeyPressed(ScreenEvent.KeyPressed.Pre event) {
        if (!walletOpen || overlayContext(event.getScreen()) == null) {
            return;
        }

        KeyEvent keyEvent = event.getKeyEvent();
        if (keyEvent.key() == InputConstants.KEY_ESCAPE) {
            walletOpen = false;
            event.setCanceled(true);
        }
    }

    private static boolean isInsideButton(double mouseX, double mouseY, OverlayContext context) {
        return mouseX >= context.buttonX()
                && mouseX < context.buttonX() + BUTTON_WIDTH
                && mouseY >= context.buttonY()
                && mouseY < context.buttonY() + BUTTON_HEIGHT;
    }

    private static int resolveDepositSlotIndex(Slot slot) {
        if (slot == null) {
            return -1;
        }
        if (slot.getClass().getName().endsWith("CreativeModeInventoryScreen$SlotWrapper")) {
            try {
                var field = slot.getClass().getDeclaredField("target");
                field.setAccessible(true);
                Object target = field.get(slot);
                if (target instanceof Slot targetSlot) {
                    return targetSlot.index;
                }
            } catch (ReflectiveOperationException ignored) {
            }
        }
        return slot.index;
    }

    private static OverlayContext overlayContext(Screen screen) {
        if (screen instanceof InventoryScreen inventoryScreen) {
            return new OverlayContext(
                    inventoryScreen,
                    inventoryScreen.getGuiLeft() + 148,
                    inventoryScreen.getGuiTop() + 61,
                    inventoryScreen.getGuiLeft() + inventoryScreen.getXSize() / 2,
                    inventoryScreen.getGuiTop() + inventoryScreen.getYSize() / 2,
                    0);
        }
        if (screen instanceof CreativeModeInventoryScreen creativeScreen && creativeScreen.isInventoryOpen()) {
            return new OverlayContext(
                    creativeScreen,
                    creativeScreen.getGuiLeft() + 148,
                    creativeScreen.getGuiTop() + 31,
                    creativeScreen.getGuiLeft() + creativeScreen.getXSize() / 2,
                    creativeScreen.getGuiTop() + creativeScreen.getYSize() / 2,
                    -20);
        }
        return null;
    }

    private record OverlayContext(
            AbstractContainerScreen<?> screen,
            int buttonX,
            int buttonY,
            int popupCenterX,
            int popupCenterY,
            int popupYOffset
    ) {
    }
}
