package ca.techgarage.blocks.flowers;

import ca.techgarage.blocks.GrowableFlowerBlock;
import ca.techgarage.spells.FlowerExtractionData;
import ca.techgarage.spells.IExtractableFlower;
import ca.techgarage.spells.MagicShape;
import ca.techgarage.spells.SpellElement;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.component.SuspiciousStewEffects;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class Begoneya extends GrowableFlowerBlock implements IExtractableFlower {
    private boolean fullygrown = false;
    public Begoneya(Properties properties) {
        super(SuspiciousStewEffects.EMPTY, properties, 3, 0.10f);
    }

    @Override
    protected void onFullyGrown(Level world, BlockPos pos, BlockState state) {
        fullygrown = true;
    }
    private static final FlowerExtractionData EXTRACTION_DATA = new FlowerExtractionData(
            0.08f,  // 8% - Dark element
            0.20f,  // 20% - shape
            0.25f,  // 35% - husk
            // Remaining 47% - FAILED
            List.of(new FlowerExtractionData.WeightedEntry<>(SpellElement.WIND, 1.0f)),
            List.of(
                    new FlowerExtractionData.WeightedEntry<>(MagicShape.PROJECTILE, 2.0f),
                    new FlowerExtractionData.WeightedEntry<>(MagicShape.CONE,       1.5f),
                    new FlowerExtractionData.WeightedEntry<>(MagicShape.AOE,        1.0f),
                    new FlowerExtractionData.WeightedEntry<>(MagicShape.COLUMN,     1.0f),
                    new FlowerExtractionData.WeightedEntry<>(MagicShape.BUFF_SELF,  0.5f)
            )
    );
    public void animateTick(final BlockState state, final Level level, final BlockPos pos, final RandomSource random) {
        if (!fullygrown) return;

        double fireflyX = (double)pos.getX() + random.nextDouble() * (double)10.0F - (double)5.0F;
        double fireflyY = (double)pos.getY() + random.nextDouble() * (double)5.0F;
        double fireflyZ = (double)pos.getZ() + random.nextDouble() * (double)10.0F - (double)5.0F;
        level.addParticle(ParticleTypes.FIREFLY, fireflyX, fireflyY, fireflyZ, (double)0.0F, (double)0.0F, (double)0.0F);


    }
    @Override
    public FlowerExtractionData getExtractionData() {
        return EXTRACTION_DATA;
    }
}