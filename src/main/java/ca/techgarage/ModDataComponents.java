package ca.techgarage;

import ca.techgarage.spells.MagicEssenceData;
import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.Identifier;

public class ModDataComponents {

    public static final DataComponentType<MagicEssenceData> MAGIC_ESSENCE_DATA =
            Registry.register(
                    BuiltInRegistries.DATA_COMPONENT_TYPE,
                    Identifier.fromNamespaceAndPath(BotanicMagic.MOD_ID, "magic_essence_data"),
                    DataComponentType.<MagicEssenceData>builder()
                            .persistent(MagicEssenceData.CODEC)
                            .networkSynchronized(MagicEssenceData.STREAM_CODEC)
                            .build()
            );
    public static final DataComponentType<Integer> SPELL_XP_COST =
            Registry.register(
                    BuiltInRegistries.DATA_COMPONENT_TYPE,
                    Identifier.fromNamespaceAndPath(BotanicMagic.MOD_ID, "spell_xp_cost"),
                    DataComponentType.<Integer>builder()
                            .persistent(Codec.INT)
                            .networkSynchronized(ByteBufCodecs.INT)
                            .build()
            );
    public static void register() {
        BotanicMagic.LOGGER.info("[Botanic Magic] Data components initialized");
    }
}