package ca.techgarage.spells;

import ca.techgarage.spells.ExtractionResult;
import ca.techgarage.spells.MagicShape;
import ca.techgarage.spells.SpellElement;
import net.minecraft.util.RandomSource;
import java.util.List;
import java.util.Optional;

public class FlowerExtractionData {

    private final float elementChance;
    private final float shapeChance;
    private final float huskChance;
    private final List<WeightedEntry<SpellElement>> elements;
    private final List<WeightedEntry<MagicShape>> shapes;

    public FlowerExtractionData(float elementChance, float shapeChance, float huskChance,
                                List<WeightedEntry<SpellElement>> elements,
                                List<WeightedEntry<MagicShape>> shapes) {
        this.elementChance = elementChance;
        this.shapeChance = shapeChance;
        this.huskChance = huskChance;
        this.elements = elements;
        this.shapes = shapes;
    }

    public ExtractionOutcome roll(RandomSource random) {
        float roll = random.nextFloat();

        if (roll < elementChance) {
            return new ExtractionOutcome(ExtractionResult.ELEMENT, rollWeighted(elements, random), null);
        }
        roll -= elementChance;

        if (roll < shapeChance) {
            return new ExtractionOutcome(ExtractionResult.SHAPE, null, rollWeighted(shapes, random));
        }
        roll -= shapeChance;

        if (roll < huskChance) {
            return new ExtractionOutcome(ExtractionResult.HUSK, null, null);
        }

        return new ExtractionOutcome(ExtractionResult.FAILED, null, null);
    }



    private <T> T rollWeighted(List<WeightedEntry<T>> pool, RandomSource random) {
        float total = 0;
        for (WeightedEntry<T> entry : pool) total += entry.weight();
        float roll = random.nextFloat() * total;
        for (WeightedEntry<T> entry : pool) {
            roll -= entry.weight();
            if (roll <= 0) return entry.value();
        }
        return pool.get(pool.size() - 1).value();
    }

    public record WeightedEntry<T>(T value, float weight) {}

    public record ExtractionOutcome(ExtractionResult result, SpellElement element, MagicShape shape) {}
}