package dk.acidglow.civicraft.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public class WalletButton extends ImageButton {
    private static final Component LABEL = Component.translatable("screen.civicraft.wallet");
    private static final Identifier ICON_TEXTURE = Identifier.fromNamespaceAndPath("civicraft", "textures/gui/coin_pouch.png");
    private static final int ICON_TEXTURE_WIDTH = 898;
    private static final int ICON_TEXTURE_HEIGHT = 888;

    public WalletButton(int x, int y, Button.OnPress onPress) {
        super(x, y, 20, 18, SPRITES, onPress, LABEL);
        this.setTooltip(Tooltip.create(Component.translatable("tooltip.civicraft.wallet_button")));
    }

    @Override
    public void renderContents(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderDefaultSprite(guiGraphics);
        guiGraphics.blit(
                RenderPipelines.GUI_TEXTURED,
                ICON_TEXTURE,
                this.getX() + 2,
                this.getY() + 1,
                0.0F,
                0.0F,
                16,
                16,
                ICON_TEXTURE_WIDTH,
                ICON_TEXTURE_HEIGHT,
                ICON_TEXTURE_WIDTH,
                ICON_TEXTURE_HEIGHT,
                -1);
    }
}
