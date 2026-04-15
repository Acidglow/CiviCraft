package dk.acidglow.civicraft.network;

import dk.acidglow.civicraft.CoinDenomination;
import dk.acidglow.civicraft.wallet.WalletAttachments;
import dk.acidglow.civicraft.wallet.WalletData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public final class WalletPayloadHandler {
    private WalletPayloadHandler() {
    }

    public static void handleDeposit(DepositCoinPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer player)) {
                return;
            }
            InventoryMenu inventoryMenu = player.inventoryMenu;
            if (payload.slotIndex() < 0 || payload.slotIndex() >= inventoryMenu.slots.size()) {
                return;
            }

            Slot slot = inventoryMenu.getSlot(payload.slotIndex());
            ItemStack stack = slot.getItem();
            CoinDenomination denomination = CoinDenomination.fromItem(stack.getItem());
            if (denomination == null || stack.isEmpty() || !slot.mayPickup(player)) {
                return;
            }

            ItemStack removed = slot.remove(stack.getCount());
            slot.onTake(player, removed);
            inventoryMenu.broadcastChanges();
            if (player.containerMenu != inventoryMenu) {
                player.containerMenu.broadcastChanges();
            }

            WalletData walletData = player.getData(WalletAttachments.WALLET);
            player.setData(WalletAttachments.WALLET, walletData.deposit(denomination, removed.getCount()));
        });
    }

    public static void handleWithdraw(WithdrawCoinPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer player)) {
                return;
            }

            CoinDenomination denomination = CoinDenomination.fromOrdinal(payload.denominationOrdinal());
            if (denomination == null) {
                return;
            }

            WalletData walletData = player.getData(WalletAttachments.WALLET);
            int amount = payload.withdrawStack()
                    ? Math.min(walletData.maxWithdrawable(denomination), denomination.item().getDefaultInstance().getMaxStackSize())
                    : 1;
            if (amount <= 0) {
                return;
            }

            ItemStack stack = new ItemStack(denomination.item(), amount);
            player.getInventory().add(stack);
            int inserted = amount - stack.getCount();
            if (inserted <= 0) {
                return;
            }

            player.setData(WalletAttachments.WALLET, walletData.withdraw(denomination, inserted));
            player.containerMenu.broadcastChanges();
        });
    }
}
