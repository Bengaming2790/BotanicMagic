package ca.techgarage.blocks.flowers;

import ca.techgarage.blocks.GrowableFlowerBlock;
import ca.techgarage.spells.FlowerExtractionData;
import ca.techgarage.spells.IExtractableFlower;
import ca.techgarage.spells.MagicShape;
import ca.techgarage.spells.SpellElement;
import net.minecraft.world.item.component.SuspiciousStewEffects;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.List;

public class Sparkspur extends GrowableFlowerBlock implements IExtractableFlower {
    public Sparkspur(BlockBehaviour.Properties properties) {
        super(SuspiciousStewEffects.EMPTY, properties, 3, 0.30f);
    }

    private static final FlowerExtractionData EXTRACTION_DATA = new FlowerExtractionData(
            0.25f,  // 25% - SPARK element
            0.25f,  // 25% - shape
            0.20f,  // 20% - husk
            // Remaining 30% - FAILED
            List.of(new FlowerExtractionData.WeightedEntry<>(SpellElement.SPARK, 1.0f),
                    new FlowerExtractionData.WeightedEntry<>(SpellElement.LIGHT, 0.01f)),
            List.of(
                    new FlowerExtractionData.WeightedEntry<>(MagicShape.PROJECTILE, 1.0f),
                    new FlowerExtractionData.WeightedEntry<>(MagicShape.CONE,       0.5f),
                    new FlowerExtractionData.WeightedEntry<>(MagicShape.AOE,        1.25f),
                    new FlowerExtractionData.WeightedEntry<>(MagicShape.COLUMN,     2.0f),
                    new FlowerExtractionData.WeightedEntry<>(MagicShape.BUFF_SELF,  1.0f)
            )
    );
    @Override
    public FlowerExtractionData getExtractionData() {
        return EXTRACTION_DATA;
    }
}
