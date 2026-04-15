package dk.acidglow.civicraft;

import java.util.function.Consumer;
import java.util.function.IntSupplier;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

public class CoinItem extends Item {
    private final IntSupplier valueSupplier;

    public CoinItem(Properties properties, IntSupplier valueSupplier) {
        super(properties);
        this.valueSupplier = valueSupplier;
    }

    public int getValueInCopper() {
        return valueSupplier.getAsInt();
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> tooltipAdder, TooltipFlag tooltipFlag) {
        tooltipAdder.accept(Component.translatable("tooltip.civicraft.coin_value", getValueInCopper()).withStyle(ChatFormatting.GRAY));
    }
}
