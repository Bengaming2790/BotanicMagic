package ca.techgarage.spells;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.ByteBufCodecs;

import java.util.Optional;

public record MagicEssenceData(
        Optional<SpellElement> element,
        Optional<MagicShape> shape
) {
    public static final Codec<MagicEssenceData> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.STRING.xmap(SpellElement::valueOf, SpellElement::name)
                            .optionalFieldOf("element").forGetter(MagicEssenceData::element),
                    Codec.STRING.xmap(MagicShape::valueOf, MagicShape::name)
                            .optionalFieldOf("shape").forGetter(MagicEssenceData::shape)
            ).apply(instance, MagicEssenceData::new)
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, MagicEssenceData> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.optional(ByteBufCodecs.STRING_UTF8
                            .map(SpellElement::valueOf, SpellElement::name)),
                    MagicEssenceData::element,
                    ByteBufCodecs.optional(ByteBufCodecs.STRING_UTF8
                            .map(MagicShape::valueOf, MagicShape::name)),
                    MagicEssenceData::shape,
                    MagicEssenceData::new
            );
}