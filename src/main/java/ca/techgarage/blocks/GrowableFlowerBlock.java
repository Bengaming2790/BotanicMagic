package ca.techgarage.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.FlowerBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.item.component.SuspiciousStewEffects;

public abstract class GrowableFlowerBlock extends FlowerBlock {

    protected final int maxAge;
    protected final float growthChance;

    public static final IntegerProperty AGE = IntegerProperty.create("age", 0, 3);
    protected GrowableFlowerBlock(
            SuspiciousStewEffects effects,
            BlockBehaviour.Properties properties,
            int maxAge,
            float growthChance
    ) {
        super(effects, properties);

        this.maxAge = maxAge;
        this.growthChance = growthChance;

        this.registerDefaultState(this.stateDefinition.any().setValue(AGE, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AGE);
    }


    @Override
    public void randomTick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
        int age = state.getValue(AGE);

        if (age < maxAge && random.nextFloat() < growthChance) {
            int newAge = age + 1;
            world.setBlock(pos, state.setValue(AGE, newAge), 2);

            onGrow(world, pos, state, newAge);

            if (newAge == maxAge) {
                onFullyGrown(world, pos, state);
            }
        }
    }

    protected void onGrow(Level world, BlockPos pos, BlockState state, int newAge) {}

    protected void onFullyGrown(Level world, BlockPos pos, BlockState state) {}

    @Override
    public boolean isRandomlyTicking(BlockState state) {
        return state.getValue(AGE) < maxAge;
    }
}