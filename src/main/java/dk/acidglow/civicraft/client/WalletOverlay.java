package dk.acidglow.civicraft.client;

import dk.acidglow.civicraft.CoinDenomination;
import dk.acidglow.civicraft.wallet.WalletData;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public final class WalletOverlay {
    private static final int PANEL_WIDTH = 135;
    private static final int PANEL_HEIGHT = 72;
    private static final Identifier BACKGROUND_TEXTURE = Identifier.fromNamespaceAndPath("civicraft", "textures/gui/coin_pouch_open.png");
    private static final int BACKGROUND_TEXTURE_WIDTH = 723;
    private static final int BACKGROUND_TEXTURE_HEIGHT = 621;
    private static final int GOLD_COLOR = 0xFFFFD700;
    private static final int SILVER_COLOR = 0xFFD8D8D8;
    private static final int COPPER_COLOR = 0xFFC87533;
    private static final int TEXT_BACKGROUND_COLOR = 0xB0101010;

    public void render(GuiGraphics guiGraphics, Font font, int popupCenterX, int popupCenterY, WalletData walletData, int popupYOffset) {
        Bounds bounds = this.panelBounds(popupCenterX, popupCenterY, popupYOffset);
        guiGraphics.blit(
                RenderPipelines.GUI_TEXTURED,
                BACKGROUND_TEXTURE,
                bounds.left(),
                bounds.top(),
                0.0F,
                0.0F,
                PANEL_WIDTH,
                PANEL_HEIGHT,
                BACKGROUND_TEXTURE_WIDTH,
                BACKGROUND_TEXTURE_HEIGHT,
                BACKGROUND_TEXTURE_WIDTH,
                BACKGROUND_TEXTURE_HEIGHT,
                -1);

        SegmentLayout layout = this.segmentLayout(font, popupCenterX, popupCenterY, walletData, popupYOffset);
        guiGraphics.fill(layout.backgroundLeft(), layout.backgroundTop(), layout.backgroundRight(), layout.backgroundBottom(), TEXT_BACKGROUND_COLOR);
        guiGraphics.drawString(font, Component.literal(layout.goldText()).withStyle(ChatFormatting.BOLD), layout.goldX(), layout.textY(), GOLD_COLOR, false);
        guiGraphics.drawString(font, Component.literal(layout.silverText()).withStyle(ChatFormatting.BOLD), layout.silverX(), layout.textY(), SILVER_COLOR, false);
        guiGraphics.drawString(font, Component.literal(layout.copperText()).withStyle(ChatFormatting.BOLD), layout.copperX(), layout.textY(), COPPER_COLOR, false);
    }

    public CoinDenomination denominationAt(Font font, int popupCenterX, int popupCenterY, WalletData walletData, int popupYOffset, double mouseX, double mouseY) {
        SegmentLayout layout = this.segmentLayout(font, popupCenterX, popupCenterY, walletData, popupYOffset);
        if (this.isInside(mouseX, mouseY, layout.goldX(), layout.textY(), layout.goldWidth(), font.lineHeight)) {
            return CoinDenomination.GOLD;
        }
        if (this.isInside(mouseX, mouseY, layout.silverX(), layout.textY(), layout.silverWidth(), font.lineHeight)) {
            return CoinDenomination.SILVER;
        }
        if (this.isInside(mouseX, mouseY, layout.copperX(), layout.textY(), layout.copperWidth(), font.lineHeight)) {
            return CoinDenomination.COPPER;
        }
        return null;
    }

    public boolean contains(int popupCenterX, int popupCenterY, int popupYOffset, double mouseX, double mouseY) {
        Bounds bounds = this.panelBounds(popupCenterX, popupCenterY, popupYOffset);
        return this.isInside(mouseX, mouseY, bounds.left(), bounds.top(), PANEL_WIDTH, PANEL_HEIGHT);
    }

    private Bounds panelBounds(int popupCenterX, int popupCenterY, int popupYOffset) {
        int left = popupCenterX - PANEL_WIDTH / 2;
        int top = popupCenterY - PANEL_HEIGHT / 2 - 41 + popupYOffset;
        return new Bounds(left, top, left + PANEL_WIDTH, top + PANEL_HEIGHT);
    }

    private SegmentLayout segmentLayout(Font font, int popupCenterX, int popupCenterY, WalletData walletData, int popupYOffset) {
        Bounds bounds = this.panelBounds(popupCenterX, popupCenterY, popupYOffset);
        String goldText = walletData.gold() + "g";
        String silverText = walletData.silver() + "s";
        String copperText = walletData.copper() + "c";

        int goldWidth = font.width(Component.literal(goldText).withStyle(ChatFormatting.BOLD));
        int silverWidth = font.width(Component.literal(silverText).withStyle(ChatFormatting.BOLD));
        int copperWidth = font.width(Component.literal(copperText).withStyle(ChatFormatting.BOLD));
        int totalWidth = goldWidth + silverWidth + copperWidth + font.width("  ");
        int startX = bounds.left() + (PANEL_WIDTH - totalWidth) / 2;
        int textY = bounds.top() + 22;
        int silverX = startX + goldWidth + font.width(" ");
        int copperX = silverX + silverWidth + font.width(" ");
        int backgroundPadding = 3;
        int backgroundLeft = startX - backgroundPadding;
        int backgroundTop = textY - backgroundPadding;
        int backgroundRight = startX + totalWidth + backgroundPadding;
        int backgroundBottom = textY + font.lineHeight + backgroundPadding;

        return new SegmentLayout(
                goldText,
                silverText,
                copperText,
                startX,
                silverX,
                copperX,
                goldWidth,
                silverWidth,
                copperWidth,
                textY,
                backgroundLeft,
                backgroundTop,
                backgroundRight,
                backgroundBottom);
    }

    private boolean isInside(double mouseX, double mouseY, int x, int y, int width, int height) {
        return mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
    }

    private record Bounds(int left, int top, int right, int bottom) {
    }

    private record SegmentLayout(
            String goldText,
            String silverText,
            String copperText,
            int goldX,
            int silverX,
            int copperX,
            int goldWidth,
            int silverWidth,
            int copperWidth,
            int textY,
            int backgroundLeft,
            int backgroundTop,
            int backgroundRight,
            int backgroundBottom
    ) {
    }
}
