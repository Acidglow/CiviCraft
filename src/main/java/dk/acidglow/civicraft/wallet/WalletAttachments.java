package dk.acidglow.civicraft.wallet;

import dk.acidglow.civicraft.CiviCraft;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public final class WalletAttachments {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENTS = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, CiviCraft.MODID);

    public static final java.util.function.Supplier<AttachmentType<WalletData>> WALLET = ATTACHMENTS.register(
            "wallet",
            () -> AttachmentType.builder(WalletData::empty)
                    .serialize(WalletData.CODEC.fieldOf("wallet"))
                    .sync((holder, player) -> holder == player, WalletData.STREAM_CODEC)
                    .copyOnDeath()
                    .build());

    private WalletAttachments() {
    }

    public static void register(IEventBus modEventBus) {
        ATTACHMENTS.register(modEventBus);
    }
}
